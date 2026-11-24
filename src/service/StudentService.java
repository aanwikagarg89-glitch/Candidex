package service;

import exceptions.CandidexException;
import exceptions.InvalidAcademicDataException;
import model.AcademicRecord;
import model.Student;
import repository.StudentRepository;

/**
 * Service managing student profile, academic updates, and skill tags.
 */
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudent(String studentId) throws CandidexException {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new CandidexException("Student not found for ID: " + studentId));
    }
    public void updateBasicDetails(String studentId, String fullName,
                               String phone, String department)
        throws CandidexException {

    Student student = getStudent(studentId);

    student.setFullName(fullName);
    student.setPhoneNumber(phone);
    student.setDepartment(department);

    studentRepository.save(student);
}

    public void updateAcademicRecord(String studentId, double cgpa, double tenth, double twelfth,
                                     int activeBacklogs, int historyBacklogs, int gradYear)
            throws CandidexException, InvalidAcademicDataException {
        Student student = getStudent(studentId);
        AcademicRecord newRecord = new AcademicRecord(cgpa, tenth, twelfth, activeBacklogs, historyBacklogs, gradYear);
        student.setAcademicRecord(newRecord);
        studentRepository.save(student);
    }

    public void addSkill(String studentId, String skill) throws CandidexException {
        Student student = getStudent(studentId);
        student.addSkill(skill);
        studentRepository.save(student);
    }

    public boolean removeSkill(String studentId, String skill) throws CandidexException {
        Student student = getStudent(studentId);
        boolean removed = student.removeSkill(skill);
        if (removed) {
            studentRepository.save(student);
        }
        return removed;
    }
}
