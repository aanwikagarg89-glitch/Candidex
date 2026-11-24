package service;

import exceptions.CandidexException;
import model.Admin;
import model.Company;
import model.Student;
import model.User;
import repository.DataStore;
import repository.DriveRepository;
import repository.StudentRepository;
import utils.IdGenerator;
import utils.ValidationUtil;

import java.util.Optional;

/**
 * Service managing user authentication, role-based login verification,
 * and new user registration with input validation.
 */
public class AuthService {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final Admin admin;

    public AuthService(DataStore dataStore) {
        this.studentRepository = dataStore.getStudentRepository();
        this.driveRepository = dataStore.getDriveRepository();
        this.admin = dataStore.getDefaultAdmin();
    }

    /**
     * Authenticates user credentials across Admin, Student, and Company records.
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        String cleanEmail = email.trim().toLowerCase();

        // Check Admin
        if (admin != null && admin.getEmail().equalsIgnoreCase(cleanEmail) && admin.verifyPassword(password)) {
            return Optional.of(admin);
        }

        // Check Students
        Optional<Student> studentOpt = studentRepository.findByEmail(cleanEmail);
        if (studentOpt.isPresent() && studentOpt.get().verifyPassword(password)) {
            return Optional.of(studentOpt.get());
        }

        // Check Companies
        Optional<Company> companyOpt = driveRepository.findCompanyByEmail(cleanEmail);
        if (companyOpt.isPresent() && companyOpt.get().verifyPassword(password)) {
            return Optional.of(companyOpt.get());
        }

        return Optional.empty();
    }

    /**
     * Registers a new student with auto-generated ID and validation.
     */
    public Student registerStudent(String fullName, String email, String password, String phoneNumber,
                                   String rollNumber, String department, model.AcademicRecord academicRecord)
            throws CandidexException {

        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new CandidexException("Full Name cannot be empty.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new CandidexException("Invalid email format: " + email);
        }
        if (studentRepository.existsByEmail(email)) {
            throw new CandidexException("A student with this email address already exists.");
        }
        if (!ValidationUtil.isValidRollNumber(rollNumber)) {
            throw new CandidexException("Roll Number must be at least 3 characters long.");
        }
        if (studentRepository.existsByRollNumber(rollNumber)) {
            throw new CandidexException("A student with roll number [" + rollNumber + "] is already registered.");
        }
        if (!ValidationUtil.isValidPhone(phoneNumber)) {
            throw new CandidexException("Invalid phone number format.");
        }
        if (password == null || password.length() < 4) {
            throw new CandidexException("Password must be at least 4 characters long.");
        }

        // Auto-generate enterprise student ID
        String studentId = IdGenerator.generateStudentId();
        Student newStudent = new Student(studentId, fullName.trim(), email.trim().toLowerCase(),
                password, phoneNumber.trim(), rollNumber.trim().toUpperCase(), department.trim().toUpperCase(),
                academicRecord, false);

        studentRepository.save(newStudent);
        return newStudent;
    }

    /**
     * Registers a new company with auto-generated ID and validation.
     */
    public Company registerCompany(String companyName, String email, String password, String phoneNumber,
                                   String industryDomain, String websiteUrl, String hrContactPerson)
            throws CandidexException {

        if (!ValidationUtil.isNotEmpty(companyName)) {
            throw new CandidexException("Company Name cannot be empty.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new CandidexException("Invalid company email format: " + email);
        }
        if (driveRepository.existsCompanyByEmail(email)) {
            throw new CandidexException("A company with this email address already exists.");
        }
        if (!ValidationUtil.isValidPhone(phoneNumber)) {
            throw new CandidexException("Invalid phone number format.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new CandidexException("Password must be at least 4 characters.");
        }

        // Auto-generate enterprise company ID
        String companyId = IdGenerator.generateCompanyId();
        Company newCompany = new Company(companyId, companyName.trim(), email.trim().toLowerCase(),
                password, phoneNumber.trim(), industryDomain.trim(), websiteUrl.trim(), hrContactPerson.trim(), false);

        driveRepository.saveCompany(newCompany);
        return newCompany;
    }
}
