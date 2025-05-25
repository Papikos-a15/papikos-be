package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class KosAvailabilityValidationRule implements ValidationRule {

    @Override
    public void validate(ValidationContext context) {
        if (!context.hasKos()) return;

        Kos kos = context.getKos();
        if (!kos.isAvailable()) {
            throw new IllegalStateException("Kos is not available for booking");
        }

        if (kos.getAvailableRooms() <= 0) {
            throw new IllegalStateException("No rooms available for booking");
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "CREATION".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "KOS_AVAILABILITY";
    }

    @Override
    public int getPriority() {
        return 1; // High priority
    }
}