package id.ac.ui.cs.advprog.papikosbe.service.booking;

import java.util.concurrent.CompletableFuture;


public interface BookingStatusUpdateService {
    void scheduledBookingStatusUpdate();
    CompletableFuture<Integer> updateExpiredBookingsAsync();
    CompletableFuture<Integer> updateStartedBookingsAsync();
    CompletableFuture<Integer> cancelExpiredPendingPaymentsAsync();
}