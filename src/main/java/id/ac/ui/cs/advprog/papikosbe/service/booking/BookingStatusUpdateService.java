package id.ac.ui.cs.advprog.papikosbe.service.booking;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for handling booking status updates
 */
public interface BookingStatusUpdateService {
    
    /**
     * Scheduled method to check and update expired bookings
     */
    void scheduledExpiredBookingsUpdate();
    
    /**
     * Find and update bookings that have passed their end date to INACTIVE status
     * @return CompletableFuture with the count of updated bookings
     */
    CompletableFuture<Integer> updateExpiredBookingsAsync();
}