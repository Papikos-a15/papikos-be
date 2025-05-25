package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BaseValidationRule;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ActivationValidationRule extends BaseValidationRule {

    @Override
    protected void doValidate(ValidationContext context) throws ValidationException {
        if (context.getBooking().getStatus() != BookingStatus.APPROVED) {
            throw new ValidationException(
                    "Only APPROVED bookings can be activated",
                    getOperationType(),
                    context.getOperation()
            );
        }

        if (context.getBooking().getCheckInDate().isAfter(LocalDate.now())) {
            throw new ValidationException(
                    "Booking cannot be activated before check-in date",
                    getOperationType(),
                    context.getOperation()
            );
        }
    }

    @Override
    public boolean supports(String operation, BookingStatus status) {
        return "ACTIVATION".equals(operation);
    }

    @Override
    public String getOperationType() {
        return "ACTIVATION";
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