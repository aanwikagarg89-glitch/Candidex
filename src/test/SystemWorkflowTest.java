package test;

import database.DBConnectionManager;
import exceptions.DuplicateApplicationException;
import exceptions.IneligibleStudentException;
import model.*;
import model.enums.InterviewStatus;
import repository.DataStore;
import service.*;
import threading.BatchProcessingManager;
import utils.ValidationUtil;

import java.util.*;

/**
 * Comprehensive automated verification test suite verifying all system workflows:
 * 1. DataStore seeding & sample vs. registered record demarcation
 * 2. Interactive student registration with auto ID generation & validation
 * 3. Dynamic eligibility evaluation (pass & fail diagnostic cases)
 * 4. Application submission & duplicate prevention
 * 5. Concurrent multithreaded batch application verification (Producer-Consumer)
 * 6. Candidate ranking (CGPA, Placement Score, PriorityQueue)
 * 7. Interview scheduling and final job offer generation
 * 8. Institutional TPO report generation
 * 9. JDBC architecture & connection metadata verification
 */
public class SystemWorkflowTest {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("           CANDIDEX AUTOMATED SYSTEM WORKFLOW TEST              ");
        System.out.println("================================================================");

        int passed = 0;
        int failed = 0;

        try {
            // STEP 1: Initialize DataStore
            DataStore dataStore = new DataStore();
            dataStore.seedDemonstrationData();
            assertCondition(dataStore.getStudentRepository().findAll().size() >= 3, "Sample students seeded");
            assertCondition(dataStore.getDriveRepository().findAllCompanies().size() >= 3, "Sample companies seeded");
            assertCondition(dataStore.getDriveRepository().findAllDrives().size() >= 3, "Sample drives seeded");
            passed++;

            // STEP 2: Register New Student
            AuthService authService = new AuthService(dataStore);
            AcademicRecord newRecord = new AcademicRecord(8.85, 91.0, 89.5, 0, 0, 2026);
            Student newStudent = authService.registerStudent(
                    "Pooja Hegde",
                    "pooja.hegde@campus.edu",
                    "securePass123",
                    "9876543210",
                    "22CS999",
                    "CSE",
                    newRecord
            );
            newStudent.addSkills("Java", "Spring Boot", "Microservices", "Docker");
            dataStore.getStudentRepository().save(newStudent);

            assertCondition(newStudent.getUserId().startsWith("STU-"), "Student ID auto-generated (" + newStudent.getUserId() + ")");
            assertCondition(!newStudent.isSampleRecord(), "Student correctly tagged as user-registered (not sample)");
            passed++;

            // STEP 3: Register New Company & Post Drive
            Company newComp = authService.registerCompany(
                    "Amazon Web Services",
                    "campus-hiring@amazon.com",
                    "awsSecure2026",
                    "9988776655",
                    "Cloud Infrastructure",
                    "https://amazon.jobs",
                    "Siddharth Roy"
            );
            assertCondition(newComp.getUserId().startsWith("CMP-"), "Company ID auto-generated (" + newComp.getUserId() + ")");
            passed++;

            DriveService driveService = new DriveService(dataStore.getDriveRepository());
            Set<String> eligibleBranches = new HashSet<>(Arrays.asList("CSE", "IT"));
            EligibilityCriteria criteria = new EligibilityCriteria(8.00, 80.0, 80.0, 0, eligibleBranches, 2026);
            PlacementDrive awsDrive = driveService.createDrive(
                    newComp.getUserId(),
                    newComp.getCompanyName(),
                    "Cloud Support Associate",
                    "Troubleshoot distributed cloud architectures and services.",
                    19.5,
                    "Hyderabad",
                    criteria,
                    ValidationUtil.getRelativeDeadlineDays(25)
            );
            assertCondition(awsDrive.getDriveId().startsWith("DRV-"), "Drive ID auto-generated (" + awsDrive.getDriveId() + ")");
            passed++;

            // STEP 4: Dynamic Eligibility Evaluation
            boolean isEligible = driveService.isEligible(newStudent, awsDrive);
            assertCondition(isEligible, "Pooja Hegde is dynamically ELIGIBLE for AWS drive");

            // Test student who is ineligible (Rohan Iyer has 2 backlogs and is from MECH)
            Student rohan = dataStore.getStudentRepository().findByRollNumber("22ME310").orElseThrow();
            List<String> rohanFailures = driveService.getEligibilityDiagnosticReasons(rohan, awsDrive);
            assertCondition(!rohanFailures.isEmpty(), "Rohan Iyer is correctly identified as INELIGIBLE for AWS");
            assertCondition(rohanFailures.size() >= 2, "Detailed diagnostics contain multiple failure reasons");
            passed++;

            // STEP 5: Apply for Drive & Test Duplicate Application Blocking
            ApplicationService appService = new ApplicationService(
                    dataStore.getApplicationRepository(),
                    dataStore.getStudentRepository(),
                    dataStore.getDriveRepository(),
                    driveService
            );

            Application app1 = appService.applyForDrive(newStudent.getUserId(), awsDrive.getDriveId());
            assertCondition(app1.getApplicationId().startsWith("APP-"), "Application ID auto-generated (" + app1.getApplicationId() + ")");
            passed++;

            // Test duplicate application prevention
            boolean duplicateBlocked = false;
            try {
                appService.applyForDrive(newStudent.getUserId(), awsDrive.getDriveId());
            } catch (DuplicateApplicationException e) {
                duplicateBlocked = true;
            }
            assertCondition(duplicateBlocked, "DuplicateApplicationException successfully thrown on re-application");
            passed++;

            // Test ineligible application blocking
            boolean ineligibleBlocked = false;
            try {
                appService.applyForDrive(rohan.getUserId(), awsDrive.getDriveId());
            } catch (IneligibleStudentException e) {
                ineligibleBlocked = true;
            }
            assertCondition(ineligibleBlocked, "IneligibleStudentException successfully thrown for ineligible candidate");
            passed++;

            // STEP 6: Multithreaded Batch Application Processing
            BatchProcessingManager batchManager = new BatchProcessingManager(
                    dataStore.getStudentRepository(),
                    dataStore.getDriveRepository(),
                    dataStore.getApplicationRepository(),
                    driveService
            );
            List<Application> pending = dataStore.getApplicationRepository().findPendingApplications();
            assertCondition(!pending.isEmpty(), "Pending applications found for batch processing");
            batchManager.executeBatchProcessing(pending);
            passed++;

            // STEP 7: Candidate Ranking & PriorityQueue
            RankingService rankingService = new RankingService();
            List<Student> allStudents = dataStore.getStudentRepository().findAll();
            List<Student> rankedByCgpa = rankingService.rankByCgpa(allStudents);
            assertCondition(rankedByCgpa.get(0).getAcademicRecord().getCgpa() >= rankedByCgpa.get(1).getAcademicRecord().getCgpa(),
                    "Students correctly sorted in descending CGPA order");

            List<Student> top2 = rankingService.getTopKCandidates(allStudents, 2);
            assertCondition(top2.size() == 2, "PriorityQueue correctly extracted top 2 candidates");
            passed++;

            // STEP 8: Interview Scheduling & Job Offer Extension
            Interview interview = appService.scheduleRound(
                    app1.getApplicationId(),
                    1,
                    "Technical Coding & Systems Round",
                    "30-11-2026 11:00 AM",
                    "Online Google Meet"
            );
            assertCondition(interview.getInterviewId().startsWith("INT-"), "Interview round scheduled");

            appService.updateInterviewResult(interview.getInterviewId(), InterviewStatus.CLEARED, "Demonstrated strong Java concurrency and collections mastery.");
            PlacementResult offer = appService.extendJobOffer(app1.getApplicationId(), 19.5);
            assertCondition(offer.getResultId().startsWith("OFFER-"), "Final offer generated (" + offer.getResultId() + ")");
            assertCondition(newStudent.isPlaced(), "Student placement flag updated to PLACED");
            passed++;

            // STEP 9: TPO Reports
            ReportService reportService = new ReportService(
                    dataStore.getStudentRepository(),
                    dataStore.getDriveRepository(),
                    dataStore.getApplicationRepository()
            );
            reportService.generateOverallPlacementSummary();
            reportService.generateBranchWiseReport();
            reportService.generateCompanyWiseReport();
            passed++;

            // STEP 10: JDBC Metadata & Architectural Compliance
            DBConnectionManager.displayMetadata();
            DBConnectionManager.printJdbcVivaArchitecture();
            passed++;

            System.out.println("\n================================================================");
            System.out.printf("  ✔ ALL SYSTEM VERIFICATION CHECKS PASSED: %d / %d%n", passed, (passed + failed));
            System.out.println("================================================================");

        } catch (Exception e) {
            System.err.println("Test suite encountered unexpected failure: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }
    }

    private static void assertCondition(boolean condition, String description) {
        if (condition) {
            System.out.println("  ✔ PASS: " + description);
        } else {
            System.err.println("  ✘ FAIL: " + description);
            throw new AssertionError("Test assertion failed: " + description);
        }
    }
}
