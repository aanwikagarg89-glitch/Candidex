package app;

import database.DBConnectionManager;
import exceptions.*;
import model.*;
import model.enums.ApplicationStatus;
import model.enums.InterviewStatus;
import model.enums.UserRole;
import repository.*;
import service.*;
import threading.BatchProcessingManager;
import utils.ValidationUtil;

import java.util.*;

/**
 * Interactive Command-Line Interface (CLI) Menu System for CANDIDEX.
 * Implements role-specific navigation for Students, Administrators, and
 * Companies.
 */
public class ConsoleMenu {

    private final DataStore dataStore;
    private final SessionManager sessionManager;
    private final AuthService authService;
    private final StudentService studentService;
    private final DriveService driveService;
    private final ApplicationService applicationService;
    private final RankingService rankingService;
    private final ReportService reportService;
    private final BatchProcessingManager batchProcessingManager;

    public ConsoleMenu(DataStore dataStore, SessionManager sessionManager) {
        this.dataStore = dataStore;
        this.sessionManager = sessionManager;
        this.authService = new AuthService(dataStore);
        this.studentService = new StudentService(dataStore.getStudentRepository());
        this.driveService = new DriveService(dataStore.getDriveRepository());
        this.applicationService = new ApplicationService(
                dataStore.getApplicationRepository(),
                dataStore.getStudentRepository(),
                dataStore.getDriveRepository(),
                this.driveService);
        this.rankingService = new RankingService();
        this.reportService = new ReportService(
                dataStore.getStudentRepository(),
                dataStore.getDriveRepository(),
                dataStore.getApplicationRepository());
        this.batchProcessingManager = new BatchProcessingManager(
                dataStore.getStudentRepository(),
                dataStore.getDriveRepository(),
                dataStore.getApplicationRepository(),
                this.driveService);
    }

    public void start() {
        boolean exitProgram = false;
        while (!exitProgram) {
            if (!sessionManager.isAuthenticated()) {
                exitProgram = handleGuestMenu();
            } else {
                User user = sessionManager.getCurrentUser();
                switch (user.getRole()) {
                    case STUDENT:
                        handleStudentMenu((Student) user);
                        break;
                    case ADMIN:
                        handleAdminMenu((Admin) user);
                        break;
                    case COMPANY:
                        handleCompanyMenu((Company) user);
                        break;
                }
            }
        }
        System.out.println("\nThank you for using CANDIDEX. Program terminated cleanly.");
    }

    // =========================================================================
    // GUEST / AUTHENTICATION MENU
    // =========================================================================

    private boolean handleGuestMenu() {
        printHeader("CANDIDEX: CAMPUS PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("1. Login (Student / Admin / Company)");
        System.out.println("2. Student Registration (Enter profile, academics & skills)");
        System.out.println("3. Company Registration (Onboard corporate recruiter)");
        System.out.println("4. Browse Active Placement Drives");
        System.out.println("5. Exit");
        System.out.println("----------------------------------------------------------------");
        int choice = InputHelper.readInt("Select an option", 1, 5);
        switch (choice) {
            case 1:
                loginUser();
                break;
            case 2:
                registerStudentInteractive();
                break;
            case 3:
                registerCompanyInteractive();
                break;
            case 4:
                browseActiveDrivesGuest();
                break;
            case 5:
                return true;
        }
        return false;
    }

    private void loginUser() {
        printHeader("USER LOGIN");
        System.out.println("Hint for Viva Demo:");
        System.out.println("  • Admin   : admin@campus.edu / admin123");
        System.out.println("  • Student : rahul.sharma@campus.edu / pass123");
        System.out.println("  • Company : careers@google.com / comp123");
        System.out.println("  (Or log in with your newly registered credentials)");
        System.out.println("----------------------------------------------------------------");

        String email = InputHelper.readEmail("Enter Registered Email");
        String password = InputHelper.readString("Enter Password", false);

        Optional<User> userOpt = authService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            sessionManager.createSession(user);
            System.out.printf("%n✔ Authentication successful! Welcome, %s [%s]%n",
                    user.getFullName(), user.getRole().getDisplayName());
        } else {
            System.out.println("\n✘ Invalid email or password. Please check your credentials.");
        }
    }

    private void registerStudentInteractive() {
        printHeader("STUDENT SELF-REGISTRATION");
        System.out.println("Please provide your accurate academic credentials.");
        System.out.println("----------------------------------------------------------------");

        String fullName = InputHelper.readString("Full Name", false);
        String email = InputHelper.readEmail("Email Address");
        String password = InputHelper.readString("Create Password (min 4 characters)", false);
        String phone = InputHelper.readPhone("Contact Phone Number");
        String rollNumber = InputHelper.readString("University Roll Number", false);
        String department = InputHelper.readString("Department / Branch (e.g. CSE, ECE, MECH, IT)", false);

        System.out.println("\n--- Academic Information ---");
        double cgpa = InputHelper.readDouble("Current Cumulative CGPA", 0.0, 10.0);
        double tenth = InputHelper.readDouble("10th Standard Percentage", 0.0, 100.0);
        double twelfth = InputHelper.readDouble("12th / Diploma Percentage", 0.0, 100.0);
        int activeBacklogs = InputHelper.readInt("Active / Standing Backlogs", 0, 20);
        int historyBacklogs = InputHelper.readInt("Total Historical Backlogs", activeBacklogs, 30);
        int gradYear = InputHelper.readInt("Expected Graduation Batch Year", 2024, 2030);

        Set<String> skills = InputHelper.readCommaSeparatedSet("Skills Inventory");

        try {
            AcademicRecord academicRecord = new AcademicRecord(cgpa, tenth, twelfth, activeBacklogs, historyBacklogs,
                    gradYear);
            Student newStudent = authService.registerStudent(fullName, email, password, phone, rollNumber, department,
                    academicRecord);
            for (String skill : skills) {
                newStudent.addSkill(skill);
            }
            dataStore.getStudentRepository().save(newStudent);

            System.out.println("\n✔ Registration successful! Your student profile has been created.");
            System.out.printf("  Assigned Student ID: %s%n", newStudent.getUserId());
            System.out.println("  You can now log in using your registered email and password.");
        } catch (CandidexException e) {
            System.err.printf("%n✘ Registration Failed: %s%n", e.getMessage());
        }
    }

    private void registerCompanyInteractive() {
        printHeader("COMPANY RECRUITER REGISTRATION");
        String companyName = InputHelper.readString("Company Name", false);
        String email = InputHelper.readEmail("Official Company Email");
        String password = InputHelper.readString("Account Password", false);
        String phone = InputHelper.readPhone("Contact Phone Number");
        String industry = InputHelper.readString("Industry Sector (e.g. Cloud, FinTech, E-Commerce)", false);
        String website = InputHelper.readString("Careers / Corporate Website", false);
        String hrName = InputHelper.readString("HR Lead / Recruiter Full Name", false);

        try {
            Company newCompany = authService.registerCompany(companyName, email, password, phone, industry, website,
                    hrName);
            System.out.println("\n✔ Company registered successfully!");
            System.out.printf("  Assigned Company ID: %s%n", newCompany.getUserId());
            System.out.println("  You can now log in to post placement drives and review candidates.");
        } catch (CandidexException e) {
            System.err.printf("%n✘ Registration Failed: %s%n", e.getMessage());
        }
    }

    private void browseActiveDrivesGuest() {
        printHeader("CURRENTLY ACTIVE PLACEMENT DRIVES");
        List<PlacementDrive> openDrives = driveService.getOpenDrives();
        if (openDrives.isEmpty()) {
            System.out.println("No open placement drives at this time.");
            return;
        }
        for (PlacementDrive d : openDrives) {
            d.displayDriveSummary();
        }
    }

    private void testJdbcMenu() {
        printHeader("JDBC ARCHITECTURE & DATABASE CONNECTIVITY");
        System.out.println("1. View JDBC Architecture & Course Syllabus Breakdown");
        System.out.println("2. Display Database URL & Metadata Parameters");
        System.out.println("3. Test Physical MySQL Connection Handshake (DriverManager.getConnection)");
        System.out.println("4. Return to Main Menu");
        System.out.println("----------------------------------------------------------------");

        int choice = InputHelper.readInt("Select an option", 1, 4);
        switch (choice) {
            case 1:
                DBConnectionManager.printJdbcVivaArchitecture();
                break;
            case 2:
                DBConnectionManager.displayMetadata();
                break;
            case 3:
                DBConnectionManager.testConnection();
                break;
            case 4:
                break;
        }
    }

    // =========================================================================
    // STUDENT ROLE MENU
    // =========================================================================

    private void handleStudentMenu(Student student) {
        printHeader("STUDENT DASHBOARD — " + student.getFullName().toUpperCase());

        System.out.println("1. View My Profile & Academic Record");
        System.out.println("2. Edit Profile");
        System.out.println("3. Manage Skills (Add / Remove)");
        System.out.println("4. Browse Placement Drives & Check Live Eligibility");
        System.out.println("5. Apply for a Placement Drive");
        System.out.println("6. View My Submitted Applications & Status Tracker");
        System.out.println("7. View Scheduled Interview Rounds");
        System.out.println("8. View Placement Result / Offer Letter");
        System.out.println("9. Logout");
        System.out.println("----------------------------------------------------------------");

        int choice = InputHelper.readInt("Select an option", 1, 9);

        switch (choice) {
            case 1:
                student.displayProfile();
                break;
            case 2:
                editStudentProfile(student);
                break;
            case 3:
                manageStudentSkills(student);
                break;
            case 4:
                browseDrivesWithEligibility(student);
                break;
            case 5:
                applyForDriveInteractive(student);
                break;
            case 6:
                viewStudentApplications(student);
                break;
            case 7:
                viewStudentInterviews(student);
                break;
            case 8:
                viewStudentPlacementResult(student);
                break;
            case 9:
                sessionManager.destroySession();
                System.out.println("\n✔ Logged out successfully.");
                break;
        }
    }

    private void editStudentProfile(Student student) {
        printHeader("EDIT STUDENT PROFILE");
        System.out.println("1. Edit Full Name");
        System.out.println("2. Edit Phone Number");
        System.out.println("3. Edit Department / Branch");
        System.out.println("4. Update Academic Details");
        System.out.println("5. Back to Dashboard");
        System.out.println("----------------------------------------------------------------");
        int choice = InputHelper.readInt("Select an option", 1, 5);
        try {
            switch (choice) {
                case 1:
                    String name = InputHelper.readString("Enter New Full Name", false);
                    studentService.updateBasicDetails(
                            student.getUserId(),
                            name,
                            student.getPhoneNumber(),
                            student.getDepartment());
                    System.out.println("✔ Name updated successfully!");
                    break;
                case 2:
                    String phone = InputHelper.readPhone("Enter New Phone Number");
                    studentService.updateBasicDetails(
                            student.getUserId(),
                            student.getFullName(),
                            phone,
                            student.getDepartment());
                    System.out.println("✔ Phone number updated successfully!");
                    break;
                case 3:
                    String department = InputHelper.readString("Enter New Department", false);
                    studentService.updateBasicDetails(
                            student.getUserId(),
                            student.getFullName(),
                            student.getPhoneNumber(),
                            department);
                    System.out.println("✔ Department updated successfully!");
                    break;
                case 4:
                    updateStudentAcademics(student);
                    break;
                case 5:
                    System.out.println("Returning to Student Dashboard...");
                    break;
            }
        } catch (Exception e) {
            System.out.println("✘ Profile update failed: " + e.getMessage());
        }
    }

    private void updateStudentAcademics(Student student) {
        printHeader("UPDATE ACADEMIC RECORD");
        try {
            double cgpa = InputHelper.readDouble("Current Cumulative CGPA", 0.0, 10.0);
            double tenth = InputHelper.readDouble("10th Standard Percentage", 0.0, 100.0);
            double twelfth = InputHelper.readDouble("12th / Diploma Percentage", 0.0, 100.0);
            int active = InputHelper.readInt("Active Backlogs Count", 0, 20);
            int history = InputHelper.readInt("Total Historical Backlogs", active, 30);
            int year = InputHelper.readInt("Graduation Batch Year", 2024, 2030);

            studentService.updateAcademicRecord(student.getUserId(), cgpa, tenth, twelfth, active, history, year);
            System.out.println("\n✔ Academic record updated successfully!");
        } catch (Exception e) {
            System.err.printf("%n✘ Update failed: %s%n", e.getMessage());
        }
    }

    private void manageStudentSkills(Student student) {
        printHeader("MANAGE SKILLS INVENTORY");
        System.out.printf("Current Skills: %s%n",
                student.getSkills().isEmpty() ? "[None]" : String.join(", ", student.getSkills()));
        System.out.println("1. Add Skill Tag");
        System.out.println("2. Remove Skill Tag");
        System.out.println("3. Back to Dashboard");

        int choice = InputHelper.readInt("Select an option", 1, 3);
        try {
            if (choice == 1) {
                String skill = InputHelper.readString("Enter skill name (e.g. Java, AWS, React)", false);
                studentService.addSkill(student.getUserId(), skill);
                System.out.println("✔ Skill added!");
            } else if (choice == 2) {
                String skill = InputHelper.readString("Enter skill name to remove", false);
                boolean removed = studentService.removeSkill(student.getUserId(), skill);
                if (removed) {
                    System.out.println("✔ Skill removed.");
                } else {
                    System.out.println("✘ Skill was not found in your inventory.");
                }
            }
        } catch (CandidexException e) {
            System.err.println(e.getMessage());
        }
    }

    private void browseDrivesWithEligibility(Student student) {
        printHeader("PLACEMENT DRIVES & DYNAMIC ELIGIBILITY EVALUATION");
        List<PlacementDrive> openDrives = driveService.getOpenDrives();
        if (openDrives.isEmpty()) {
            System.out.println("No open placement drives found.");
            return;
        }

        System.out.println("Evaluating your eligibility against all active company drives in real-time...\n");
        for (PlacementDrive drive : openDrives) {
            drive.displayDriveSummary();
            List<String> diagnosticReasons = driveService.getEligibilityDiagnosticReasons(student, drive);
            if (diagnosticReasons.isEmpty()) {
                System.out.println(">>> ELIGIBILITY STATUS: [✔ FULLY ELIGIBLE TO APPLY]");
            } else {
                System.out.println(">>> ELIGIBILITY STATUS: [✘ INELIGIBLE]");
                System.out.println("    Unmet Criteria:");
                for (String reason : diagnosticReasons) {
                    System.out.println("    - " + reason);
                }
            }
            System.out.println("----------------------------------------------------------------");
        }
    }

    private void applyForDriveInteractive(Student student) {
        printHeader("APPLY FOR PLACEMENT DRIVE");
        String driveId = InputHelper.readString("Enter Drive ID to apply (e.g. DRV-1000)", false).toUpperCase();

        try {
            Application app = applicationService.applyForDrive(student.getUserId(), driveId);
            System.out.println("\n✔ APPLICATION SUBMITTED SUCCESSFULLY!");
            System.out.printf("  Application Reference ID: %s%n", app.getApplicationId());
            System.out.printf("  Applied For             : %s at %s%n", app.getJobRole(), app.getCompanyName());
            System.out.printf("  Status                  : %s%n", app.getStatus().getDescription());
        } catch (DuplicateApplicationException e) {
            System.err.printf("%n✘ Duplicate Application: You have already applied for this placement drive.%n");
        } catch (IneligibleStudentException e) {
            System.err.printf("%n✘ Application Rejected by Eligibility Engine:%n");
            for (String reason : e.getFailureReasons()) {
                System.err.println("  • " + reason);
            }
        } catch (CandidexException e) {
            System.err.printf("%n✘ Application Error: %s%n", e.getMessage());
        }
    }

    private void viewStudentApplications(Student student) {
        printHeader("MY SUBMITTED APPLICATIONS & STATUS TRACKER");
        List<Application> apps = applicationService.getStudentApplications(student.getUserId());
        if (apps.isEmpty()) {
            System.out.println("You have not submitted any placement applications yet.");
            return;
        }
        for (Application a : apps) {
            a.displaySummary();
        }
    }

    private void viewStudentInterviews(Student student) {
        printHeader("SCHEDULED INTERVIEW ROUNDS");
        List<Application> apps = applicationService.getStudentApplications(student.getUserId());
        boolean hasInterviews = false;
        for (Application a : apps) {
            List<Interview> rounds = applicationService.getInterviewsForApplication(a.getApplicationId());
            if (!rounds.isEmpty()) {
                hasInterviews = true;
                System.out.printf("Company: %s | Role: %s (Application: %s)%n",
                        a.getCompanyName(), a.getJobRole(), a.getApplicationId());
                for (Interview i : rounds) {
                    System.out.print("  ↳ ");
                    i.displayInterviewCard();
                }
                System.out.println();
            }
        }
        if (!hasInterviews) {
            System.out.println("No interview rounds scheduled yet.");
        }
    }

    private void viewStudentPlacementResult(Student student) {
        printHeader("FINAL PLACEMENT RESULTS & OFFERS");
        Optional<PlacementResult> offerOpt = dataStore.getApplicationRepository()
                .findResultByStudentId(student.getUserId());
        if (offerOpt.isPresent()) {
            offerOpt.get().displayOffer();
        } else {
            System.out.println("No final placement offers extended yet. Keep preparing and applying!");
        }
    }

    // =========================================================================
    // ADMINISTRATOR / TPO ROLE MENU
    // =========================================================================

    private void handleAdminMenu(Admin admin) {
        printHeader("ADMINISTRATOR (TPO) CONTROL CENTER");
        System.out.println("1. View Student Master Registry (Search & Filter)");
        System.out.println("2. Register a New Student (Admin Override)");
        System.out.println("3. View Registered Recruiting Companies");
        System.out.println("4. Register a New Company");
        System.out.println("5. View All Placement Drives");
        System.out.println("6. Create an Institutional Placement Drive");
        System.out.println("7. Close / Terminate a Placement Drive");
        System.out.println("8. View All Submitted Applications (System-wide)");
        System.out.println("9. Execute Multithreaded Batch Application Processing (Producer-Consumer)");
        System.out.println("10. Placement Analytics & Statistical Reports");
        System.out.println("11. Test MySQL Database Connectivity (JDBC Demo)");
        System.out.println("12. Logout");
        System.out.println("----------------------------------------------------------------");

        int choice = InputHelper.readInt("Select an option", 1, 12);
        switch (choice) {
            case 1:
                adminViewStudents();
                break;
            case 2:
                registerStudentInteractive();
                break;
            case 3:
                adminViewCompanies();
                break;
            case 4:
                registerCompanyInteractive();
                break;
            case 5:
                adminViewAllDrives();
                break;
            case 6:
                adminCreateDrive();
                break;
            case 7:
                adminCloseDrive();
                break;
            case 8:
                adminViewAllApplications();
                break;
            case 9:
                adminRunBatchProcessing();
                break;
            case 10:
                adminViewReports();
                break;
            case 11:
                testJdbcMenu();
                break;
            case 12:
                sessionManager.destroySession();
                System.out.println("\n✔ Admin logged out successfully.");
                break;
        }
    }

    private void adminViewStudents() {
        printHeader("STUDENT MASTER DIRECTORY");
        System.out.println("1. List All Students");
        System.out.println("2. Filter by Department");
        System.out.println("3. Search by University Roll Number");
        System.out.println("4. List Unplaced Students");

        int opt = InputHelper.readInt("Select filter", 1, 4);
        List<Student> students = Collections.emptyList();
        if (opt == 1) {
            students = dataStore.getStudentRepository().findAll();
        } else if (opt == 2) {
            String dept = InputHelper.readString("Enter department (e.g. CSE, ECE)", false);
            students = dataStore.getStudentRepository().findByDepartment(dept);
        } else if (opt == 3) {
            String roll = InputHelper.readString("Enter Roll Number", false);
            Optional<Student> s = dataStore.getStudentRepository().findByRollNumber(roll);
            if (s.isPresent()) {
                students = Collections.singletonList(s.get());
            }
        } else if (opt == 4) {
            students = dataStore.getStudentRepository().findUnplaced();
        }

        if (students.isEmpty()) {
            System.out.println("No matching student records found.");
            return;
        }

        System.out.printf("%-10s | %-12s | %-20s | %-8s | %-6s | %-10s%n",
                "ID", "ROLL NO", "FULL NAME", "BRANCH", "CGPA", "STATUS");
        System.out.println("----------------------------------------------------------------------------");
        for (Student s : students) {
            double cgpa = (s.getAcademicRecord() != null) ? s.getAcademicRecord().getCgpa() : 0.0;
            System.out.printf("%-10s | %-12s | %-20s | %-8s | %-6.2f | %-10s%n",
                    s.getUserId(), s.getRollNumber(), s.getFullName(), s.getDepartment(),
                    cgpa, (s.isPlaced() ? "PLACED" : "UNPLACED"));
        }
    }

    private void adminViewCompanies() {
        printHeader("REGISTERED RECRUITING PARTNERS");
        List<Company> list = dataStore.getDriveRepository().findAllCompanies();
        if (list.isEmpty()) {
            System.out.println("No companies registered yet.");
            return;
        }
        for (Company c : list) {
            c.displayProfile();
        }
    }

    private void adminViewAllDrives() {
        printHeader("ALL PLACEMENT DRIVES");
        List<PlacementDrive> all = driveService.getAllDrives();
        if (all.isEmpty()) {
            System.out.println("No placement drives posted yet.");
            return;
        }
        for (PlacementDrive d : all) {
            d.displayDriveSummary();
        }
    }

    private void adminCreateDrive() {
        printHeader("POST NEW PLACEMENT DRIVE");
        List<Company> companies = dataStore.getDriveRepository().findAllCompanies();
        if (companies.isEmpty()) {
            System.out.println("No companies registered yet. Please register a company first.");
            return;
        }

        System.out.println("Available Companies:");
        for (Company c : companies) {
            System.out.printf("  • [%s] %s (%s)%n", c.getUserId(), c.getCompanyName(), c.getIndustryDomain());
        }

        String companyId = InputHelper.readString("Enter Company ID", false).toUpperCase();
        Company company = companies.stream().filter(c -> c.getUserId().equalsIgnoreCase(companyId)).findFirst()
                .orElse(null);
        if (company == null) {
            System.out.println("✘ Invalid Company ID.");
            return;
        }

        String role = InputHelper.readString("Job Title / Designation", false);
        String desc = InputHelper.readString("Brief Job Description", false);
        double ctc = InputHelper.readDouble("Package CTC in LPA", 1.0, 150.0);
        String loc = InputHelper.readString("Job Location", false);

        System.out.println("\n--- Define Eligibility Criteria ---");
        double minCgpa = InputHelper.readDouble("Minimum CGPA Required", 0.0, 10.0);
        double min10th = InputHelper.readDouble("Minimum 10th Percentage", 0.0, 100.0);
        double min12th = InputHelper.readDouble("Minimum 12th Percentage", 0.0, 100.0);
        int maxBacklogs = InputHelper.readInt("Max Allowed Active Backlogs", 0, 10);
        Set<String> branches = InputHelper.readCommaSeparatedSet("Eligible Branches");
        int batchYear = InputHelper.readInt("Graduation Year (0 for Any)", 0, 2030);

        int deadlineDays = InputHelper.readInt("Application Window in Days", 1, 90);
        String deadlineDate = ValidationUtil.getRelativeDeadlineDays(deadlineDays);

        EligibilityCriteria criteria = new EligibilityCriteria(minCgpa, min10th, min12th, maxBacklogs, branches,
                batchYear);
        PlacementDrive drive = driveService.createDrive(company.getUserId(), company.getCompanyName(), role, desc, ctc,
                loc, criteria, deadlineDate);

        System.out.println("\n✔ Placement Drive posted successfully!");
        System.out.printf("  Drive ID: %s | Deadline: %s%n", drive.getDriveId(), deadlineDate);
    }

    private void adminCloseDrive() {
        printHeader("CLOSE PLACEMENT DRIVE");
        String driveId = InputHelper.readString("Enter Drive ID to close", false).toUpperCase();
        try {
            driveService.closeDrive(driveId);
            System.out.printf("✔ Placement Drive [%s] has been closed.%n", driveId);
        } catch (CandidexException e) {
            System.err.println("✘ " + e.getMessage());
        }
    }

    private void adminViewAllApplications() {
        printHeader("SYSTEM-WIDE PLACEMENT APPLICATIONS");
        List<Application> list = applicationService.getAllApplications();
        if (list.isEmpty()) {
            System.out.println("No applications submitted in the system yet.");
            return;
        }
        for (Application a : list) {
            a.displaySummary();
        }
    }

    private void adminRunBatchProcessing() {
        printHeader("MULTITHREADED APPLICATION BATCH PROCESSOR");
        List<Application> pending = dataStore.getApplicationRepository().findPendingApplications();
        if (pending.isEmpty()) {
            System.out.println("No pending applications found requiring batch verification.");
            return;
        }

        System.out.printf("Found %d pending applications requiring automated verification.%n", pending.size());
        boolean proceed = InputHelper.readBoolean("Launch parallel producer-consumer worker threads?");
        if (proceed) {
            batchProcessingManager.executeBatchProcessing(pending);
        }
    }

    private void adminViewReports() {
        printHeader("INSTITUTIONAL PLACEMENT REPORTS & ANALYTICS");
        System.out.println("1. Overall Placement Summary & Package Averages");
        System.out.println("2. Branch-Wise Placement Statistics");
        System.out.println("3. Company-Wise Hiring & CTC Breakdown");

        int choice = InputHelper.readInt("Select report type", 1, 3);
        switch (choice) {
            case 1:
                reportService.generateOverallPlacementSummary();
                break;
            case 2:
                reportService.generateBranchWiseReport();
                break;
            case 3:
                reportService.generateCompanyWiseReport();
                break;
        }
    }

    // =========================================================================
    // COMPANY RECRUITER ROLE MENU
    // =========================================================================

    private void handleCompanyMenu(Company company) {
        printHeader("COMPANY RECRUITER PORTAL — " + company.getCompanyName().toUpperCase());
        System.out.println("1. View Company Profile");
        System.out.println("2. Update Profile & HR Contact");
        System.out.println("3. Post a New Placement Drive");
        System.out.println("4. View My Posted Drives");
        System.out.println("5. View Applicants for My Drives");
        System.out.println("6. Rank Applicants (CGPA / Composite Score)");
        System.out.println("7. Schedule an Interview Round");
        System.out.println("8. Record Interview Result & Feedback");
        System.out.println("9. Extend Final Job Offer (Selection)");
        System.out.println("10. Logout");
        System.out.println("----------------------------------------------------------------");

        int choice = InputHelper.readInt("Select an option", 1, 10);
        switch (choice) {
            case 1:
                company.displayProfile();
                break;
            case 2:
                updateCompanyProfile(company);
                break;
            case 3:
                companyCreateDrive(company);
                break;
            case 4:
                viewCompanyDrives(company);
                break;
            case 5:
                viewCompanyApplicants(company);
                break;
            case 6:
                rankApplicantsMenu(company);
                break;
            case 7:
                scheduleInterviewInteractive(company);
                break;
            case 8:
                recordInterviewFeedbackInteractive();
                break;
            case 9:
                extendJobOfferInteractive(company);
                break;
            case 10:
                sessionManager.destroySession();
                System.out.println("\n✔ Recruiter logged out successfully.");
                break;
        }
    }

    private void updateCompanyProfile(Company company) {
        printHeader("UPDATE COMPANY PROFILE");
        String industry = InputHelper.readString("Industry Sector", false);
        String website = InputHelper.readString("Website URL", false);
        String hr = InputHelper.readString("HR Contact Person", false);
        String phone = InputHelper.readPhone("Phone Number");

        try {
            driveService.updateCompanyProfile(company.getUserId(), industry, website, hr, phone);
            System.out.println("\n✔ Company profile updated successfully!");
        } catch (CandidexException e) {
            System.err.println("✘ " + e.getMessage());
        }
    }

    private void companyCreateDrive(Company company) {
        printHeader("POST PLACEMENT DRIVE — " + company.getCompanyName());
        String role = InputHelper.readString("Job Role / Designation", false);
        String desc = InputHelper.readString("Job Description", false);
        double ctc = InputHelper.readDouble("Package CTC in LPA", 1.0, 150.0);
        String loc = InputHelper.readString("Job Location", false);

        System.out.println("\n--- Set Eligibility Criteria ---");
        double minCgpa = InputHelper.readDouble("Minimum CGPA", 0.0, 10.0);
        double min10th = InputHelper.readDouble("Minimum 10th %", 0.0, 100.0);
        double min12th = InputHelper.readDouble("Minimum 12th %", 0.0, 100.0);
        int maxBacklogs = InputHelper.readInt("Max Standing Backlogs", 0, 10);
        Set<String> branches = InputHelper.readCommaSeparatedSet("Eligible Branches");
        int batchYear = InputHelper.readInt("Graduation Year (0 for Any)", 0, 2030);

        int deadlineDays = InputHelper.readInt("Application Window in Days", 1, 60);
        String deadlineDate = ValidationUtil.getRelativeDeadlineDays(deadlineDays);

        EligibilityCriteria criteria = new EligibilityCriteria(minCgpa, min10th, min12th, maxBacklogs, branches,
                batchYear);
        PlacementDrive drive = driveService.createDrive(company.getUserId(), company.getCompanyName(), role, desc, ctc,
                loc, criteria, deadlineDate);

        System.out.println("\n✔ Placement Drive posted!");
        System.out.printf("  Drive ID: %s | Deadline: %s%n", drive.getDriveId(), deadlineDate);
    }

    private void viewCompanyDrives(Company company) {
        printHeader("POSTED PLACEMENT DRIVES — " + company.getCompanyName());
        List<PlacementDrive> drives = driveService.getDrivesByCompany(company.getUserId());
        if (drives.isEmpty()) {
            System.out.println("No drives posted yet.");
            return;
        }
        for (PlacementDrive d : drives) {
            d.displayDriveSummary();
        }
    }

    private void viewCompanyApplicants(Company company) {
        printHeader("APPLICANTS FOR MY DRIVES");
        List<PlacementDrive> drives = driveService.getDrivesByCompany(company.getUserId());
        if (drives.isEmpty()) {
            System.out.println("No drives posted yet.");
            return;
        }

        for (PlacementDrive d : drives) {
            System.out.printf("Drive [%s] - %s (Package: %.2f LPA):%n", d.getDriveId(), d.getJobRole(),
                    d.getPackageCtcLpa());
            List<Application> apps = applicationService.getDriveApplications(d.getDriveId());
            if (apps.isEmpty()) {
                System.out.println("  No applications submitted yet for this drive.");
            } else {
                for (Application a : apps) {
                    System.out.print("  ↳ ");
                    a.displaySummary();
                }
            }
            System.out.println();
        }
    }

    private void rankApplicantsMenu(Company company) {
        printHeader("APPLICANT RANKING & COMPARATOR ENGINE");
        String driveId = InputHelper.readString("Enter Drive ID to rank applicants for", false).toUpperCase();
        List<Application> apps = applicationService.getDriveApplications(driveId);
        if (apps.isEmpty()) {
            System.out.println("No applicants found for this drive.");
            return;
        }

        List<Student> students = new ArrayList<>();
        for (Application a : apps) {
            dataStore.getStudentRepository().findById(a.getStudentId()).ifPresent(students::add);
        }

        System.out.println("1. Rank Candidates by CGPA (Descending)");
        System.out.println("2. Rank Candidates by Composite Placement Score (CGPA + Skills - Backlogs)");
        System.out.println("3. Top-K Shortlist using PriorityQueue");

        int choice = InputHelper.readInt("Select ranking strategy", 1, 3);
        List<Student> rankedList = Collections.emptyList();
        if (choice == 1) {
            rankedList = rankingService.rankByCgpa(students);
            System.out.println("\n--- CANDIDATES RANKED BY CGPA ---");
        } else if (choice == 2) {
            rankedList = rankingService.rankByPlacementScore(students);
            System.out.println("\n--- CANDIDATES RANKED BY COMPOSITE PLACEMENT SCORE ---");
        } else if (choice == 3) {
            int k = InputHelper.readInt("Enter K (Top candidate count)", 1, students.size());
            rankedList = rankingService.getTopKCandidates(students, k);
            System.out.printf("%n--- TOP %d CANDIDATES (EXTRACTED VIA PRIORITYQUEUE) ---%n", k);
        }

        System.out.printf("%-6s | %-12s | %-20s | %-8s | %-6s | %-6s%n",
                "RANK", "ROLL NO", "FULL NAME", "BRANCH", "CGPA", "SCORE");
        System.out.println("-----------------------------------------------------------------------");
        int rank = 1;
        for (Student s : rankedList) {
            double cgpa = (s.getAcademicRecord() != null) ? s.getAcademicRecord().getCgpa() : 0.0;
            double score = utils.StudentPlacementScoreComparator.calculateScore(s);
            System.out.printf("#%-5d | %-12s | %-20s | %-8s | %-6.2f | %-6.1f%n",
                    rank++, s.getRollNumber(), s.getFullName(), s.getDepartment(), cgpa, score);
        }
    }

    private void scheduleInterviewInteractive(Company company) {
        printHeader("SCHEDULE INTERVIEW ROUND");
        String appId = InputHelper.readString("Enter Application ID (e.g. APP-1000)", false).toUpperCase();
        int roundNum = InputHelper.readInt("Round Number", 1, 10);
        String roundName = InputHelper.readString("Round Name (e.g. Technical Interview 1)", false);
        String dateTime = InputHelper.readString("Interview Date & Time (e.g. 25-11-2026 14:00)", false);
        String mode = InputHelper.readString("Interview Mode (e.g. Online Google Meet / Campus Room 204)", false);

        try {
            Interview interview = applicationService.scheduleRound(appId, roundNum, roundName, dateTime, mode);
            System.out.println("\n✔ Interview scheduled successfully!");
            System.out.printf("  Interview ID: %s | Round: %s%n", interview.getInterviewId(), interview.getRoundName());
        } catch (CandidexException e) {
            System.err.println("✘ " + e.getMessage());
        }
    }

    private void recordInterviewFeedbackInteractive() {
        printHeader("RECORD INTERVIEW RESULT");
        String interviewId = InputHelper.readString("Enter Interview ID (e.g. INT-1001)", false).toUpperCase();

        System.out.println("Select Outcome:");
        System.out.println("1. Cleared Round");
        System.out.println("2. Did Not Clear (Rejected)");
        System.out.println("3. Candidate Absent");

        int outcomeChoice = InputHelper.readInt("Select outcome", 1, 3);
        InterviewStatus status = InterviewStatus.SCHEDULED;
        if (outcomeChoice == 1)
            status = InterviewStatus.CLEARED;
        else if (outcomeChoice == 2)
            status = InterviewStatus.REJECTED;
        else if (outcomeChoice == 3)
            status = InterviewStatus.ABSENT;

        String feedback = InputHelper.readString("Interviewer Comments & Feedback", false);

        try {
            applicationService.updateInterviewResult(interviewId, status, feedback);
            System.out.println("\n✔ Interview feedback recorded successfully!");
        } catch (CandidexException e) {
            System.err.println("✘ " + e.getMessage());
        }
    }

    private void extendJobOfferInteractive(Company company) {
        printHeader("EXTEND FINAL JOB OFFER");
        String appId = InputHelper.readString("Enter Application ID to extend offer", false).toUpperCase();
        double packageCtc = InputHelper.readDouble("Final Offered CTC in LPA", 1.0, 150.0);

        try {
            PlacementResult result = applicationService.extendJobOffer(appId, packageCtc);
            System.out.println("\n🎉 CONGRATULATIONS! JOB OFFER EXTENDED!");
            result.displayOffer();
        } catch (CandidexException e) {
            System.err.println("✘ " + e.getMessage());
        }
    }

    // =========================================================================
    // UI FORMATTING UTILITIES
    // =========================================================================

    private void printHeader(String title) {
        System.out.println();
        System.out.println("================================================================");
        System.out.printf("  %s%n", title);
        System.out.println("================================================================");
    }
}
