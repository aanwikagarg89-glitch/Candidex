package model;

import model.enums.ApplicationStatus;

/**
 * Entity representing an application submitted by a student for a specific placement drive.
 * Demonstrates State encapsulation and Entity association.
 */
public class Application {

    private String applicationId;
    private String studentId;
    private String studentRollNumber;
    private String studentName;
    private String driveId;
    private String companyName;
    private String jobRole;
    private String appliedDate;
    private ApplicationStatus status;
    private String remarks;
    private boolean sampleRecord;

    public Application(String applicationId, String studentId, String studentRollNumber, String studentName,
                       String driveId, String companyName, String jobRole, String appliedDate, boolean sampleRecord) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.appliedDate = appliedDate;
        this.status = ApplicationStatus.SUBMITTED;
        this.remarks = "Application received and queued for review.";
        this.sampleRecord = sampleRecord;
    }

    public void updateStatus(ApplicationStatus newStatus, String remarks) {
        this.status = newStatus;
        if (remarks != null && !remarks.trim().isEmpty()) {
            this.remarks = remarks.trim();
        }
    }

    public boolean isFinalized() {
        return this.status == ApplicationStatus.SELECTED || this.status == ApplicationStatus.REJECTED;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
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

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSampleRecord() {
        return sampleRecord;
    }

    public void setSampleRecord(boolean sampleRecord) {
        this.sampleRecord = sampleRecord;
    }

    public void displaySummary() {
        System.out.printf("[%s] ID: %s | Student: %s (%s) | Role: %s @ %s | Status: %s | Remarks: %s%n",
                (sampleRecord ? "SAMPLE" : "LIVE"), applicationId, studentName, studentRollNumber,
                jobRole, companyName, status.getDescription(), remarks);
    }
}
