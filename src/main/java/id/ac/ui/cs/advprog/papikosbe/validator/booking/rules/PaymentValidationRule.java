package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidationRule implements ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        if (context.getBooking().getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only bookings in PENDING_PAYMENT status can be paid");
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
}