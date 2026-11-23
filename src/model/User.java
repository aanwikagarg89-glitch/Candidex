package model;

import model.enums.UserRole;

/**
 * Abstract base class representing an authentic user in CANDIDEX.
 * Demonstrates Abstraction, Encapsulation, and inheritance foundation.
 */
public abstract class User {

    protected String userId;
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected UserRole role;
    protected boolean sampleRecord;

    /**
     * Parameterized constructor demonstrating constructor initialization.
     */
    public User(String userId, String fullName, String email, String password, String phoneNumber, UserRole role, boolean sampleRecord) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.sampleRecord = sampleRecord;
    }

    /**
     * Abstract method enforcing Polymorphism across all concrete subclasses.
     */
    public abstract void displayProfile();

    /**
     * Verifies provided password against stored password.
     */
    public boolean verifyPassword(String inputPassword) {
        if (inputPassword == null || this.password == null) {
            return false;
        }
        return this.password.equals(inputPassword);
    }

    // Getters and Setters demonstrating Encapsulation
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isSampleRecord() {
        return sampleRecord;
    }

    public void setSampleRecord(boolean sampleRecord) {
        this.sampleRecord = sampleRecord;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Email: %s | Phone: %s | %s",
                role.getDisplayName(), userId, fullName, email, phoneNumber,
                (sampleRecord ? "[Sample Data]" : "[Registered User]"));
    }
}
