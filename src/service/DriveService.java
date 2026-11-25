package service;

import exceptions.CandidexException;
import model.AcademicRecord;
import model.Company;
import model.EligibilityCriteria;
import model.PlacementDrive;
import model.Student;
import repository.DriveRepository;
import utils.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Consolidated service orchestrating Recruiting Company profiles, Placement Drive lifecycles,
 * and the dynamic eligibility rule engine.
 */
public class DriveService {

    private final DriveRepository driveRepository;

    public DriveService(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    // --- Company Profile Operations ---

    public Company getCompany(String companyId) throws CandidexException {
        return driveRepository.findCompanyById(companyId)
                .orElseThrow(() -> new CandidexException("Company [" + companyId + "] not found."));
    }

    public void updateCompanyProfile(String companyId, String industry, String website, String hrContact, String phone)
            throws CandidexException {
        Company company = getCompany(companyId);
        if (industry != null && !industry.trim().isEmpty()) {
            company.setIndustryDomain(industry.trim());
        }
        if (website != null && !website.trim().isEmpty()) {
            company.setWebsiteUrl(website.trim());
        }
        if (hrContact != null && !hrContact.trim().isEmpty()) {
            company.setHrContactPerson(hrContact.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            company.setPhoneNumber(phone.trim());
        }
        driveRepository.saveCompany(company);
    }

    // --- Placement Drive Operations ---

    public PlacementDrive createDrive(String companyId, String companyName, String jobRole, String description,
                                      double packageCtcLpa, String location, EligibilityCriteria criteria,
                                      String deadlineDate) {
        String driveId = IdGenerator.generateDriveId();
        PlacementDrive drive = new PlacementDrive(driveId, companyId, companyName, jobRole, description,
                packageCtcLpa, location, criteria, deadlineDate, false);
        driveRepository.saveDrive(drive);
        return drive;
    }

    public List<PlacementDrive> getOpenDrives() {
        return driveRepository.findOpenDrives();
    }

    public List<PlacementDrive> getAllDrives() {
        return driveRepository.findAllDrives();
    }

    public List<PlacementDrive> getDrivesByCompany(String companyId) {
        return driveRepository.findDrivesByCompanyId(companyId);
    }

    public PlacementDrive getDrive(String driveId) throws CandidexException {
        return driveRepository.findDriveById(driveId)
                .orElseThrow(() -> new CandidexException("Placement Drive [" + driveId + "] not found."));
    }

    public void closeDrive(String driveId) throws CandidexException {
        PlacementDrive drive = getDrive(driveId);
        drive.closeDrive();
        driveRepository.saveDrive(drive);
    }

    // --- Dynamic Eligibility Engine Operations ---

    /**
     * Assesses whether a student meets all eligibility parameters for a placement drive.
     */
    public boolean isEligible(Student student, PlacementDrive drive) {
        if (student == null || drive == null) {
            return false;
        }
        return getEligibilityDiagnosticReasons(student, drive).isEmpty();
    }

    /**
     * Evaluates criteria and returns a list of reasons if ineligible.
     * If the list is empty, the student is fully eligible.
     */
    public List<String> getEligibilityDiagnosticReasons(Student student, PlacementDrive drive) {
        if (student == null || drive == null) {
            return Collections.singletonList("Invalid student or drive reference.");
        }

        EligibilityCriteria criteria = drive.getCriteria();
        if (criteria == null) {
            return Collections.emptyList(); // No restrictions specified
        }

        AcademicRecord record = student.getAcademicRecord();
        if (record == null) {
            return Collections.singletonList("Student academic record is incomplete or missing.");
        }

        List<String> failures = new ArrayList<>();

        // 1. CGPA Criterion
        if (record.getCgpa() < criteria.getMinCgpa()) {
            failures.add(String.format("CGPA shortfall: Student CGPA (%.2f) is below required minimum (%.2f).",
                    record.getCgpa(), criteria.getMinCgpa()));
        }

        // 2. 10th Percentage Criterion
        if (record.getTenthPercentage() < criteria.getMinTenthPercentage()) {
            failures.add(String.format("10th marks shortfall: Student percentage (%.1f%%) < Required (%.1f%%).",
                    record.getTenthPercentage(), criteria.getMinTenthPercentage()));
        }

        // 3. 12th Percentage Criterion
        if (record.getTwelfthPercentage() < criteria.getMinTwelfthPercentage()) {
            failures.add(String.format("12th marks shortfall: Student percentage (%.1f%%) < Required (%.1f%%).",
                    record.getTwelfthPercentage(), criteria.getMinTwelfthPercentage()));
        }

        // 4. Active Backlogs Restriction
        if (record.getActiveBacklogs() > criteria.getMaxAllowedActiveBacklogs()) {
            failures.add(String.format("Backlog restriction: Student has %d active backlogs (Max permitted: %d).",
                    record.getActiveBacklogs(), criteria.getMaxAllowedActiveBacklogs()));
        }

        // 5. Eligible Branches / Departments
        if (!criteria.isBranchAllowed(student.getDepartment())) {
            failures.add(String.format("Branch restriction: Student branch [%s] is not in permitted branches %s.",
                    student.getDepartment(), criteria.getAllowedBranches()));
        }

        // 6. Graduation Batch Year
        if (criteria.getGraduationBatchYear() > 0 && record.getGraduationYear() != criteria.getGraduationBatchYear()) {
            failures.add(String.format("Graduation batch mismatch: Expected batch %d, but student is from %d.",
                    criteria.getGraduationBatchYear(), record.getGraduationYear()));
        }

        return failures;
    }
}
