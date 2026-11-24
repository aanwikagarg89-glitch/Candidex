package model;

import model.enums.UserRole;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Concrete entity representing a student applicant.
 * Demonstrates Inheritance (extends User), Polymorphism, and Collections (Set).
 */
public class Student extends User {

    private String rollNumber;
    private String department;
    private AcademicRecord academicRecord;
    private final Set<String> skills;
    private boolean placed;

    public Student(String userId, String fullName, String email, String password, String phoneNumber,
                   String rollNumber, String department, AcademicRecord academicRecord, boolean sampleRecord) {
        super(userId, fullName, email, password, phoneNumber, UserRole.STUDENT, sampleRecord);
        this.rollNumber = rollNumber;
        this.department = department;
        this.academicRecord = academicRecord;
        this.skills = new LinkedHashSet<>();
        this.placed = false;
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.skills.add(skill.trim());
        }
    }

    public void addSkills(String... newSkills) {
        if (newSkills != null) {
            for (String s : newSkills) {
                addSkill(s);
            }
        }
    }

    public boolean removeSkill(String skill) {
        if (skill != null) {
            return this.skills.remove(skill.trim());
        }
        return false;
    }

    public Set<String> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public AcademicRecord getAcademicRecord() {
        return academicRecord;
    }

    public void setAcademicRecord(AcademicRecord academicRecord) {
        this.academicRecord = academicRecord;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    /**
     * Polymorphic implementation of displayProfile().
     */
    @Override
    public void displayProfile() {
        System.out.println("================================================================");
        System.out.println("                     STUDENT PROFILE                            ");
        System.out.println("================================================================");
        System.out.printf("Student ID     : %s %s%n", userId, (sampleRecord ? "[SAMPLE]" : "[REGISTERED]"));
        System.out.printf("Full Name      : %s%n", fullName);
        System.out.printf("Roll Number    : %s%n", rollNumber);
        System.out.printf("Department     : %s%n", department);
        System.out.printf("Email Address  : %s%n", email);
        System.out.printf("Phone Number   : %s%n", phoneNumber);
        System.out.printf("Placement Flag : %s%n", (placed ? "PLACED (Offer Accepted)" : "UNPLACED / SEEKING"));
        System.out.println("----------------------------------------------------------------");
        if (academicRecord != null) {
            System.out.printf("Academic Data  : %s%n", academicRecord.toString());
        } else {
            System.out.println("Academic Data  : Not recorded yet.");
        }
        System.out.printf("Skills         : %s%n", skills.isEmpty() ? "No skills added" : String.join(", ", skills));
        System.out.println("================================================================");
    }
}
