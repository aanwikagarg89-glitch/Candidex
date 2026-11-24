package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.enums.DriveStatus;

/**
 * Entity representing a Campus Placement Drive hosted by a company.
 * Demonstrates Composition (contains EligibilityCriteria) and Enum integration.
 */
public class PlacementDrive {

    private String driveId;
    private String companyId;
    private String companyName;
    private String jobRole;
    private String jobDescription;
    private double packageCtcLpa;
    private String jobLocation;
    private EligibilityCriteria criteria;
    private String applicationDeadline;
    private DriveStatus status;
    private boolean sampleRecord;

    public PlacementDrive(String driveId, String companyId, String companyName, String jobRole,
                          String jobDescription, double packageCtcLpa, String jobLocation,
                          EligibilityCriteria criteria, String applicationDeadline, boolean sampleRecord) {
        this.driveId = driveId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.jobDescription = jobDescription;
        this.packageCtcLpa = packageCtcLpa;
        this.jobLocation = jobLocation;
        this.criteria = criteria;
        this.applicationDeadline = applicationDeadline;
        this.status = DriveStatus.OPEN;
        this.sampleRecord = sampleRecord;
    }

    
    public boolean isOpen() {
        if (this.status != DriveStatus.OPEN) {
            return false;
        }
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate deadline = LocalDate.parse(
                    applicationDeadline, formatter
            );
            return !LocalDate.now().isAfter(deadline);
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public void closeDrive() {
        this.status = DriveStatus.CLOSED;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public double getPackageCtcLpa() {
        return packageCtcLpa;
    }

    public void setPackageCtcLpa(double packageCtcLpa) {
        this.packageCtcLpa = packageCtcLpa;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public EligibilityCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(EligibilityCriteria criteria) {
        this.criteria = criteria;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public DriveStatus getStatus() {
        return status;
    }

    public void setStatus(DriveStatus status) {
        this.status = status;
    }

    public boolean isSampleRecord() {
        return sampleRecord;
    }

    public void setSampleRecord(boolean sampleRecord) {
        this.sampleRecord = sampleRecord;
    }

    public void displayDriveSummary() {
        System.out.println("----------------------------------------------------------------");
        System.out.printf("Drive ID     : %s %s%n", driveId, (sampleRecord ? "[SAMPLE]" : "[USER CREATED]"));
        System.out.printf("Company      : %s (ID: %s)%n", companyName, companyId);
        System.out.printf("Role         : %s%n", jobRole);
        System.out.printf("Package CTC  : %.2f LPA%n", packageCtcLpa);
        System.out.printf("Location     : %s%n", jobLocation);
        System.out.printf("Deadline     : %s%n", applicationDeadline);
        System.out.printf("Status       : %s%n", status.getDescription());
        System.out.printf("Eligibility  : %s%n", (criteria != null ? criteria.toString() : "Open to All"));
        System.out.println("----------------------------------------------------------------");
    }
}
