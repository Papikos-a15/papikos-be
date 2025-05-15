package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import jakarta.persistence.EntityNotFoundException;

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
        // Validate input
        if (booking.getMonthlyPrice() <= 0) {
            throw new IllegalArgumentException("Monthly price must be greater than 0");
        }
        if (booking.getFullName() == null || booking.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (booking.getPhoneNumber() == null || booking.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        
        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }
        
        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        
        // Simpan booking ke dalam store
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    @Override
    public List<Booking> findAllBookings() {
        return new ArrayList<>(bookingStore.values());
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
        } else {
            throw new EntityNotFoundException("Booking with ID " + bookingId + " not found");
        }
    }
    
    @Override
    public void updateBooking(Booking booking) {
        // Skeleton implementation - will be filled in during the GREEN phase
        // Check if booking exists
        if (!bookingStore.containsKey(booking.getBookingId())) {
            throw new EntityNotFoundException("Booking with ID " + booking.getBookingId() + " not found");
        }
        
        // For now, always throw an exception if booking status is not PENDING_PAYMENT
        Booking existingBooking = bookingStore.get(booking.getBookingId());
        if (existingBooking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Cannot edit booking after it has been paid or cancelled");
        }
        
        // Otherwise, update the booking
        bookingStore.put(booking.getBookingId(), booking);
    }
    
    // Utility for tests
    public void clearStore() {
        bookingStore.clear();
    }
}