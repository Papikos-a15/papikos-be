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
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BookingStatusUpdateService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingStatusUpdateService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Scheduled task to check for and update expired bookings daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    public void scheduledExpiredBookingsUpdate() {
        log.info("Running scheduled expired bookings check");
        updateExpiredBookingsAsync(); 
    }

    /**
     * Find and update bookings that have passed their end date to INACTIVE status
     * @return CompletableFuture with the count of updated bookings
     */
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> updateExpiredBookingsAsync() {
        log.info("Checking for expired bookings");
        LocalDate today = LocalDate.now();
        
        // Find bookings that should be marked inactive
        List<Booking> expiredBookings = bookingRepository.findBookingsToDeactivate(today);
        
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