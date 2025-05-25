package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

@Component
public class KosAvailabilityValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        // ✅ LSP: No early return - always process since BaseValidationRule already checks requirements
        Kos kos = context.getKos();

        if (!kos.isAvailable()) {
            throw new ValidationException(
                    "Kos is not available for booking",
                    getOperationType(),
                    context.getOperation()
            );
        }

        if (kos.getAvailableRooms() <= 0) {
            throw new ValidationException(
                    "No rooms available for booking",
                    getOperationType(),
                    context.getOperation()
            );
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
    public ValidationRequirement getRequirements() {
        return ValidationRequirement.BOOKING_AND_KOS; // ✅ Explicit requirement
    }
}