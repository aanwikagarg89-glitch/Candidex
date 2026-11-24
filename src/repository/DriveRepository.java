package repository;

import model.Company;
import model.PlacementDrive;

import java.util.*;

/**
 * Unified in-memory repository managing Company recruiter profiles
 * and their published Placement Drives.
 * Demonstrates LinkedHashMap, Optional API, and synchronized multi-entity persistence.
 */
public class DriveRepository {

    private final Map<String, Company> companyById = new LinkedHashMap<>();
    private final Map<String, Company> companyByEmail = new HashMap<>();
    private final Map<String, PlacementDrive> driveById = new LinkedHashMap<>();

    // --- Company Operations ---

    public synchronized void saveCompany(Company company) {
        if (company == null) return;
        companyById.put(company.getUserId(), company);
        if (company.getEmail() != null) {
            companyByEmail.put(company.getEmail().toLowerCase(), company);
        }
    }

    public synchronized Optional<Company> findCompanyById(String companyId) {
        if (companyId == null) return Optional.empty();
        return Optional.ofNullable(companyById.get(companyId));
    }

    public synchronized Optional<Company> findCompanyByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(companyByEmail.get(email.toLowerCase()));
    }

    public synchronized boolean existsCompanyByEmail(String email) {
        if (email == null) return false;
        return companyByEmail.containsKey(email.toLowerCase());
    }

    public synchronized List<Company> findAllCompanies() {
        return new ArrayList<>(companyById.values());
    }

    // --- Placement Drive Operations ---

    public synchronized void saveDrive(PlacementDrive drive) {
        if (drive == null) return;
        driveById.put(drive.getDriveId(), drive);
    }

    public synchronized Optional<PlacementDrive> findDriveById(String driveId) {
        if (driveId == null) return Optional.empty();
        return Optional.ofNullable(driveById.get(driveId));
    }

    public synchronized List<PlacementDrive> findAllDrives() {
        return new ArrayList<>(driveById.values());
    }

    public synchronized List<PlacementDrive> findOpenDrives() {
        List<PlacementDrive> openDrives = new ArrayList<>();
        for (PlacementDrive d : driveById.values()) {
            if (d.isOpen()) {
                openDrives.add(d);
            }
        }
        return openDrives;
    }

    public synchronized List<PlacementDrive> findDrivesByCompanyId(String companyId) {
        if (companyId == null) return Collections.emptyList();
        List<PlacementDrive> companyDrives = new ArrayList<>();
        for (PlacementDrive d : driveById.values()) {
            if (companyId.equalsIgnoreCase(d.getCompanyId())) {
                companyDrives.add(d);
            }
        }
        return companyDrives;
    }
}
