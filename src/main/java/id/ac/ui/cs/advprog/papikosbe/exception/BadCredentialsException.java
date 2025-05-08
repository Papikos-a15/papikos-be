// src/main/java/id/ac/ui/cs/advprog/papikosbe/exception/BadCredentialsException.java
package id.ac.ui.cs.advprog.papikosbe.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
