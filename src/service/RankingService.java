package service;

import model.Student;
import utils.StudentCGPAComparator;
import utils.StudentPlacementScoreComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Service providing ranking and sorting algorithms for applicants.
 * Demonstrates Collections.sort, Comparators, and PriorityQueue data structure.
 */
public class RankingService {

    private final StudentCGPAComparator cgpaComparator = new StudentCGPAComparator();
    private final StudentPlacementScoreComparator scoreComparator = new StudentPlacementScoreComparator();

    /**
     * Ranks a list of students strictly by CGPA in descending order.
     */
    public List<Student> rankByCgpa(List<Student> students) {
        if (students == null) return Collections.emptyList();
        List<Student> sortedList = new ArrayList<>(students);
        sortedList.sort(cgpaComparator);
        return sortedList;
    }

    /**
     * Ranks students by their holistic placement score (CGPA + Skills - Backlog Penalty).
     */
    public List<Student> rankByPlacementScore(List<Student> students) {
        if (students == null) return Collections.emptyList();
        List<Student> sortedList = new ArrayList<>(students);
        sortedList.sort(scoreComparator);
        return sortedList;
    }

    /**
     * Extracts top-K candidates using a PriorityQueue (demonstrating PriorityQueue collection).
     */
    public List<Student> getTopKCandidates(List<Student> students, int k) {
        if (students == null || students.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        // PriorityQueue configured with scoreComparator
        PriorityQueue<Student> pq = new PriorityQueue<>(scoreComparator);
        pq.addAll(students);

        List<Student> topK = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < k) {
            topK.add(pq.poll());
            count++;
        }
        return topK;
    }
}
