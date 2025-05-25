package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

@Component
public class ApprovalValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        if (context.getBooking().getStatus() != BookingStatus.PAID) {
            throw new ValidationException(
                    "Only PAID bookings can be approved",
                    getOperationType(),
                    context.getOperation()
            );
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "APPROVAL".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "APPROVAL";
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