package model;

import model.enums.UserRole;

/**
 * Concrete entity representing a hiring Corporate Partner / Recruiting Company.
 * Demonstrates Inheritance (extends User) and Polymorphism.
 */
public class Company extends User {

    private String companyName;
    private String industryDomain;
    private String websiteUrl;
    private String hrContactPerson;

    public Company(String userId, String companyName, String email, String password, String phoneNumber,
                   String industryDomain, String websiteUrl, String hrContactPerson, boolean sampleRecord) {
        super(userId, companyName, email, password, phoneNumber, UserRole.COMPANY, sampleRecord);
        this.companyName = companyName;
        this.industryDomain = industryDomain;
        this.websiteUrl = websiteUrl;
        this.hrContactPerson = hrContactPerson;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.fullName = companyName;
    }

    public String getIndustryDomain() {
        return industryDomain;
    }

    public void setIndustryDomain(String industryDomain) {
        this.industryDomain = industryDomain;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getHrContactPerson() {
        return hrContactPerson;
    }

    public void setHrContactPerson(String hrContactPerson) {
        this.hrContactPerson = hrContactPerson;
    }

    @Override
    public void displayProfile() {
        System.out.println("================================================================");
        System.out.println("                     COMPANY PROFILE                            ");
        System.out.println("================================================================");
        System.out.printf("Company ID     : %s %s%n", userId, (sampleRecord ? "[SAMPLE]" : "[REGISTERED]"));
        System.out.printf("Company Name   : %s%n", companyName);
        System.out.printf("Industry Sector: %s%n", industryDomain);
        System.out.printf("Website URL    : %s%n", websiteUrl);
        System.out.printf("Official Email : %s%n", email);
        System.out.printf("Contact Phone  : %s%n", phoneNumber);
        System.out.printf("HR Lead        : %s%n", hrContactPerson);
        System.out.println("================================================================");
    }
}
