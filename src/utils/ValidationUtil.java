package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Utility class providing string inspection, regex validation, range checks,
 * and date/deadline formatting.
 * Demonstrates String manipulation, regular expressions, Java Time APIs, and static utility design.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,14}$");

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("\\s+", "")).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidCgpa(double cgpa) {
        return cgpa >= 0.0 && cgpa <= 10.0;
    }

    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }

    public static boolean isValidRollNumber(String roll) {
        return isNotEmpty(roll) && roll.trim().length() >= 3;
    }

    public static String getCurrentDateTimeFormatted() {
        return LocalDateTime.now().format(DISPLAY_FORMATTER);
    }

    public static String getCurrentDateOnly() {
        return LocalDateTime.now().format(DATE_ONLY_FORMATTER);
    }

    public static String getRelativeDeadlineDays(int daysAhead) {
        return LocalDateTime.now().plusDays(daysAhead).format(DATE_ONLY_FORMATTER);
    }
}
