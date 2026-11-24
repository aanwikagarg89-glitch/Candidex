package exceptions;

/**
 * Base checked exception class for all domain-specific errors in CANDIDEX.
 */
public class CandidexException extends Exception {

    public CandidexException(String message) {
        super(message);
    }

    public CandidexException(String message, Throwable cause) {
        super(message, cause);
    }
}
