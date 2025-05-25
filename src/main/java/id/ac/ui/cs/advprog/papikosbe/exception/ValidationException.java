package id.ac.ui.cs.advprog.papikosbe.exception;


/**
 * Exception thrown when validation fails
 */
public class ValidationException extends RuntimeException {
    private final String validationRule;
    private final String operation;

    public ValidationException(String message, String validationRule, String operation) {
        super(message);
        this.validationRule = validationRule;
        this.operation = operation;
    }

    public ValidationException(String message, String validationRule, String operation, Throwable cause) {
        super(message, cause);
        this.validationRule = validationRule;
        this.operation = operation;
    }

    public String getValidationRule() { return validationRule; }
    public String getOperation() { return operation; }
}
