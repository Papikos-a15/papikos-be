package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class DeactivationValidationRule implements ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        if (context.getBooking().getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE bookings can be deactivated");
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
}