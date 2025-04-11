package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import java.util.Optional;
import java.util.UUID;

public class BookingServiceImpl implements BookingService {

    private static BookingServiceImpl instance;

    // Constructor private untuk Singleton
    private BookingServiceImpl() {
        // TODO: Inisialisasi state internal jika diperlukan
    }

    public static synchronized BookingServiceImpl getInstance() {
        if (instance == null) {
            instance = new BookingServiceImpl();
        }
        return instance;
    }

    @Override
    public Booking createBooking(Booking booking) {
        // TODO: Implementasikan pembuatan booking
        return null;
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        // TODO: Implementasikan pencarian booking berdasarkan bookingId
        return Optional.empty();
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        // TODO: Implementasikan pembatalan booking
    }
}
