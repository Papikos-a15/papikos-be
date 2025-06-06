package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookingStatusUpdateServiceImpl implements BookingStatusUpdateService {

    private final BookingRepository bookingRepository;
    private final BookingValidator stateValidator; // Add validator
    private final KosService kosService; // Add for room management

    @Autowired
    public BookingStatusUpdateServiceImpl(BookingRepository bookingRepository,
                                          BookingValidator stateValidator,
                                          KosService kosService) {
        this.bookingRepository = bookingRepository;
        this.stateValidator = stateValidator;
        this.kosService = kosService;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    public void scheduledBookingStatusUpdate() {
        log.info("Running scheduled expired bookings check");
        updateExpiredBookingsAsync();
        updateStartedBookingsAsync();
        cancelExpiredPendingPaymentsAsync();
    }

    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> updateExpiredBookingsAsync() {
        log.info("Checking for expired bookings");
        LocalDate today = LocalDate.now();

        List<Booking> activeBookings = bookingRepository.findByStatus(BookingStatus.ACTIVE);

        List<Booking> expiredBookings = activeBookings.stream()
            .filter(booking -> {
                LocalDate endDate = booking.getCheckInDate().plusMonths(booking.getDuration());
                return endDate.isBefore(today);
            })
            .collect(Collectors.toList());
        
        log.info("Found {} bookings to mark as INACTIVE", expiredBookings.size());
        
        int updatedCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                stateValidator.validateForDeactivation(booking);
                
                booking.setStatus(BookingStatus.INACTIVE);
                bookingRepository.save(booking);

                kosService.addAvailableRoom(booking.getKosId());
                
                updatedCount++;
                log.info("Updated booking {} to INACTIVE status", booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to update booking {} to INACTIVE: {}", booking.getBookingId(), e.getMessage());
            }
        }
        
        return CompletableFuture.completedFuture(updatedCount);
    }

    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> updateStartedBookingsAsync() {
        log.info("Checking for bookings that should start or be cancelled");
        LocalDate today = LocalDate.now();
        int updatedCount = 0;

        List<Booking> approvedBookings = bookingRepository.findByStatus(BookingStatus.APPROVED);
        List<Booking> startingBookings = approvedBookings.stream()
                .filter(booking -> !booking.getCheckInDate().isAfter(today))
                .collect(Collectors.toList());

        for (Booking booking : startingBookings) {
            try {
                stateValidator.validateForActivation(booking);
                booking.setStatus(BookingStatus.ACTIVE);
                bookingRepository.save(booking);
                updatedCount++;
                log.info("Updated booking {} to ACTIVE status", booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to update booking {} to ACTIVE: {}", booking.getBookingId(), e.getMessage());
            }
        }

        List<Booking> pendingOrPaidBookings = new ArrayList<>();
        pendingOrPaidBookings.addAll(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT));
        pendingOrPaidBookings.addAll(bookingRepository.findByStatus(BookingStatus.PAID));

        List<Booking> missedBookings = pendingOrPaidBookings.stream()
                .filter(booking -> booking.getCheckInDate().isBefore(today))
                .collect(Collectors.toList());

        for (Booking booking : missedBookings) {
            try {
                stateValidator.validateForCancellation(booking);
                
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                kosService.addAvailableRoom(booking.getKosId());
                
                updatedCount++;
                log.info("Auto-cancelled booking {} (check-in date passed)", booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to cancel booking {}: {}", booking.getBookingId(), e.getMessage());
            }
        }

        return CompletableFuture.completedFuture(updatedCount);
    }

    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> cancelExpiredPendingPaymentsAsync() {
        log.info("Checking for expired PENDING_PAYMENT and PAID bookings to cancel");
        LocalDate today = LocalDate.now();
        int cancelledCount = 0;

        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT);
        List<Booking> expiredPendingBookings = pendingBookings.stream()
                .filter(booking -> booking.getCheckInDate().isBefore(today))
                .collect(Collectors.toList());

        List<Booking> paidBookings = bookingRepository.findByStatus(BookingStatus.PAID);
        List<Booking> expiredPaidBookings = paidBookings.stream()
                .filter(booking -> booking.getCheckInDate().isBefore(today))
                .collect(Collectors.toList());

        List<Booking> allExpiredBookings = new ArrayList<>();
        allExpiredBookings.addAll(expiredPendingBookings);
        allExpiredBookings.addAll(expiredPaidBookings);

        log.info("Found {} PENDING_PAYMENT and {} PAID bookings to cancel",
                expiredPendingBookings.size(), expiredPaidBookings.size());

        for (Booking booking : allExpiredBookings) {
            try {
                stateValidator.validateForCancellation(booking);

                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                kosService.addAvailableRoom(booking.getKosId());

                cancelledCount++;
                log.info("Auto-cancelled expired {} booking {}",
                        booking.getStatus(), booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to cancel expired booking {}: {}",
                        booking.getBookingId(), e.getMessage());
            }
        }

        log.info("Successfully cancelled {} expired bookings", cancelledCount);
        return CompletableFuture.completedFuture(cancelledCount);
    }
}