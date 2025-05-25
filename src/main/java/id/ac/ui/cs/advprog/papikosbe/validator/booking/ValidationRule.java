package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;

public interface ValidationRule {
    void validate(ValidationContext context);
    boolean supports(String operation, BookingStatus status);
    String getOperationType();
    int getPriority(); // For execution order
}