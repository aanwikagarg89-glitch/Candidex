package model.enums;

/**
 * Enumeration representing the outcome status of an interview round.
 */
public enum InterviewStatus {
    SCHEDULED("Scheduled"),
    CLEARED("Cleared Round"),
    REJECTED("Did Not Clear"),
    ABSENT("Candidate Absent");

    private final String description;

    InterviewStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
