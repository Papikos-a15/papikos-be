package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class BookingValidator {

    private final List<ValidationRule> validationRules;

    @Autowired
    public BookingValidator(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public void validate(ValidationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ValidationContext cannot be null");
        }

        List<ValidationRule> applicableRules = validationRules.stream()
                .filter(rule -> rule.supports(context.getOperation(), context.getBooking().getStatus()))
                .filter(rule -> rule.contextMeetsRequirements(context)) // ✅ LSP: Check requirements
                .toList();

        // ✅ LSP: Consistent exception handling
        for (ValidationRule rule : applicableRules) {
            rule.validate(context); // Will throw ValidationException if fails
        }
    }

    // Convenience methods remain the same...
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

    // Keep direct method for simple kos availability checks
    public void validateKosAvailability(Kos kos) {
        if (!kos.isAvailable()) {
            throw new IllegalStateException("Kos is not available for booking");
        }

        if (kos.getAvailableRooms() <= 0) {
            throw new IllegalStateException("No rooms available for booking");
        }
    }
}