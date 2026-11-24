package repository;

import exceptions.InvalidAcademicDataException;
import model.*;
import utils.ValidationUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Central runtime DataStore coordinator.
 * Maintains system repositories and pre-seeds high-quality demonstration records
 */
public class DataStore {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private Admin defaultAdmin;

    public DataStore() {
        this.studentRepository = new StudentRepository();
        this.driveRepository = new DriveRepository();
        this.applicationRepository = new ApplicationRepository();
    }

    /**
     * Seeds initial sample data for demonstration.
     * All sample records are explicitly flagged with isSampleRecord = true.
     */
    public void seedDemonstrationData() {
        // 1. Seed Default Placement Cell Administrator
        this.defaultAdmin = new Admin(
                "ADM-1000",
                "Prof. Arvind Swaminathan",
                "admin@campus.edu",
                "admin123",
                "9876543210",
                "EMP-TPO-01",
                "Head of Training & Placement",
                "Placement Cell",
                true
        );

        try {
            // 2. Seed Sample Students with diverse profiles (CSE, ECE, MECH)
            AcademicRecord rec1 = new AcademicRecord(9.20, 94.5, 92.0, 0, 0, 2026);
            Student stu1 = new Student("STU-1000", "Rahul Sharma", "rahul.sharma@campus.edu", "pass123", "9123456780", "22CS101", "CSE", rec1, true);
            stu1.addSkills("Java", "Data Structures", "Spring Boot", "MySQL", "AWS");
            studentRepository.save(stu1);

            AcademicRecord rec2 = new AcademicRecord(7.60, 85.0, 82.5, 0, 1, 2026);
            Student stu2 = new Student("STU-1001", "Ananya Verma", "ananya.verma@campus.edu", "pass123", "9123456781", "22EC204", "ECE", rec2, true);
            stu2.addSkills("Java", "Embedded Systems", "Python", "IoT");
            studentRepository.save(stu2);

            AcademicRecord rec3 = new AcademicRecord(6.40, 78.0, 72.0, 2, 3, 2026);
            Student stu3 = new Student("STU-1002", "Rohan Iyer", "rohan.iyer@campus.edu", "pass123", "9123456782", "22ME310", "MECH", rec3, true);
            stu3.addSkills("AutoCAD", "SolidWorks", "Python Basics");
            studentRepository.save(stu3);

            // 3. Seed Sample Recruiting Companies
            Company comp1 = new Company("CMP-1000", "Google India", "careers@google.com", "comp123", "9899001122",
                    "Software & Internet", "https://careers.google.com", "Priya Nair", true);
            driveRepository.saveCompany(comp1);

            Company comp2 = new Company("CMP-1001", "Microsoft IDC", "recruitment@microsoft.com", "comp123", "9899001133",
                    "Cloud Computing & Enterprise", "https://careers.microsoft.com", "Rajesh Khanna", true);
            driveRepository.saveCompany(comp2);

            Company comp3 = new Company("CMP-1002", "TCS Digital", "campus@tcs.com", "comp123", "9899001144",
                    "IT Services & Consulting", "https://www.tcs.com/careers", "Vikram Sen", true);
            driveRepository.saveCompany(comp3);

            // 4. Seed Placement Drives with realistic eligibility criteria
            Set<String> techBranches = new HashSet<>(Arrays.asList("CSE", "IT", "ECE"));
            EligibilityCriteria googleCriteria = new EligibilityCriteria(8.50, 85.0, 80.0, 0, techBranches, 2026);
            PlacementDrive drive1 = new PlacementDrive("DRV-1000", comp1.getUserId(), comp1.getCompanyName(),
                    "Software Engineer - Cloud Systems", "Build scalable distributed services and cloud backends.",
                    28.5, "Bengaluru / Hyderabad", googleCriteria, ValidationUtil.getRelativeDeadlineDays(15), true);
            driveRepository.saveDrive(drive1);

            EligibilityCriteria msCriteria = new EligibilityCriteria(7.50, 75.0, 75.0, 0, techBranches, 2026);
            PlacementDrive drive2 = new PlacementDrive("DRV-1001", comp2.getUserId(), comp2.getCompanyName(),
                    "Software Engineer I", "Develop features for Microsoft Azure platform.",
                    22.0, "Hyderabad / Noida", msCriteria, ValidationUtil.getRelativeDeadlineDays(20), true);
            driveRepository.saveDrive(drive2);

            Set<String> allBranches = new HashSet<>(Arrays.asList("CSE", "ECE", "MECH", "CIVIL", "EE"));
            EligibilityCriteria tcsCriteria = new EligibilityCriteria(6.50, 60.0, 60.0, 1, allBranches, 2026);
            PlacementDrive drive3 = new PlacementDrive("DRV-1002", comp3.getUserId(), comp3.getCompanyName(),
                    "Systems Engineer", "Full stack development and digital transformations.",
                    7.5, "Pan India", tcsCriteria, ValidationUtil.getRelativeDeadlineDays(30), true);
            driveRepository.saveDrive(drive3);

            // 5. Seed a sample application from Rahul to Google
            Application app1 = new Application("APP-1000", stu1.getUserId(), stu1.getRollNumber(), stu1.getFullName(),
                    drive1.getDriveId(), drive1.getCompanyName(), drive1.getJobRole(), ValidationUtil.getCurrentDateTimeFormatted(), true);
            applicationRepository.save(app1);

        } catch (InvalidAcademicDataException e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }

    public StudentRepository getStudentRepository() {
        return studentRepository;
    }

    public DriveRepository getDriveRepository() {
        return driveRepository;
    }

    public ApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }

    public Admin getDefaultAdmin() {
        return defaultAdmin;
    }
}
