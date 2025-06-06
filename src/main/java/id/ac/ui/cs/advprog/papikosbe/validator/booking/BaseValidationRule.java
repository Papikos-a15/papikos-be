package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;


public abstract class BaseValidationRule implements ValidationRule {

    @Override
    public final void validate(ValidationContext context) throws ValidationException {
        if (context == null) {
            throw new IllegalArgumentException("ValidationContext cannot be null");
        }

        if (!contextMeetsRequirements(context)) {
            throw new IllegalArgumentException(
                    String.format("Context doesn't meet requirements for %s. Required: %s",
                            getOperationType(), getRequirements())
            );
        }

        try {
            doValidate(context);
        } catch (IllegalStateException e) {
            throw new ValidationException(e.getMessage(), getOperationType(), context.getOperation(), e);
        }
    }
    protected abstract void doValidate(ValidationContext context) throws ValidationException;
}