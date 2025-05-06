// src/main/java/id/ac/ui/cs/advprog/papikosbe/exception/DuplicateEmailException.java
package id.ac.ui.cs.advprog.papikosbe.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email already in use: " + email);
    }
}
