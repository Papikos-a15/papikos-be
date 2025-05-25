package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class CancellationValidationRule implements ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        BookingStatus status = context.getBooking().getStatus();
        if (status == BookingStatus.APPROVED ||
                status == BookingStatus.ACTIVE ||
                status == BookingStatus.INACTIVE) {
            throw new IllegalStateException("Cannot cancel approved, active, or inactive bookings");
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
}