package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> findBookingById(UUID id);
    List<Booking> findAllBookings();
    void updateBooking(Booking booking);

    // Specific status transition methods (following OCP)
    void payBooking(UUID bookingId) throws Exception;
    void approveBooking(UUID bookingId);
    void cancelBooking(UUID bookingId);
    // Add method to find bookings by owner ID
    CompletableFuture<List<Booking>> findBookingsByOwnerId(UUID ownerId);
    List<Booking> findBookingsByUserId(UUID userId);
    // For tests
    void clearStore();
}