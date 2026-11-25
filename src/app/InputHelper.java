package app;

import utils.ValidationUtil;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Robust console input helper handling user inputs, data type parsing,
 * bounds checking, and graceful error recovery without program termination.
 * Demonstrates Exception Handling (try-catch) and Scanner buffer control.
 */
public final class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
        // Prevent instantiation
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%s [%d - %d]: ", prompt, min, max);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  [!] Please enter an integer between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input format. Please enter a valid integer.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.printf("%s [%.1f - %.1f]: ", prompt, min, max);
            String input = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  [!] Please enter a number between %.2f and %.2f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid decimal number. Please enter a valid numerical value.");
            }
        }
    }

    public static String readString(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = SCANNER.nextLine().trim();
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] This field cannot be left blank. Please enter a value.");
        }
    }

    public static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt + " (e.g. user@institution.edu): ");
            String email = SCANNER.nextLine().trim();
            if (ValidationUtil.isValidEmail(email)) {
                return email;
            }
            System.out.println("  [!] Invalid email address format. Please re-enter.");
        }
    }

    public static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt + " (10-digit number): ");
            String phone = SCANNER.nextLine().trim();
            if (ValidationUtil.isValidPhone(phone)) {
                return phone;
            }
            System.out.println("  [!] Invalid phone number format. Please enter a valid 10-digit number.");
        }
    }

    public static Set<String> readCommaSeparatedSet(String prompt) {
        System.out.print(prompt + " (comma-separated, e.g. CSE, ECE, IT): ");
        String line = SCANNER.nextLine().trim();
        Set<String> resultSet = new LinkedHashSet<>();
        if (!line.isEmpty()) {
            String[] tokens = line.split(",");
            for (String token : tokens) {
                String clean = token.trim();
                if (!clean.isEmpty()) {
                    resultSet.add(clean.toUpperCase());
                }
            }
        }
        return resultSet;
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.printf("%s (Y/N): ", prompt);
            String input = SCANNER.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            }
            if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.println("  [!] Invalid choice. Please enter 'Y' for Yes or 'N' for No.");
        }
    }
}
