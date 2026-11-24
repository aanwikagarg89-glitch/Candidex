package exceptions;

/**
 * Thrown when academic data fails validation rules (e.g. CGPA < 0 or > 10, negative backlogs).
 */
public class InvalidAcademicDataException extends CandidexException {

    public InvalidAcademicDataException(String message) {
        super("Invalid Academic Data: " + message);
    }
}
