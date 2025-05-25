package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final KosService kosService;
    private final TransactionService transactionService;
    private final BookingValidator stateValidator;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private PaymentBookingRepository paymentBookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              KosService kosService,
                              TransactionService transactionService,
                              BookingValidator stateValidator) {
        this.bookingRepository = bookingRepository;
        this.kosService = kosService;
        this.transactionService = transactionService;
        this.stateValidator = stateValidator;
    }

    @Override
    public Booking createBooking(Booking booking) {
        // Validate booking data
        stateValidator.validateBasicFields(booking);

        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));
        booking.setMonthlyPrice(kos.getPrice());

        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        // Save the booking first
        Booking savedBooking = bookingRepository.save(booking);

        // Create PaymentBooking relationship with null paymentId initially
        // This will be updated when payment is made
        PaymentBooking paymentBooking = PaymentBooking.builder()
                .bookingId(savedBooking.getBookingId())
                .paymentId(null) // Will be set when payment is created
                .build();

        paymentBookingRepository.save(paymentBooking);

        return savedBooking;
    }

    @Override
    public Optional<Booking> findBookingById(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public void updateBooking(Booking booking) {
        // Validate booking data
        stateValidator.validateBasicFields(booking);

        Booking existingBooking = bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForUpdate(existingBooking);

        // Preserve the current status
        BookingStatus currentStatus = existingBooking.getStatus();
        booking.setStatus(currentStatus);

        // Update the booking
        bookingRepository.save(booking);
    }

    @Override
    public void payBooking(UUID bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForPayment(booking);

        // Get kos to get owner ID
        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

        // Create payment
        transactionService.createPayment(
                booking.getUserId(),
                kos.getOwnerId(),
                BigDecimal.valueOf(booking.getTotalPrice())
        );

        // Update booking status to PAID
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void approveBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForApproval(booking);

        // Update booking status to APPROVED
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForCancellation(booking);

        // Check if booking is PAID and needs refund
        if (booking.getStatus() == BookingStatus.PAID) {
            try {
                // Find the associated payment
                PaymentBooking paymentBooking = paymentBookingRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new EntityNotFoundException("Payment information not found for this booking"));

                if (paymentBooking.getPaymentId() != null) {
                    // Get the payment to find the owner (who will process the refund)
                    Payment payment = transactionRepository.findPaymentById(paymentBooking.getPaymentId())
                            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

                    // Call refund function with owner's ID as requester
                    UUID ownerId = payment.getOwner().getId();
                    CompletableFuture<Payment> refundFuture = transactionService.refundPayment(
                            paymentBooking.getPaymentId(),
                            ownerId
                    );

                    // Wait for refund to complete
                    Payment refundPayment = refundFuture.get();

                    if (refundPayment.getStatus() == TransactionStatus.COMPLETED) {
                        // Refund successful, set status to CANCELLED
                        booking.setStatus(BookingStatus.CANCELLED);
                        bookingRepository.save(booking);

                        log.info("Booking {} cancelled and refunded successfully", bookingId);
                    } else {
                        throw new RuntimeException("Refund failed with status: " + refundPayment.getStatus());
                    }
                } else {
                    // No payment found, just cancel the booking
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);

                    log.warn("Booking {} cancelled but no payment found to refund", bookingId);
                }
            } catch (Exception e) {
                log.error("Error processing refund for booking {}: {}", bookingId, e.getMessage());
                throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
            }
        } else {
            // Booking is not paid, just cancel normally
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            log.info("Booking {} cancelled (no payment to refund)", bookingId);
        }
    }

    @Override
    public CompletableFuture<List<Booking>> findBookingsByOwnerId(UUID ownerId) {
        return kosService.getAllKos()
            .thenApply(kosList ->
                kosList.stream()
                    .filter(kos -> kos.getOwnerId().equals(ownerId))
                    .collect(Collectors.toList())
            )
            .thenApply(ownerKos -> {
                List<UUID> ownerKosIds = ownerKos.stream()
                    .map(Kos::getId)
                    .toList();

                // Booking repo is synchronous; if needed, make it async too.
                return bookingRepository.findAll().stream()
                    .filter(booking -> ownerKosIds.contains(booking.getKosId()))
                    .collect(Collectors.toList());
            });
    }

    @Override
    public List<Booking> findBookingsByUserId(UUID userId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }


    @Override
    public void clearStore() {
        bookingRepository.deleteAll();
    }
}