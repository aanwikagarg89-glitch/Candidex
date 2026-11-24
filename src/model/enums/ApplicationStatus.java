package model.enums;

/**
 * Enumeration representing the progression stages of a student application.
 */
public enum ApplicationStatus {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    SHORTLISTED("Shortlisted"),
    INTERVIEW_SCHEDULED("Interview Scheduled"),
    REJECTED("Not Selected"),
    SELECTED("Selected / Offer Extended");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
