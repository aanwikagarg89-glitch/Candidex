package model;

import model.enums.UserRole;

/**
 * Concrete entity representing a Placement Cell Officer / Administrator.
 * Demonstrates Inheritance (extends User) and Polymorphism.
 */
public class Admin extends User {

    private String employeeId;
    private String designation;
    private String department;

    public Admin(String userId, String fullName, String email, String password, String phoneNumber,
                 String employeeId, String designation, String department, boolean sampleRecord) {
        super(userId, fullName, email, password, phoneNumber, UserRole.ADMIN, sampleRecord);
        this.employeeId = employeeId;
        this.designation = designation;
        this.department = department;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void displayProfile() {
        System.out.println("================================================================");
        System.out.println("                   ADMINISTRATOR PROFILE                        ");
        System.out.println("================================================================");
        System.out.printf("Admin ID       : %s%n", userId);
        System.out.printf("Full Name      : %s%n", fullName);
        System.out.printf("Employee ID    : %s%n", employeeId);
        System.out.printf("Designation    : %s%n", designation);
        System.out.printf("Department     : %s%n", department);
        System.out.printf("Email Address  : %s%n", email);
        System.out.printf("Phone Contact  : %s%n", phoneNumber);
        System.out.println("================================================================");
    }
}
