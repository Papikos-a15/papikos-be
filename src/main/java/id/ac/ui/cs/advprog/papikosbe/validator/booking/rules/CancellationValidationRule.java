package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

@Component
public class CancellationValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        BookingStatus status = context.getBooking().getStatus();
        if (status == BookingStatus.APPROVED ||
                status == BookingStatus.ACTIVE ||
                status == BookingStatus.INACTIVE) {
            throw new ValidationException(
                    "Cannot cancel approved, active, or inactive bookings",
                    getOperationType(),
                    context.getOperation()
            );
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "CANCELLATION".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "CANCELLATION";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public ValidationRequirement getRequirements() {
        return ValidationRequirement.BOOKING_ONLY;
    }
}