package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingAccessValidatorTest {

    private BookingAccessValidator validator;
    private Booking booking;
    private UUID userId;
    private UUID kosId;
    private UUID ownerId;
    private Kos kos;

    @BeforeEach
    void setUp() {
        validator = new BookingAccessValidator();

        userId = UUID.randomUUID();
        kosId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        // Create a test booking
        booking = new Booking(
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

        // Create a test kos
        kos = new Kos();
        kos.setId(kosId);
        kos.setOwnerId(ownerId);
        kos.setName("Test Kos");
        kos.setPrice(1500000.0);
    }

    // User Access Tests

    @Test
    void validateUserAccess_sameUser_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateUserAccess(userId, userId));
    }

    @Test
    void validateUserAccess_differentUser_throwsException() {
        UUID differentUserId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateUserAccess(differentUserId, userId));
        assertTrue(exception.getMessage().contains("Only the tenant who made the booking"));
    }

    // Owner Access Tests

    @Test
    void validateOwnerAccess_sameOwner_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateOwnerAccess(ownerId, ownerId));
    }

    @Test
    void validateOwnerAccess_differentOwner_throwsException() {
        UUID differentOwnerId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateOwnerAccess(ownerId, differentOwnerId));
        assertTrue(exception.getMessage().contains("Only the kos owner can perform this action"));
    }

    // Payment Validation Tests

    @Test
    void validateForPayment_correctUser_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateForPayment(booking, userId));
    }

    @Test
    void validateForPayment_incorrectUser_throwsException() {
        UUID differentUserId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForPayment(booking, differentUserId));
        assertTrue(exception.getMessage().contains("Only the tenant who made the booking"));
    }

    // Approval Validation Tests

    @Test
    void validateForApproval_correctOwner_doesNotThrowException() {
        assertDoesNotThrow(() -> validator.validateForApproval(booking, kos, ownerId));
    }

    @Test
    void validateForApproval_incorrectOwner_throwsException() {
        UUID differentOwnerId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateForApproval(booking, kos, differentOwnerId));
        assertTrue(exception.getMessage().contains("Only the kos owner can perform this action"));
    }
}