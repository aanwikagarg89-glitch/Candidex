package app;

import repository.DataStore;

/**
 * Main application bootstrap for CANDIDEX (Campus Placement Management System).
 * Demonstrates static void main entry point, runtime subsystem initialization,
 * and presentation loop lifecycle.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("                WELCOME TO CANDIDEX                             ");
        System.out.println("        Campus Placement & Recruitment Management System        ");
        System.out.println("================================================================");
        System.out.println("Initializing runtime in-memory data store...");

        // 1. Initialize runtime data store and seed viva demonstration data
        DataStore dataStore = new DataStore();
        dataStore.seedDemonstrationData();

        // 2. Initialize user session manager
        SessionManager sessionManager = new SessionManager();

        // 3. Launch interactive CLI menu
        ConsoleMenu menu = new ConsoleMenu(dataStore, sessionManager);
        try {
            menu.start();
        } catch (Exception e) {
            System.err.println("Fatal application error encountered: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
