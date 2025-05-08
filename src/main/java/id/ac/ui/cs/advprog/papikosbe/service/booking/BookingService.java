package id.ac.ui.cs.advprog.papikosbe.service.booking;

import java.util.Optional;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> findBookingById(UUID bookingId);
    List<Booking> findAllBookings();
    void cancelBooking(UUID bookingId);
    public void clearStore();
}
