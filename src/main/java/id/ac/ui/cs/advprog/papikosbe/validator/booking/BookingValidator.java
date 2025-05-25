package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class BookingValidator {

    private final List<ValidationRule> validationRules;

    @Autowired
    public BookingValidator(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    /**
     * Universal validation method with ValidationContext
     */
    public void validate(ValidationContext context) {
        validationRules.stream()
                .filter(rule -> rule.supports(context.getOperation(), context.getBooking().getStatus()))
                .sorted(Comparator.comparingInt(ValidationRule::getPriority))
                .forEach(rule -> rule.validate(context));
    }

    // Convenience methods for common operations
    public void validateForCreation(Booking booking, Kos kos) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .kos(kos)
                .operation("CREATION")
                .build();
        validate(context);
    }

    public void validateForUpdate(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("UPDATE")
                .build();
        validate(context);
    }

    public void validateForPayment(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("PAYMENT")
                .build();
        validate(context);
    }

    public void validateForApproval(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("APPROVAL")
                .build();
        validate(context);
    }

    public void validateForCancellation(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("CANCELLATION")
                .build();
        validate(context);
    }

    public void validateForActivation(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("ACTIVATION")
                .build();
        validate(context);
    }

    public void validateForDeactivation(Booking booking) {
        ValidationContext context = ValidationContext.builder()
                .booking(booking)
                .operation("DEACTIVATION")
                .build();
        validate(context);
    }

    public void validateKosAvailability(Kos kos) {
        // ✅ Direct validation - no need for temp booking
        if (!kos.isAvailable()) {
            throw new IllegalStateException("Kos is not available for booking");
        }

        if (kos.getAvailableRooms() <= 0) {
            throw new IllegalStateException("No rooms available for booking");
        }
    }
}