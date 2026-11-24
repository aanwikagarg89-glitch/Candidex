package model.enums;

/**
 * Enumeration representing the operational status of a placement drive.
 */
public enum DriveStatus {
    UPCOMING("Upcoming"),
    OPEN("Open for Applications"),
    IN_PROGRESS("Recruitment In Progress"),
    CLOSED("Drive Closed");

    private final String description;

    DriveStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
