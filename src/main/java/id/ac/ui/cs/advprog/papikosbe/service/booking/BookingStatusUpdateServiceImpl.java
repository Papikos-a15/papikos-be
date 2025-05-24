package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
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

/**
 * Implementation of BookingStatusUpdateService that handles the automatic
 * update of bookings that have expired (passed their end date)
 */
@Service
@Slf4j
public class BookingStatusUpdateServiceImpl implements BookingStatusUpdateService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingStatusUpdateServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    public void scheduledBookingStatusUpdate() {
        log.info("Running scheduled expired bookings check");
        updateExpiredBookingsAsync();
        updateStartedBookingsAsync();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> updateExpiredBookingsAsync() {
        log.info("Checking for expired bookings");
        LocalDate today = LocalDate.now();
        
        // Find all approved bookings
        List<Booking> approvedBookings = bookingRepository.findByStatus(BookingStatus.APPROVED);
        
        // Filter to find expired ones
        List<Booking> expiredBookings = approvedBookings.stream()
            .filter(booking -> {
                // Calculate end date by adding months to check-in date
                LocalDate endDate = booking.getCheckInDate().plusMonths(booking.getDuration());
                return endDate.isBefore(today);
            })
            .collect(Collectors.toList());
        
        log.info("Found {} bookings to mark as INACTIVE", expiredBookings.size());
        
        int updatedCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                booking.setStatus(BookingStatus.INACTIVE);
                bookingRepository.save(booking);
                updatedCount++;
                log.info("Updated booking {} to INACTIVE status", booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to update booking {} to INACTIVE: {}", booking.getBookingId(), e.getMessage());
            }
        }
        
        log.info("Successfully updated {} expired bookings to INACTIVE status", updatedCount);
        return CompletableFuture.completedFuture(updatedCount);
    }


}