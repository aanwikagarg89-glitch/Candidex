package model;

/**
 * Immutable entity representing a finalized job offer and placement outcome.
 * Demonstrates Immutability (final fields, no setters) and Data Integrity.
 */
public class PlacementResult {

    private final String resultId;
    private final String studentId;
    private final String studentRollNumber;
    private final String studentName;
    private final String companyId;
    private final String companyName;
    private final String driveId;
    private final String jobRole;
    private final double offeredCtcLpa;
    private final String offerDate;
    private final String offerLetterReference;

    public PlacementResult(String resultId, String studentId, String studentRollNumber, String studentName,
                           String companyId, String companyName, String driveId, String jobRole,
                           double offeredCtcLpa, String offerDate, String offerLetterReference) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.driveId = driveId;
        this.jobRole = jobRole;
        this.offeredCtcLpa = offeredCtcLpa;
        this.offerDate = offerDate;
        this.offerLetterReference = offerLetterReference;
    }

    public String getResultId() {
        return resultId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDriveId() {
        return driveId;
    }

    public String getJobRole() {
        return jobRole;
    }

    public double getOfferedCtcLpa() {
        return offeredCtcLpa;
    }

    public String getOfferDate() {
        return offerDate;
    }

    public String getOfferLetterReference() {
        return offerLetterReference;
    }

    public void displayOffer() {
        System.out.println("================================================================");
        System.out.println("                   OFFICIAL PLACEMENT OFFER                     ");
        System.out.println("================================================================");
        System.out.printf("Offer Reference: %s%n", offerLetterReference);
        System.out.printf("Candidate      : %s (Roll: %s)%n", studentName, studentRollNumber);
        System.out.printf("Hiring Company : %s%n", companyName);
        System.out.printf("Designation    : %s%n", jobRole);
        System.out.printf("Package (CTC)  : %.2f LPA%n", offeredCtcLpa);
        System.out.printf("Offer Date     : %s%n", offerDate);
        System.out.println("================================================================");
    }
}
