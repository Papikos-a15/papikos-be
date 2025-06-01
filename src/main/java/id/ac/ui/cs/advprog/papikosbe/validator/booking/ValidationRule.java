package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;


public interface ValidationRule {


    void validate(ValidationContext context) throws ValidationException;


    boolean supports(String operation, BookingStatus status);

    String getOperationType();


    ValidationRequirement getRequirements();

    default boolean contextMeetsRequirements(ValidationContext context) {
        if (context == null) return false;

        ValidationRequirement req = getRequirements();
        return switch (req) {
            case BOOKING_ONLY -> context.getBooking() != null;
            case BOOKING_AND_KOS -> context.getBooking() != null && context.hasKos();
            case BOOKING_AND_REQUESTER -> context.getBooking() != null && context.hasRequester();
            case FULL_CONTEXT -> context.getBooking() != null && context.hasKos() && context.hasRequester();
        };
    }
}