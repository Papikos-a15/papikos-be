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
        // Full validation of all booking fields
        validateBookingData(booking);

        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        // Save booking to store
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
        if (booking == null) {
            throw new EntityNotFoundException("Booking with ID " + bookingId + " not found");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingStore.put(bookingId, booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        // Validate booking exists
        if (!bookingStore.containsKey(booking.getBookingId())) {
            throw new EntityNotFoundException("Booking with ID " + booking.getBookingId() + " not found");
        }

        // Get existing booking
        Booking existingBooking = bookingStore.get(booking.getBookingId());

        // Check if booking can be edited (PENDING_PAYMENT or PAID status)
        if (existingBooking.getStatus() != BookingStatus.PENDING_PAYMENT &&
                existingBooking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Cannot edit booking after it has been approved or cancelled");
        }

        // Preserve the current status to prevent status changes via general update
        BookingStatus currentStatus = existingBooking.getStatus();

        // Validate all updated data
        validateBookingData(booking);

        // Ensure status isn't changed through this method
        booking.setStatus(currentStatus);

        // Update the booking
        bookingStore.put(booking.getBookingId(), booking);
    }

    @Override
    public void payBooking(UUID bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            throw new EntityNotFoundException("Booking with ID " + bookingId + " not found");
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only bookings in PENDING_PAYMENT status can be paid");
        }

        booking.setStatus(BookingStatus.PAID);
        bookingStore.put(bookingId, booking);
    }

    @Override
    public void approveBooking(UUID bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            throw new EntityNotFoundException("Booking with ID " + bookingId + " not found");
        }

        if (booking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Only PAID bookings can be approved");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingStore.put(bookingId, booking);
    }

    // Helper method to validate booking data
    private void validateBookingData(Booking booking) {
        if (booking.getDuration() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        if (booking.getCheckInDate() == null || booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (booking.getMonthlyPrice() <= 0) {
            throw new IllegalArgumentException("Monthly price must be greater than 0");
        }

        if (booking.getFullName() == null || booking.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        if (booking.getPhoneNumber() == null || booking.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
    }

    // Utility for tests
    public void clearStore() {
        bookingStore.clear();
    }
}