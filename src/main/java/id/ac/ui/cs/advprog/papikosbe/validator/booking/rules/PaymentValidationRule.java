package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        if (context.getBooking().getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ValidationException(
                    "Only bookings in PENDING_PAYMENT status can be paid",
                    getOperationType(),
                    context.getOperation()
            );
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "PAYMENT".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "PAYMENT";
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