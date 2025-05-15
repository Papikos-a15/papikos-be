package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    private static BookingServiceImpl instance;
    private Map<UUID, Booking> bookingStore;

    // Private constructor dengan inisialisasi bookingStore
    private BookingServiceImpl() {
        bookingStore = new ConcurrentHashMap<>();
    }

    public static synchronized BookingServiceImpl getInstance() {
        if (instance == null) {
            instance = new BookingServiceImpl();
        }
        return instance;
    }

    @Override
    public Booking createBooking(Booking booking) {
        // Minimal implementation for RED phase
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        // Minimal implementation for RED phase
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    @Override
    public List<Booking> findAllBookings() {
        // Minimal implementation for RED phase
        return new ArrayList<>(bookingStore.values());
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        // Minimal implementation for RED phase - will be expanded
        Booking booking = bookingStore.get(bookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        // Minimal implementation for RED phase - will be expanded
        // This will fail for the test case where we try to edit after approval
        bookingStore.put(booking.getBookingId(), booking);
    }

    @Override
    public void updateBookingStatus(UUID bookingId, BookingStatus newStatus) {
        // Skeleton implementation for RED phase
        // This method allows updating just the status of a booking
        // Will be implemented in GREEN phase

        // Minimal implementation to compile
        Booking booking = bookingStore.get(bookingId);
        if (booking != null) {
            booking.setStatus(newStatus);
            bookingStore.put(bookingId, booking);
        }
    }

    // Utility for tests
    public void clearStore() {
        bookingStore.clear();
    }
}