package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BookingService {
    // Sync methods (keep as-is)
    Booking createBooking(Booking booking);
    void updateBooking(Booking booking);
    void payBooking(UUID bookingId) throws Exception;
    void approveBooking(UUID bookingId);
    void cancelBooking(UUID bookingId);
    void clearStore();

    // Async methods (changed)
    CompletableFuture<Optional<Booking>> findBookingById(UUID id); // ← Changed to async
    CompletableFuture<List<Booking>> findAllBookings();
    CompletableFuture<List<Booking>> findBookingsByUserId(UUID userId);
    CompletableFuture<List<Booking>> findBookingsByOwnerId(UUID ownerId);
}