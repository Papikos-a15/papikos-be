package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandlerContext;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    private final PaymentBookingRepository paymentBookingRepository;
    private final TransactionRepository transactionRepository;
    private final EventHandlerContext eventHandlerContext;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              KosService kosService,
                              TransactionService transactionService,
                              BookingValidator stateValidator, EventHandlerContext eventHandlerContext,
                              PaymentBookingRepository paymentBookingRepository, TransactionRepository transactionRepository) {
        this.bookingRepository = bookingRepository;
        this.kosService = kosService;
        this.transactionService = transactionService;
        this.stateValidator = stateValidator;
        this.eventHandlerContext = eventHandlerContext;
        this.paymentBookingRepository = paymentBookingRepository;
        this.transactionRepository = transactionRepository;
    }


    private Optional<Booking> findBookingByIdSync(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking createBooking(Booking booking) {

        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));
        booking.setMonthlyPrice(kos.getPrice());


        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        kosService.subtractAvailableRoom(kos.getId());
        return bookingRepository.save(booking);
    }

    // Public async method
    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Optional<Booking>> findBookingById(UUID id) {
        try {
            Optional<Booking> booking = findBookingByIdSync(id);
            return CompletableFuture.completedFuture(booking);
        } catch (Exception e) {
            log.error("Error fetching booking {}: {}", id, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<List<Booking>> findAllBookings() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            return CompletableFuture.completedFuture(bookings);
        } catch (Exception e) {
            log.error("Error fetching all bookings: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        Booking existingBooking = findBookingByIdSync(booking.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));


        stateValidator.validateForUpdate(existingBooking);

        bookingRepository.save(booking);
    }

    @Override
    public void payBooking(UUID bookingId) throws Exception {
        Booking booking = findBookingByIdSync(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));


        stateValidator.validateForPayment(booking);


        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

        CompletableFuture<Payment> paymentFuture = transactionService.createPayment(
                booking.getUserId(),
                kos.getOwnerId(),
                BigDecimal.valueOf(booking.getTotalPrice())
        );


        Payment savedPayment = paymentFuture.get();

        UUID paymentId = savedPayment.getId();

        transactionService.processBookingPayment(bookingId, paymentId);


        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void approveBooking(UUID bookingId) {
        Booking booking = findBookingByIdSync(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        stateValidator.validateForApproval(booking);

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        BookingApprovedEvent event = new BookingApprovedEvent(this, booking.getBookingId(), booking.getUserId());
        eventHandlerContext.handleEvent(event);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        stateValidator.validateForCancellation(booking);

        if (booking.getStatus() == BookingStatus.PAID) {
            try {
                Optional<PaymentBooking> paymentBookingOpt = paymentBookingRepository.findByBookingId(bookingId);

                if (paymentBookingOpt.isPresent() && paymentBookingOpt.get().getPaymentId() != null) {
                    PaymentBooking paymentBooking = paymentBookingOpt.get();
                    Payment payment = transactionRepository.findPaymentById(paymentBooking.getPaymentId())
                            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

                    UUID ownerId = payment.getOwner().getId();
                    CompletableFuture<Payment> refundFuture = transactionService.refundPayment(
                            paymentBooking.getPaymentId(),
                            ownerId
                    );

                    Payment refundPayment = refundFuture.get();

                    if (refundPayment.getStatus() == TransactionStatus.COMPLETED) {
                        booking.setStatus(BookingStatus.CANCELLED);
                        bookingRepository.save(booking);

                        kosService.addAvailableRoom(booking.getKosId());

                        log.info("Booking {} cancelled and refunded successfully", bookingId);
                    } else {
                        throw new RuntimeException("Refund failed with status: " + refundPayment.getStatus());
                    }
                } else {
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);

                    kosService.addAvailableRoom(booking.getKosId());

                    log.warn("Booking {} cancelled but no payment found to refund", bookingId);
                }
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    log.warn("Refund processing interrupted for booking {}", bookingId);
                    throw new RuntimeException("Refund processing was interrupted", e);
                }

                log.error("Error processing refund for booking {}: {}", bookingId, e.getMessage(), e);
                throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
            }
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            kosService.addAvailableRoom(booking.getKosId());

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

                    return bookingRepository.findAll().stream()
                            .filter(booking -> ownerKosIds.contains(booking.getKosId()))
                            .collect(Collectors.toList());
                });
    }

    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<List<Booking>> findBookingsByUserId(UUID userId) {
        try {
            List<Booking> userBookings = bookingRepository.findAll().stream()
                    .filter(booking -> booking.getUserId().equals(userId))
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(userBookings);
        } catch (Exception e) {
            log.error("Error fetching bookings for user {}: {}", userId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }


    @Override
    public void clearStore() {
        bookingRepository.deleteAll();
    }
}