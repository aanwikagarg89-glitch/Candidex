package model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Value object specifying quantitative prerequisites for a placement drive.
 * Demonstrates Encapsulation and Set Collections.
 */
public class EligibilityCriteria {

    private double minCgpa;
    private double minTenthPercentage;
    private double minTwelfthPercentage;
    private int maxAllowedActiveBacklogs;
    private final Set<String> allowedBranches;
    private int graduationBatchYear; // 0 represents any batch

    public EligibilityCriteria(double minCgpa, double minTenthPercentage, double minTwelfthPercentage,
                               int maxAllowedActiveBacklogs, Set<String> allowedBranches, int graduationBatchYear) {
        this.minCgpa = minCgpa;
        this.minTenthPercentage = minTenthPercentage;
        this.minTwelfthPercentage = minTwelfthPercentage;
        this.maxAllowedActiveBacklogs = maxAllowedActiveBacklogs;
        this.allowedBranches = new LinkedHashSet<>();
        if (allowedBranches != null) {
            for (String b : allowedBranches) {
                if (b != null && !b.trim().isEmpty()) {
                    this.allowedBranches.add(b.trim().toUpperCase());
                }
            }
        }
        this.graduationBatchYear = graduationBatchYear;
    }

    public boolean isBranchAllowed(String branch) {
        if (branch == null) {
            return false;
        }
        // If allowedBranches is empty, all branches are eligible
        if (allowedBranches.isEmpty()) {
            return true;
        }
        return allowedBranches.contains(branch.trim().toUpperCase());
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public double getMinTenthPercentage() {
        return minTenthPercentage;
    }

    public void setMinTenthPercentage(double minTenthPercentage) {
        this.minTenthPercentage = minTenthPercentage;
    }

    public double getMinTwelfthPercentage() {
        return minTwelfthPercentage;
    }

    public void setMinTwelfthPercentage(double minTwelfthPercentage) {
        this.minTwelfthPercentage = minTwelfthPercentage;
    }

    public int getMaxAllowedActiveBacklogs() {
        return maxAllowedActiveBacklogs;
    }

    public void setMaxAllowedActiveBacklogs(int maxAllowedActiveBacklogs) {
        this.maxAllowedActiveBacklogs = maxAllowedActiveBacklogs;
    }

    public Set<String> getAllowedBranches() {
        return Collections.unmodifiableSet(allowedBranches);
    }

    public void addAllowedBranch(String branch) {
        if (branch != null && !branch.trim().isEmpty()) {
            this.allowedBranches.add(branch.trim().toUpperCase());
        }
    }

    public int getGraduationBatchYear() {
        return graduationBatchYear;
    }

    public void setGraduationBatchYear(int graduationBatchYear) {
        this.graduationBatchYear = graduationBatchYear;
    }

    @Override
    public String toString() {
        String branchesStr = allowedBranches.isEmpty() ? "ALL" : String.join(", ", allowedBranches);
        return String.format("Min CGPA: %.2f | Min 10th: %.1f%% | Min 12th: %.1f%% | Max Active Backlogs: %d | Branches: [%s] | Batch: %s",
                minCgpa, minTenthPercentage, minTwelfthPercentage, maxAllowedActiveBacklogs, branchesStr,
                (graduationBatchYear > 0 ? String.valueOf(graduationBatchYear) : "Any"));
    }
}
