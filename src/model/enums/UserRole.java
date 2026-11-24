package model.enums;

/**
 * Enumeration representing the distinct user roles in CANDIDEX.
 */
public enum UserRole {
    STUDENT("Student"),
    ADMIN("Administrator (TPO)"),
    COMPANY("Company (Recruiter)");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
