package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException; // ✅ ADD IMPORT
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidatorKosAvailabilityTest {

    private BookingValidator validator;
    private Booking booking;
    private Kos kos;

    @BeforeEach
    void setUp() {
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

        // Create a valid booking
        booking = new Booking();
        booking.setBookingId(UUID.randomUUID());
        booking.setUserId(UUID.randomUUID());
        booking.setKosId(UUID.randomUUID());
        booking.setCheckInDate(LocalDate.now().plusDays(7));
        booking.setDuration(3);
        booking.setMonthlyPrice(1500000.0);
        booking.setFullName("John Doe");
        booking.setPhoneNumber("081234567890");
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        // Create a valid kos with available rooms
        kos = new Kos();
        kos.setId(booking.getKosId());
        kos.setAvailable(true);
        kos.setAvailableRooms(5);
        kos.setMaxCapacity(10);
        kos.setPrice(1500000.0);
    }

    @Test
    void validateKosAvailability_availableKos_doesNotThrowException() {
        // Kos is available and has rooms
        assertDoesNotThrow(() -> validator.validateKosAvailability(kos));
    }

    @Test
    void validateKosAvailability_unavailableKos_throwsException() {
        // Kos is marked as unavailable
        kos.setAvailable(false);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateKosAvailability(kos));
        assertTrue(exception.getMessage().contains("Kos is not available for booking"));
    }

    @Test
    void validateKosAvailability_noAvailableRooms_throwsException() {
        // Kos has 0 available rooms
        kos.setAvailableRooms(0);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateKosAvailability(kos));
        assertTrue(exception.getMessage().contains("No rooms available for booking"));
    }

    @Test
    void validateKosAvailability_negativeAvailableRooms_throwsException() {
        // Kos has negative available rooms (should never happen, but testing boundary case)
        kos.setAvailableRooms(-1);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> validator.validateKosAvailability(kos));
        assertTrue(exception.getMessage().contains("No rooms available for booking"));
    }

    @Test
    void validateForCreation_withValidKos_doesNotThrowException() {
        // Both booking and kos are valid
        assertDoesNotThrow(() -> validator.validateForCreation(booking, kos));
    }

    @Test
    void validateForCreation_withUnavailableKos_throwsException() {
        // Kos is unavailable
        kos.setAvailable(false);

        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForCreation(booking, kos));
        assertTrue(exception.getMessage().contains("Kos is not available for booking"));
        assertEquals("KOS_AVAILABILITY", exception.getValidationRule());
        assertEquals("CREATION", exception.getOperation());
    }

    @Test
    void validateForCreation_withNoRooms_throwsException() {
        // No rooms available
        kos.setAvailableRooms(0);

        // ✅ CHANGE: Expect ValidationException instead of IllegalStateException
        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateForCreation(booking, kos));
        assertTrue(exception.getMessage().contains("No rooms available for booking"));
        assertEquals("KOS_AVAILABILITY", exception.getValidationRule());
        assertEquals("CREATION", exception.getOperation());
    }
}