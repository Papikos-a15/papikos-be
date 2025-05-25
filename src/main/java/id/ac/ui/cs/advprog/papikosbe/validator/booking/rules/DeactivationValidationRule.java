package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

@Component
public class DeactivationValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        if (context.getBooking().getStatus() != BookingStatus.ACTIVE) {
            throw new ValidationException(
                    "Only ACTIVE bookings can be deactivated",
                    getOperationType(),
                    context.getOperation()
            );
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "DEACTIVATION".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "DEACTIVATION";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public ValidationRequirement getRequirements() {
        return ValidationRequirement.BOOKING_ONLY;
    }
}