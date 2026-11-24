package repository;

import model.Application;
import model.Interview;
import model.PlacementResult;
import model.enums.ApplicationStatus;

import java.util.*;

/**
 * Unified in-memory recruitment repository managing candidate applications,
 * interview round schedules, composite uniqueness constraints, and finalized placement offers.
 * Demonstrates HashSet for O(1) duplicate checks and LinkedHashMap for relational mapping.
 */
public class ApplicationRepository {

    private final Map<String, Application> applicationById = new LinkedHashMap<>();
    private final Set<String> studentDriveCompositeKeys = new HashSet<>();
    private final Map<String, Interview> interviewById = new LinkedHashMap<>();
    private final Map<String, PlacementResult> resultById = new LinkedHashMap<>();
    private final Map<String, PlacementResult> resultByStudentId = new HashMap<>();

    // --- Application Operations ---

    public synchronized void save(Application app) {
        if (app == null) return;
        applicationById.put(app.getApplicationId(), app);
        String compositeKey = app.getStudentId() + "#" + app.getDriveId();
        studentDriveCompositeKeys.add(compositeKey);
    }

    public synchronized boolean existsByStudentAndDrive(String studentId, String driveId) {
        if (studentId == null || driveId == null) return false;
        return studentDriveCompositeKeys.contains(studentId + "#" + driveId);
    }

    public synchronized Optional<Application> findById(String applicationId) {
        if (applicationId == null) return Optional.empty();
        return Optional.ofNullable(applicationById.get(applicationId));
    }

    public synchronized List<Application> findByStudentId(String studentId) {
        if (studentId == null) return Collections.emptyList();
        List<Application> list = new ArrayList<>();
        for (Application app : applicationById.values()) {
            if (studentId.equalsIgnoreCase(app.getStudentId())) {
                list.add(app);
            }
        }
        return list;
    }

    public synchronized List<Application> findByDriveId(String driveId) {
        if (driveId == null) return Collections.emptyList();
        List<Application> list = new ArrayList<>();
        for (Application app : applicationById.values()) {
            if (driveId.equalsIgnoreCase(app.getDriveId())) {
                list.add(app);
            }
        }
        return list;
    }

    public synchronized List<Application> findAll() {
        return new ArrayList<>(applicationById.values());
    }

    public synchronized List<Application> findPendingApplications() {
        List<Application> pending = new ArrayList<>();
        for (Application app : applicationById.values()) {
            if (app.getStatus() == ApplicationStatus.SUBMITTED || app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                pending.add(app);
            }
        }
        return pending;
    }

    // --- Interview Round Operations ---

    public synchronized void saveInterview(Interview interview) {
        if (interview == null) return;
        interviewById.put(interview.getInterviewId(), interview);
    }

    public synchronized Optional<Interview> findInterviewById(String interviewId) {
        if (interviewId == null) return Optional.empty();
        return Optional.ofNullable(interviewById.get(interviewId));
    }

    public synchronized List<Interview> findInterviewsByApplicationId(String applicationId) {
        if (applicationId == null) return Collections.emptyList();
        List<Interview> rounds = new ArrayList<>();
        for (Interview i : interviewById.values()) {
            if (applicationId.equalsIgnoreCase(i.getApplicationId())) {
                rounds.add(i);
            }
        }
        return rounds;
    }

    public synchronized List<Interview> findAllInterviews() {
        return new ArrayList<>(interviewById.values());
    }

    // --- Placement Result & Offer Operations ---

    public synchronized void saveResult(PlacementResult result) {
        if (result == null) return;
        resultById.put(result.getResultId(), result);
        resultByStudentId.put(result.getStudentId(), result);
    }

    public synchronized Optional<PlacementResult> findResultById(String resultId) {
        if (resultId == null) return Optional.empty();
        return Optional.ofNullable(resultById.get(resultId));
    }

    public synchronized Optional<PlacementResult> findResultByStudentId(String studentId) {
        if (studentId == null) return Optional.empty();
        return Optional.ofNullable(resultByStudentId.get(studentId));
    }

    public synchronized List<PlacementResult> findAllResults() {
        return new ArrayList<>(resultById.values());
    }

    public synchronized double calculateAverageCtc() {
        if (resultById.isEmpty()) return 0.0;
        double total = 0.0;
        for (PlacementResult r : resultById.values()) {
            total += r.getOfferedCtcLpa();
        }
        return total / resultById.size();
    }
}
