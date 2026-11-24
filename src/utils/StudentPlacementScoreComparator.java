package utils;

import model.Student;

import java.util.Comparator;

/**
 * Comparator to rank candidates by a holistic placement score:
 * Score = (CGPA * 10) + (SkillCount * 2) - (ActiveBacklogs * 5).
 * Higher score gets top priority.
 * Demonstrates advanced multi-attribute custom comparator logic.
 */
public class StudentPlacementScoreComparator implements Comparator<Student> {

    @Override
    public int compare(Student s1, Student s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;

        double score1 = calculateScore(s1);
        double score2 = calculateScore(s2);

        // Descending order of placement score
        return Double.compare(score2, score1);
    }

    public static double calculateScore(Student s) {
        if (s == null || s.getAcademicRecord() == null) {
            return 0.0;
        }
        double cgpaPart = s.getAcademicRecord().getCgpa() * 10.0;
        double skillPart = s.getSkills().size() * 2.0;
        double backlogPenalty = s.getAcademicRecord().getActiveBacklogs() * 5.0;

        return Math.max(0.0, (cgpaPart + skillPart - backlogPenalty));
    }
}
