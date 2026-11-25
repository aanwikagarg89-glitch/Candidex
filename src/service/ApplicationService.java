package service;

import exceptions.CandidexException;
import exceptions.DuplicateApplicationException;
import exceptions.IneligibleStudentException;
import model.*;
import model.enums.ApplicationStatus;
import model.enums.InterviewStatus;
import repository.ApplicationRepository;
import repository.DriveRepository;
import repository.StudentRepository;
import utils.IdGenerator;
import utils.ValidationUtil;

import java.util.List;

/**
 * Consolidated recruitment service managing candidate applications,
 * live eligibility checks, interview schedules, round feedback, and final job offers.
 */
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final DriveService driveService;

    public ApplicationService(ApplicationRepository appRepo, StudentRepository studentRepo,
                              DriveRepository driveRepo, DriveService driveService) {
        this.applicationRepository = appRepo;
        this.studentRepository = studentRepo;
        this.driveRepository = driveRepo;
        this.driveService = driveService;
    }

    /**
     * Submits an application for a placement drive.
     * Enforces drive status, duplicate application checks, and academic eligibility rules.
     */
    public Application applyForDrive(String studentId, String driveId)
            throws CandidexException, DuplicateApplicationException, IneligibleStudentException {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CandidexException("Student not found for ID: " + studentId));

        PlacementDrive drive = driveRepository.findDriveById(driveId)
                .orElseThrow(() -> new CandidexException("Placement Drive not found: " + driveId));

        if (!drive.isOpen()) {
            throw new CandidexException("Placement Drive [" + driveId + "] is currently closed or has passed its deadline.");
        }

        if (applicationRepository.existsByStudentAndDrive(studentId, driveId)) {
            throw new DuplicateApplicationException(studentId, driveId);
        }

        // Live Dynamic Eligibility Check via DriveService
        List<String> ineligibilityReasons = driveService.getEligibilityDiagnosticReasons(student, drive);
        if (!ineligibilityReasons.isEmpty()) {
            throw new IneligibleStudentException("Student does not meet the eligibility requirements for this drive.", ineligibilityReasons);
        }

        // Auto-generate Application ID
        String appId = IdGenerator.generateApplicationId();
        Application application = new Application(
                appId,
                student.getUserId(),
                student.getRollNumber(),
                student.getFullName(),
                drive.getDriveId(),
                drive.getCompanyName(),
                drive.getJobRole(),
                ValidationUtil.getCurrentDateTimeFormatted(),
                false
        );

        applicationRepository.save(application);
        return application;
    }

    public List<Application> getStudentApplications(String studentId) {
        return applicationRepository.findByStudentId(studentId);
    }

    public List<Application> getDriveApplications(String driveId) {
        return applicationRepository.findByDriveId(driveId);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplication(String applicationId) throws CandidexException {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CandidexException("Application [" + applicationId + "] not found."));
    }

    public void updateStatus(String applicationId, ApplicationStatus status, String remarks)
            throws CandidexException {
        Application application = getApplication(applicationId);
        application.updateStatus(status, remarks);
        applicationRepository.save(application);
    }

    // --- Interview Round & Offer Operations ---

    public Interview scheduleRound(String applicationId, int roundNumber, String roundName,
                                   String scheduledDateTime, String mode) throws CandidexException {
        Application application = getApplication(applicationId);

        String interviewId = IdGenerator.generateInterviewId();
        Interview interview = new Interview(interviewId, applicationId, roundNumber, roundName, scheduledDateTime, mode);
        applicationRepository.saveInterview(interview);

        application.updateStatus(ApplicationStatus.INTERVIEW_SCHEDULED, "Round " + roundNumber + " (" + roundName + ") scheduled.");
        applicationRepository.save(application);

        return interview;
    }

    public void updateInterviewResult(String interviewId, InterviewStatus outcome, String feedback)
            throws CandidexException {
        Interview interview = applicationRepository.findInterviewById(interviewId)
                .orElseThrow(() -> new CandidexException("Interview [" + interviewId + "] not found."));

        interview.recordFeedback(outcome, feedback);
        applicationRepository.saveInterview(interview);

        // Update parent application remarks
        Application application = applicationRepository.findById(interview.getApplicationId()).orElse(null);
        if (application != null) {
            if (outcome == InterviewStatus.CLEARED) {
                application.setRemarks("Cleared " + interview.getRoundName() + ".");
            } else if (outcome == InterviewStatus.REJECTED) {
                application.updateStatus(ApplicationStatus.REJECTED, "Did not clear " + interview.getRoundName() + ".");
            }
            applicationRepository.save(application);
        }
    }

    public PlacementResult extendJobOffer(String applicationId, double packageCtcLpa) throws CandidexException {
        Application application = getApplication(applicationId);

        application.updateStatus(ApplicationStatus.SELECTED, "Selected! Offer extended with package " + packageCtcLpa + " LPA.");
        applicationRepository.save(application);

        // Mark student as placed
        Student student = studentRepository.findById(application.getStudentId()).orElse(null);
        if (student != null) {
            student.setPlaced(true);
            studentRepository.save(student);
        }

        String resultId = IdGenerator.generateResultId();
        String offerLetterRef = "OFFER-CANDIDEX-" + System.currentTimeMillis() % 100000;
        PlacementResult result = new PlacementResult(
                resultId,
                application.getStudentId(),
                application.getStudentRollNumber(),
                application.getStudentName(),
                "CMP-N/A",
                application.getCompanyName(),
                application.getDriveId(),
                application.getJobRole(),
                packageCtcLpa,
                ValidationUtil.getCurrentDateOnly(),
                offerLetterRef
        );

        applicationRepository.saveResult(result);
        return result;
    }

    public List<Interview> getInterviewsForApplication(String applicationId) {
        return applicationRepository.findInterviewsByApplicationId(applicationId);
    }
}
