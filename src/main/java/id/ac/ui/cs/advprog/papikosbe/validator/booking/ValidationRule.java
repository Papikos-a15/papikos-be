package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;

/**
 * LSP-Compliant ValidationRule interface
 *
 * CONTRACT:
 * 1. validate() must throw ValidationException if validation fails
 * 2. validate() must NOT return early - always process or throw
 * 3. supports() must return true if rule can handle the operation
 * 4. getRequirements() defines what context data is needed
 */
public interface ValidationRule {

    /**
     * Validates the given context
     *
     * @param context ValidationContext containing data to validate
     * @throws ValidationException if validation fails
     * @throws IllegalArgumentException if context doesn't meet requirements
     *
     * PRECONDITION: context != null && contextMeetsRequirements(context)
     * POSTCONDITION: either completes normally or throws exception
     */
    void validate(ValidationContext context) throws ValidationException;

    /**
     * Check if this rule supports the given operation and status
     */
    boolean supports(String operation, BookingStatus status);

    /**
     * Get the type of validation this rule performs
     */
    String getOperationType();

    /**
     * Get execution priority (lower = higher priority)
     */
    int getPriority();

    /**
     * Define what context data this rule requires
     */
    ValidationRequirement getRequirements();

    /**
     * Check if context meets this rule's requirements
     * DEFAULT IMPLEMENTATION for backward compatibility
     */
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