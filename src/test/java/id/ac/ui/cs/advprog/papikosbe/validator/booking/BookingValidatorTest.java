package id.ac.ui.cs.advprog.papikosbe.validator.booking;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidatorTest {

    private BookingValidator validator;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // ✅ Create real validation rules with actual logic
        List<ValidationRule> rules = List.of(
                new UpdateValidationRule(),
                new PaymentValidationRule(),
                new ApprovalValidationRule(),
                new CancellationValidationRule(),
                new ActivationValidationRule(),
                new DeactivationValidationRule(),
                new KosAvailabilityValidationRule()
        );
        validator = new BookingValidator(rules);

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
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
        assertEquals("UPDATE", exception.getValidationRule());
    }

    @Test
    void validateForUpdate_cancelled_throwsException() {
        booking.setStatus(BookingStatus.CANCELLED);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
        assertEquals("UPDATE", exception.getValidationRule());
    }

    @Test
    void validateForUpdate_active_throwsException() {
        booking.setStatus(BookingStatus.ACTIVE);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
        assertEquals("UPDATE", exception.getValidationRule());
    }

    @Test
    void validateForUpdate_inactive_throwsException() {
        booking.setStatus(BookingStatus.INACTIVE);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForUpdate(booking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved, activated, cancelled, or deactivated"));
        assertEquals("UPDATE", exception.getValidationRule());
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
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForPayment(booking));
        assertTrue(exception.getMessage().contains("Only bookings in PENDING_PAYMENT status can be paid"));
        assertEquals("PAYMENT", exception.getValidationRule());
    }

    @Test
    void validateForPayment_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForPayment(booking));
        assertTrue(exception.getMessage().contains("Only bookings in PENDING_PAYMENT status can be paid"));
        assertEquals("PAYMENT", exception.getValidationRule());
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
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForApproval(booking));
        assertTrue(exception.getMessage().contains("Only PAID bookings can be approved"));
        assertEquals("APPROVAL", exception.getValidationRule());
    }

    @Test
    void validateForApproval_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForApproval(booking));
        assertTrue(exception.getMessage().contains("Only PAID bookings can be approved"));
        assertEquals("APPROVAL", exception.getValidationRule());
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
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel approved, active, or inactive bookings"));
        assertEquals("CANCELLATION", exception.getValidationRule());
    }

    @Test
    void validateForCancellation_active_throwsException() {
        booking.setStatus(BookingStatus.ACTIVE);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel approved, active, or inactive bookings"));
        assertEquals("CANCELLATION", exception.getValidationRule());
    }

    @Test
    void validateForCancellation_inactive_throwsException() {
        booking.setStatus(BookingStatus.INACTIVE);
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForCancellation(booking));
        assertTrue(exception.getMessage().contains("Cannot cancel approved, active, or inactive bookings"));
        assertEquals("CANCELLATION", exception.getValidationRule());
    }

    // Activation Validation Tests

    @Test
    void validateForActivation_approved_doesNotThrowException() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setCheckInDate(LocalDate.now().minusDays(1)); // past check-in date
        assertDoesNotThrow(() -> validator.validateForActivation(booking));
    }

    @Test
    void validateForActivation_paid_throwsException() {
        booking.setStatus(BookingStatus.PAID);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForActivation(booking));
        assertTrue(exception.getMessage().contains("Only APPROVED bookings can be activated"));
        assertEquals("ACTIVATION", exception.getValidationRule());
    }

    @Test
    void validateForActivation_futureCheckIn_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setCheckInDate(LocalDate.now().plusDays(1)); // future check-in date
        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForActivation(booking));
        assertTrue(exception.getMessage().contains("Booking cannot be activated before check-in date"));
        assertEquals("ACTIVATION", exception.getValidationRule());
    }

    // Deactivation Validation Tests

    @Test
    void validateForDeactivation_active_doesNotThrowException() {
        booking.setStatus(BookingStatus.ACTIVE);
        assertDoesNotThrow(() -> validator.validateForDeactivation(booking));
    }

    @Test
    void validateForDeactivation_approved_throwsException() {
        booking.setStatus(BookingStatus.APPROVED);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForDeactivation(booking));
        assertTrue(exception.getMessage().contains("Only ACTIVE bookings can be deactivated"));
        assertEquals("DEACTIVATION", exception.getValidationRule());
    }
    @Test
    void validate_nullContext_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null));
        assertEquals("ValidationContext cannot be null", ex.getMessage());
    }
}