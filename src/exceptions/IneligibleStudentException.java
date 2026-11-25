package exceptions;

import java.util.Collections;
import java.util.List;

/**
 * Thrown when a student attempts to apply for a placement drive without meeting the criteria.
 * Carries specific diagnostic failure reasons for clear feedback.
 */
public class IneligibleStudentException extends CandidexException {

    private final List<String> failureReasons;

    public IneligibleStudentException(String message, List<String> failureReasons) {
        super(message);
        this.failureReasons = failureReasons != null ? failureReasons : Collections.emptyList();
    }

    public List<String> getFailureReasons() {
        return Collections.unmodifiableList(failureReasons);
    }
}
