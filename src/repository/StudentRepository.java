package repository;

import model.Student;

import java.util.*;

/**
 * In-memory repository for Student entities.
 * Demonstrates Collections (HashMap, ArrayList), Optional, and defensive copying.
 */
public class StudentRepository {

    private final Map<String, Student> studentById = new LinkedHashMap<>();
    private final Map<String, Student> studentByRoll = new HashMap<>();
    private final Map<String, Student> studentByEmail = new HashMap<>();

    public synchronized void save(Student student) {
        if (student == null) return;
        studentById.put(student.getUserId(), student);
        if (student.getRollNumber() != null) {
            studentByRoll.put(student.getRollNumber().toUpperCase(), student);
        }
        if (student.getEmail() != null) {
            studentByEmail.put(student.getEmail().toLowerCase(), student);
        }
    }

    public synchronized Optional<Student> findById(String userId) {
        if (userId == null) return Optional.empty();
        return Optional.ofNullable(studentById.get(userId));
    }

    public synchronized Optional<Student> findByRollNumber(String rollNumber) {
        if (rollNumber == null) return Optional.empty();
        return Optional.ofNullable(studentByRoll.get(rollNumber.toUpperCase()));
    }

    public synchronized Optional<Student> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(studentByEmail.get(email.toLowerCase()));
    }

    public synchronized boolean existsByRollNumber(String rollNumber) {
        if (rollNumber == null) return false;
        return studentByRoll.containsKey(rollNumber.toUpperCase());
    }

    public synchronized boolean existsByEmail(String email) {
        if (email == null) return false;
        return studentByEmail.containsKey(email.toLowerCase());
    }

    public synchronized List<Student> findAll() {
        return new ArrayList<>(studentById.values());
    }

    public synchronized List<Student> findByDepartment(String dept) {
        if (dept == null) return Collections.emptyList();
        List<Student> result = new ArrayList<>();
        for (Student s : studentById.values()) {
            if (dept.equalsIgnoreCase(s.getDepartment())) {
                result.add(s);
            }
        }
        return result;
    }

    public synchronized List<Student> findUnplaced() {
        List<Student> result = new ArrayList<>();
        for (Student s : studentById.values()) {
            if (!s.isPlaced()) {
                result.add(s);
            }
        }
        return result;
    }

    public synchronized List<Student> findPlaced() {
        List<Student> result = new ArrayList<>();
        for (Student s : studentById.values()) {
            if (s.isPlaced()) {
                result.add(s);
            }
        }
        return result;
    }
}
