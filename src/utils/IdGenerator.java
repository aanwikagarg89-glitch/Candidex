package utils;

/**
 * Thread-safe identifier sequence generator for domain entities.
 * Generates enterprise IDs like STU-1001, CMP-1001, DRV-1001, etc.
 * Demonstrates static members and synchronization.
 */
public final class IdGenerator {

    private static int studentCounter = 1002;
    private static int adminCounter = 1000;
    private static int companyCounter = 1002;
    private static int driveCounter = 1002;
    private static int applicationCounter = 1000;
    private static int interviewCounter = 1000;
    private static int resultCounter = 1000;

    private IdGenerator() {
        // Prevent instantiation
    }

    public static synchronized String generateStudentId() {
        return "STU-" + (++studentCounter);
    }

    public static synchronized String generateAdminId() {
        return "ADM-" + (++adminCounter);
    }

    public static synchronized String generateCompanyId() {
        return "CMP-" + (++companyCounter);
    }

    public static synchronized String generateDriveId() {
        return "DRV-" + (++driveCounter);
    }

    public static synchronized String generateApplicationId() {
        return "APP-" + (++applicationCounter);
    }

    public static synchronized String generateInterviewId() {
        return "INT-" + (++interviewCounter);
    }

    public static synchronized String generateResultId() {
        return "OFFER-" + (++resultCounter);
    }
}
