package id.ac.ui.cs.advprog.papikosbe.service;

import java.util.Optional;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> findBookingById(UUID bookingId);
    void cancelBooking(UUID bookingId);
}
