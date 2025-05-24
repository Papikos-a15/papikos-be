package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidatorTest {

    private BookingValidator validator;
    private Booking booking;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();

        // Create a sample booking
        booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),
                3,
                1500000.0,
                "John Doe",
                "081234567890",
                BookingStatus.PENDING_PAYMENT
        );
    }

    // Update Validation Tests

    @Test
    void validateForUpdate_pendingPayment_doesNotThrowException() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        assertDoesNotThrow(() -> validator.validateForUpdate(booking));
    }

    @Test
    void validateForUpdate_paid_doesNotThrowException() {
        booking.setStatus(BookingStatus.PAID);
        assertDoesNotThrow(() -> validator.validateForUpdate(booking));
    }

    @Test
    void validateForUpdate_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved or cancelled"));
    }

    @Test
    void validateForUpdate_cancelled_throwsException() {
        booking.setStatus(BookingStatus.CANCELLED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved or cancelled"));
    }

    // Payment Validation Tests

    @Test
    void validateForPayment_pendingPayment_doesNotThrowException() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        assertDoesNotThrow(() -> validator.validateForPayment(booking));
    }

    @Test
    void validateForPayment_paid_throwsException() {
        booking.setStatus(BookingStatus.PAID);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForPayment(booking));
        assertTrue(exception.getMessage().contains("Only bookings in PENDING_PAYMENT status can be paid"));
    }

    @Test
    void validateForPayment_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForPayment(booking));
        assertTrue(exception.getMessage().contains("Only bookings in PENDING_PAYMENT status can be paid"));
    }

    // Approval Validation Tests

    @Test
    void validateForApproval_paid_doesNotThrowException() {
        booking.setStatus(BookingStatus.PAID);
        assertDoesNotThrow(() -> validator.validateForApproval(booking));
    }

    @Test
    void validateForApproval_pendingPayment_throwsException() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForApproval(booking));
        assertTrue(exception.getMessage().contains("Only PAID bookings can be approved"));
    }

    @Test
    void validateForApproval_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForApproval(booking));
        assertTrue(exception.getMessage().contains("Only PAID bookings can be approved"));
    }

    // Cancellation Validation Tests

    @Test
    void validateForCancellation_pendingPayment_doesNotThrowException() {
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        assertDoesNotThrow(() -> validator.validateForCancellation(booking));
    }

    @Test
    void validateForCancellation_paid_doesNotThrowException() {
        booking.setStatus(BookingStatus.PAID);
        assertDoesNotThrow(() -> validator.validateForCancellation(booking));
    }

    @Test
    void validateForCancellation_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel an already approved booking"));
    }

    @Test
    void validateForUpdate_active_throwsException() {
        booking.setStatus(BookingStatus.ACTIVE);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
    }

    @Test
    void validateForUpdate_inactive_throwsException() {
        booking.setStatus(BookingStatus.INACTIVE);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
    }

    @Test
    void validateForCancellation_active_throwsException() {
        booking.setStatus(BookingStatus.ACTIVE);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel approved, active, or inactive bookings"));
    }

    @Test
    void validateForCancellation_inactive_throwsException() {
        booking.setStatus(BookingStatus.INACTIVE);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel approved, active, or inactive bookings"));
    }

    @Test
    void validateBookingAdvance_today_throwsException() {
        LocalDate today = LocalDate.now();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBookingAdvance(today));
        assertTrue(exception.getMessage().contains("Booking must be made at least 1 day in advance"));
    }

    @Test
    void validateBookingAdvance_yesterday_throwsException() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBookingAdvance(yesterday));
        assertTrue(exception.getMessage().contains("Booking must be made at least 1 day in advance"));
    }

    @Test
    void validateBookingAdvance_tomorrow_doesNotThrowException() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertDoesNotThrow(() -> validator.validateBookingAdvance(tomorrow));
    }
}