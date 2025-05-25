package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;

/**
 * Base class that enforces LSP compliance
 */
public abstract class BaseValidationRule implements ValidationRule {

    @Override
    public final void validate(ValidationContext context) throws ValidationException {
        // ✅ ENFORCE LSP: Always check preconditions
        if (context == null) {
            throw new IllegalArgumentException("ValidationContext cannot be null");
        }

        if (!contextMeetsRequirements(context)) {
            throw new IllegalArgumentException(
                    String.format("Context doesn't meet requirements for %s. Required: %s",
                            getOperationType(), getRequirements())
            );
        }

        // ✅ ENFORCE LSP: Delegate to actual validation with consistent error handling
        try {
            doValidate(context);
        } catch (IllegalStateException e) {
            // Convert to ValidationException for consistency
            throw new ValidationException(e.getMessage(), getOperationType(), context.getOperation(), e);
        }
    }

    /**
     * Actual validation logic - subclasses implement this
     * ✅ LSP: All implementations must follow same contract
     */
    protected abstract void doValidate(ValidationContext context) throws ValidationException;
}