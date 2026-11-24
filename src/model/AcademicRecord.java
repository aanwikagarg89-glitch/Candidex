package model;

import exceptions.InvalidAcademicDataException;

/**
 * Value object encapsulating quantitative academic performance and backlog
 * history.
 * Demonstrates Encapsulation and Data Validation.
 */
public class AcademicRecord {

    private double cgpa;
    private double tenthPercentage;
    private double twelfthPercentage;
    private int activeBacklogs;
    private int totalHistoryBacklogs;
    private int graduationYear;

    public AcademicRecord(double cgpa, double tenthPercentage, double twelfthPercentage,
            int activeBacklogs, int totalHistoryBacklogs, int graduationYear)
            throws InvalidAcademicDataException {
        validate(cgpa, tenthPercentage, twelfthPercentage, activeBacklogs, totalHistoryBacklogs, graduationYear);
        this.cgpa = cgpa;
        this.tenthPercentage = tenthPercentage;
        this.twelfthPercentage = twelfthPercentage;
        this.activeBacklogs = activeBacklogs;
        this.totalHistoryBacklogs = totalHistoryBacklogs;
        this.graduationYear = graduationYear;
    }

    private void validate(double cgpa, double tenth, double twelfth, int active, int history, int year)
            throws InvalidAcademicDataException {
        if (cgpa < 0.0 || cgpa > 10.0) {
            throw new InvalidAcademicDataException("CGPA must be between 0.0 and 10.0. Provided: " + cgpa);
        }
        if (tenth < 0.0 || tenth > 100.0) {
            throw new InvalidAcademicDataException("10th percentage must be between 0.0 and 100.0. Provided: " + tenth);
        }
        if (twelfth < 0.0 || twelfth > 100.0) {
            throw new InvalidAcademicDataException(
                    "12th percentage must be between 0.0 and 100.0. Provided: " + twelfth);
        }
        if (active < 0) {
            throw new InvalidAcademicDataException("Active backlogs cannot be negative. Provided: " + active);
        }
        if (history < active) {
            throw new InvalidAcademicDataException("Total history backlogs cannot be fewer than active backlogs.");
        }
        if (year < 2000 || year > 2100) {
            throw new InvalidAcademicDataException("Graduation year must be realistic (2000-2100). Provided: " + year);
        }
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) throws InvalidAcademicDataException {
        if (cgpa < 0.0 || cgpa > 10.0) {
            throw new InvalidAcademicDataException("CGPA must be between 0.0 and 10.0.");
        }
        this.cgpa = cgpa;
    }

    public double getTenthPercentage() {
        return tenthPercentage;
    }

    public void setTenthPercentage(double tenthPercentage) throws InvalidAcademicDataException {
        if (tenthPercentage < 0.0 || tenthPercentage > 100.0) {
            throw new InvalidAcademicDataException("10th percentage must be between 0.0 and 100.0.");
        }
        this.tenthPercentage = tenthPercentage;
    }

    public double getTwelfthPercentage() {
        return twelfthPercentage;
    }

    public void setTwelfthPercentage(double twelfthPercentage) throws InvalidAcademicDataException {
        if (twelfthPercentage < 0.0 || twelfthPercentage > 100.0) {
            throw new InvalidAcademicDataException("12th percentage must be between 0.0 and 100.0.");
        }
        this.twelfthPercentage = twelfthPercentage;
    }

    public int getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(int activeBacklogs)
            throws InvalidAcademicDataException {
        if (activeBacklogs < 0) {
            throw new InvalidAcademicDataException(
                    "Active backlogs cannot be negative.");
        }
        if (activeBacklogs > totalHistoryBacklogs) {
            throw new InvalidAcademicDataException(
                    "Active backlogs cannot exceed total history backlogs.");
        }
        this.activeBacklogs = activeBacklogs;
    }

    public int getTotalHistoryBacklogs() {
        return totalHistoryBacklogs;
    }

    public void setTotalHistoryBacklogs(int totalHistoryBacklogs)
            throws InvalidAcademicDataException {
        if (totalHistoryBacklogs < 0 ||
                totalHistoryBacklogs < activeBacklogs) {
            throw new InvalidAcademicDataException(
                    "Total history backlogs cannot be negative or less than active backlogs.");
        }
        this.totalHistoryBacklogs = totalHistoryBacklogs;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear)
            throws InvalidAcademicDataException {
        if (graduationYear < 2000 || graduationYear > 2100) {
            throw new InvalidAcademicDataException(
                    "Graduation year must be between 2000 and 2100.");
        }
        this.graduationYear = graduationYear;
    }

    public boolean hasActiveBacklogs() {
        return activeBacklogs > 0;
    }

    @Override
    public String toString() {
        return String.format("CGPA: %.2f | 10th: %.1f%% | 12th: %.1f%% | Active Backlogs: %d | History: %d | Batch: %d",
                cgpa, tenthPercentage, twelfthPercentage, activeBacklogs, totalHistoryBacklogs, graduationYear);
    }
}
