package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final KosService kosService;
    private final TransactionService transactionService;
    private final BookingValidator stateValidator;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              KosService kosService,
                              TransactionService transactionService,
                              BookingValidator stateValidator) {
        this.bookingRepository = bookingRepository;
        this.kosService = kosService;
        this.transactionService = transactionService;
        this.stateValidator = stateValidator;}


    private Optional<Booking> findBookingByIdSync(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking createBooking(Booking booking) {
        
        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }
        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));
        booking.setMonthlyPrice(kos.getPrice());

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
                
        // Validate state allows update
        stateValidator.validateForUpdate(existingBooking);
        
        bookingRepository.save(booking);
    }

    @Override
    public void payBooking(UUID bookingId) throws Exception {
        Booking booking = findBookingByIdSync(bookingId)
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
        Booking booking = findBookingByIdSync(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForApproval(booking);

        // Update booking status to APPROVED
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = findBookingByIdSync(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForCancellation(booking);

        // Update booking status to CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        
        // Add available room back
        kosService.addAvailableRoom(booking.getKosId());
        
        bookingRepository.save(booking);
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