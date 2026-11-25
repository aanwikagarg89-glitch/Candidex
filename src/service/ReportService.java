package service;

import model.PlacementResult;
import model.Student;
import repository.ApplicationRepository;
import repository.DriveRepository;
import repository.StudentRepository;

import java.util.*;

/**
 * Service aggregating institutional placement metrics and generating TPO reports.
 * Demonstrates Map aggregations, mathematical computations, and formatted report output.
 */
public class ReportService {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;

    public ReportService(StudentRepository studentRepo, DriveRepository driveRepo,
                         ApplicationRepository appRepo) {
        this.studentRepository = studentRepo;
        this.driveRepository = driveRepo;
        this.applicationRepository = appRepo;
    }

    public void generateOverallPlacementSummary() {
        List<Student> allStudents = studentRepository.findAll();
        List<PlacementResult> allResults = applicationRepository.findAllResults();
        int totalStudents = allStudents.size();
        int placedCount = studentRepository.findPlaced().size();
        int unplacedCount = studentRepository.findUnplaced().size();
        double placementPercentage = (totalStudents > 0) ? ((double) placedCount / totalStudents) * 100.0 : 0.0;

        double highestCtc = 0.0;
        double totalCtc = 0.0;
        for (PlacementResult r : allResults) {
            if (r.getOfferedCtcLpa() > highestCtc) {
                highestCtc = r.getOfferedCtcLpa();
            }
            totalCtc += r.getOfferedCtcLpa();
        }
        double avgCtc = allResults.isEmpty() ? 0.0 : totalCtc / allResults.size();

        System.out.println("================================================================");
        System.out.println("             INSTITUTIONAL PLACEMENT SUMMARY                    ");
        System.out.println("================================================================");
        System.out.printf("Total Registered Students : %d%n", totalStudents);
        System.out.printf("Total Students Placed     : %d%n", placedCount);
        System.out.printf("Total Students Seeking    : %d%n", unplacedCount);
        System.out.printf("Placement Conversion Rate : %.2f%%%n", placementPercentage);
        System.out.printf("Total Job Offers Extended : %d%n", allResults.size());
        System.out.printf("Highest Offered Package   : %.2f LPA%n", highestCtc);
        System.out.printf("Average Offered Package   : %.2f LPA%n", avgCtc);
        System.out.println("================================================================");
    }

    public void generateBranchWiseReport() {
        List<Student> allStudents = studentRepository.findAll();
        if (allStudents.isEmpty()) {
            System.out.println("No student records available for branch-wise analysis.");
            return;
        }

        // Map key = Department, value = int[]{ totalInBranch, placedInBranch }
        Map<String, int[]> branchStats = new TreeMap<>();
        for (Student s : allStudents) {
            String dept = s.getDepartment() != null ? s.getDepartment().toUpperCase() : "GENERAL";
            branchStats.putIfAbsent(dept, new int[]{0, 0});
            int[] counts = branchStats.get(dept);
            counts[0]++; // total
            if (s.isPlaced()) {
                counts[1]++; // placed
            }
        }

        System.out.println("================================================================");
        System.out.println("            BRANCH-WISE PLACEMENT STATISTICS                    ");
        System.out.println("================================================================");
        System.out.printf("%-12s | %-12s | %-12s | %-12s%n", "BRANCH", "REGISTERED", "PLACED", "PLACEMENT %");
        System.out.println("----------------------------------------------------------------");
        for (Map.Entry<String, int[]> entry : branchStats.entrySet()) {
            String branch = entry.getKey();
            int total = entry.getValue()[0];
            int placed = entry.getValue()[1];
            double pct = (total > 0) ? ((double) placed / total) * 100.0 : 0.0;
            System.out.printf("%-12s | %-12d | %-12d | %-11.2f%%%n", branch, total, placed, pct);
        }
        System.out.println("================================================================");
    }

    public void generateCompanyWiseReport() {
        List<PlacementResult> allResults = applicationRepository.findAllResults();
        if (allResults.isEmpty()) {
            System.out.println("No job offers have been finalized yet.");
            return;
        }

        // Map key = Company Name, value = List of packages
        Map<String, List<Double>> companyHires = new TreeMap<>();
        for (PlacementResult r : allResults) {
            String company = r.getCompanyName();
            companyHires.putIfAbsent(company, new ArrayList<>());
            companyHires.get(company).add(r.getOfferedCtcLpa());
        }

        System.out.println("================================================================");
        System.out.println("              COMPANY-WISE RECRUITMENT REPORT                   ");
        System.out.println("================================================================");
        System.out.printf("%-24s | %-12s | %-15s%n", "COMPANY NAME", "SELECTIONS", "AVG PACKAGE");
        System.out.println("----------------------------------------------------------------");
        for (Map.Entry<String, List<Double>> entry : companyHires.entrySet()) {
            String company = entry.getKey();
            List<Double> packages = entry.getValue();
            double sum = 0.0;
            for (double p : packages) sum += p;
            double avg = sum / packages.size();
            System.out.printf("%-24s | %-12d | %-14.2f LPA%n", company, packages.size(), avg);
        }
        System.out.println("================================================================");
    }
}
