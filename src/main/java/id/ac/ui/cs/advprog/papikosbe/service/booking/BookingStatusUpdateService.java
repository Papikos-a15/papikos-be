package id.ac.ui.cs.advprog.papikosbe.service.booking;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for handling booking status updates
 */
public interface BookingStatusUpdateService {
    
    /**
     * Scheduled method to check and update expired bookings
     */
    void scheduledBookingStatusUpdate();
    
    /**
     * Find and update bookings that have passed their end date to INACTIVE status
     * @return CompletableFuture with the count of updated bookings
     */
    CompletableFuture<Integer> updateExpiredBookingsAsync();

    /**
     * Update bookings based on check-in date:
     * - APPROVED bookings with today's or past check-in date → ACTIVE
     * - PENDING/PAID bookings with past check-in date → CANCELLED
     * @return CompletableFuture with the count of updated bookings
     */
    CompletableFuture<Integer> updateStartedBookingsAsync();
}