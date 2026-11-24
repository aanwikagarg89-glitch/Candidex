package model;

import model.enums.InterviewStatus;

/**
 * Entity representing an interview round scheduled for a candidate.
 * Demonstrates Entity association and Enum state progression.
 */
public class Interview {

    private String interviewId;
    private String applicationId;
    private int roundNumber;
    private String roundName;
    private String scheduledDateTime;
    private String interviewMode;
    private InterviewStatus status;
    private String interviewerFeedback;

    public Interview(String interviewId, String applicationId, int roundNumber, String roundName,
                     String scheduledDateTime, String interviewMode) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        this.scheduledDateTime = scheduledDateTime;
        this.interviewMode = interviewMode;
        this.status = InterviewStatus.SCHEDULED;
        this.interviewerFeedback = "Round scheduled. Awaiting completion.";
    }

    public void recordFeedback(InterviewStatus newStatus, String feedback) {
        this.status = newStatus;
        if (feedback != null && !feedback.trim().isEmpty()) {
            this.interviewerFeedback = feedback.trim();
        }
    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public String getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getInterviewerFeedback() {
        return interviewerFeedback;
    }

    public void setInterviewerFeedback(String interviewerFeedback) {
        this.interviewerFeedback = interviewerFeedback;
    }

    public void displayInterviewCard() {
        System.out.printf("Round %d: %s | Time: %s | Mode: %s | Status: %s | Feedback: %s%n",
                roundNumber, roundName, scheduledDateTime, interviewMode, status.getDescription(), interviewerFeedback);
    }
}
