package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.PaymentService;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final KosService kosService;
    private final PaymentService paymentService;

    @Override
    public Booking createBooking(Booking booking) {
        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos with ID " + booking.getKosId() + " not found"));
        booking.setMonthlyPrice(kos.getPrice());

        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        validateBookingData(booking);

        // Save booking to repository
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public void updateBooking(Booking booking) {
        Booking existingBooking = bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

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
        bookingRepository.save(booking);
    }

    @Override
    public void payBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only bookings in PENDING_PAYMENT status can be paid");
        }

        // Get kos to find the owner
        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

        // Create payment from tenant to owner
        BigDecimal amount = BigDecimal.valueOf(booking.getTotalPrice());
        paymentService.createPayment(booking.getUserId(), kos.getOwnerId(), amount);

        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void approveBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Only PAID bookings can be approved");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
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
    @Override
    public void clearStore() {
        bookingRepository.deleteAll();
    }
}