package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidatorTest {

    private BookingValidator validator;
    private Booking validBooking;
    private UUID userId;
    private UUID kosId;
    private UUID ownerId;
    private Kos kos;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();

        userId = UUID.randomUUID();
        kosId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        // Create a valid booking for testing
        validBooking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                1500000.0,
                "John Doe",
                "081234567890",
                BookingStatus.PENDING_PAYMENT
        );

        // Create a kos for testing
        kos = new Kos();
        kos.setId(kosId);
        kos.setOwnerId(ownerId);
    }

    // Basic Field Validation Tests

    @Test
    void validateBasicFields_validBooking_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateBasicFields(validBooking));
    }

    @Test
    void validateBasicFields_invalidDuration_throwsException() {
        validBooking.setDuration(0);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBasicFields(validBooking));
        assertTrue(exception.getMessage().contains("Duration must be at least 1 month"));
    }

    @Test
    void validateBasicFields_pastCheckInDate_throwsException() {
        validBooking.setCheckInDate(LocalDate.now().minusDays(1));
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBasicFields(validBooking));
        assertTrue(exception.getMessage().contains("Check-in date cannot be in the past"));
    }

    @Test
    void validateBasicFields_invalidPrice_throwsException() {
        validBooking.setMonthlyPrice(0);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBasicFields(validBooking));
        assertTrue(exception.getMessage().contains("Monthly price must be greater than 0"));
    }

    @Test
    void validateBasicFields_emptyFullName_throwsException() {
        validBooking.setFullName("");
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBasicFields(validBooking));
        assertTrue(exception.getMessage().contains("Full name cannot be empty"));
    }

    @Test
    void validateBasicFields_emptyPhoneNumber_throwsException() {
        validBooking.setPhoneNumber("");
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validateBasicFields(validBooking));
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }

    // Payment Validation Tests

    @Test
    void validateForPayment_validBookingAndUser_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateForPayment(validBooking, userId));
    }

    @Test
    void validateForPayment_invalidStatus_throwsException() {
        validBooking.setStatus(BookingStatus.PAID);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForPayment(validBooking, userId));
        assertTrue(exception.getMessage().contains("Only bookings in PENDING_PAYMENT status can be paid"));
    }

    @Test
    void validateForPayment_wrongUser_throwsException() {
        UUID differentUserId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForPayment(validBooking, differentUserId));
        assertTrue(exception.getMessage().contains("Only the tenant who made the booking can pay for it"));
    }

    // Approval Validation Tests

    @Test
    void validateForApproval_validBookingAndOwner_doesNotThrowException() {
        validBooking.setStatus(BookingStatus.PAID);
        assertDoesNotThrow(() -> validator.validateForApproval(validBooking, kos, ownerId));
    }

    @Test
    void validateForApproval_invalidStatus_throwsException() {
        validBooking.setStatus(BookingStatus.PENDING_PAYMENT);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForApproval(validBooking, kos, ownerId));
        assertTrue(exception.getMessage().contains("Only PAID bookings can be approved"));
    }

    @Test
    void validateForApproval_wrongOwner_throwsException() {
        UUID differentOwnerId = UUID.randomUUID();
        validBooking.setStatus(BookingStatus.PAID);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForApproval(validBooking, kos, differentOwnerId));
        assertTrue(exception.getMessage().contains("Only the kos owner can approve this booking"));
    }

    // Update Validation Tests

    @Test
    void validateForUpdate_pendingPayment_doesNotThrowException() {
        validBooking.setStatus(BookingStatus.PENDING_PAYMENT);
        assertDoesNotThrow(() -> validator.validateForUpdate(validBooking));
    }

    @Test
    void validateForUpdate_paid_doesNotThrowException() {
        validBooking.setStatus(BookingStatus.PAID);
        assertDoesNotThrow(() -> validator.validateForUpdate(validBooking));
    }

    @Test
    void validateForUpdate_approved_throwsException() {
        validBooking.setStatus(BookingStatus.APPROVED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(validBooking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved or cancelled"));
    }

    @Test
    void validateForUpdate_cancelled_throwsException() {
        validBooking.setStatus(BookingStatus.CANCELLED);
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForUpdate(validBooking));
        assertTrue(exception.getMessage().contains("Cannot edit booking after it has been approved or cancelled"));
    }

    // Owner Access Validation Tests

    @Test
    void validateOwnerAccess_sameOwner_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateOwnerAccess(ownerId, ownerId));
    }

    @Test
    void validateOwnerAccess_differentOwner_throwsException() {
        UUID differentOwnerId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateOwnerAccess(ownerId, differentOwnerId));
        assertTrue(exception.getMessage().contains("You can only view bookings for kos you own"));
    }
}