package exceptions;

/**
 * Thrown when a student attempts to apply more than once to the same placement drive.
 */
public class DuplicateApplicationException extends CandidexException {

    private final String studentId;
    private final String driveId;

    public DuplicateApplicationException(String studentId, String driveId) {
        super("Duplicate Application: Student [" + studentId + "] has already applied to Placement Drive [" + driveId + "].");
        this.studentId = studentId;
        this.driveId = driveId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getDriveId() {
        return driveId;
    }
}
