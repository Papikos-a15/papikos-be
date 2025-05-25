package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class ApprovalValidationRule implements ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        if (context.getBooking().getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Only PAID bookings can be approved");
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
}