package id.ac.ui.cs.advprog.papikosbe.validator.booking.rules;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationContext;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.ValidationRule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ActivationValidationRule implements ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        if (context.getBooking().getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED bookings can be activated");
        }

        if (context.getBooking().getCheckInDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Booking cannot be activated before check-in date");
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
}