package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Demonstrates JDBC connectivity strictly within the syllabus boundary.
 * Consolidates database configuration, URL formation, driver loading,
 * DriverManager, Connection establishment, and educational architecture guide.
 *
 * NOTE: As per syllabus requirements, NO CRUD operations, PreparedStatement,
 * ResultSet, or Statement operations are included.
 */
public class DBConnectionManager {

    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "candidex_db";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "password";

    // Standard JDBC Connection URL structure: jdbc:subprotocol://host:port/database
    public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    private DBConnectionManager() {
        // Prevent direct instantiation
    }

    /**
     * Displays database URL structure and driver category metadata.
     */
    public static void displayMetadata() {
        System.out.println("================================================================");
        System.out.println("              JDBC ARCHITECTURE & URL METADATA                  ");
        System.out.println("================================================================");
        System.out.printf("JDBC Driver Class : %s%n", DB_DRIVER);
        System.out.printf("Driver Category   : Type 4 (Direct-to-Database Native-Protocol Thin Driver)%n");
        System.out.printf("Database Engine   : MySQL Relational Database Management System (RDBMS)%n");
        System.out.printf("Host & Port       : %s:%d%n", DB_HOST, DB_PORT);
        System.out.printf("Database Name     : %s%n", DB_NAME);
        System.out.printf("Constructed URL   : %s%n", DB_URL);
        System.out.printf("Default Username  : %s%n", DB_USER);
        System.out.println("================================================================");
    }

    /**
     * Establishes a physical database connection using DriverManager.
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        // Step 1: Explicitly load the MySQL Driver class into memory (JDBC Architecture requirement)
        Class.forName(DB_DRIVER);

        // Step 2: Establish connection via DriverManager using the Database URL and credentials
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Diagnostic method to test physical database connectivity without modifying any data.
     */
    public static boolean testConnection() {
        System.out.println("================================================================");
        System.out.println("             TESTING MYSQL DATABASE CONNECTIVITY                ");
        System.out.println("================================================================");
        System.out.println("Initiating JDBC Connection handshake via DriverManager...");
        System.out.printf("Target URL: %s%n", DB_URL);

        Connection connection = null;
        try {
            connection = getConnection();
            if (connection != null && !connection.isClosed()) {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("  ✔ Connection Successfully Established!");
                System.out.printf("  Database Product : %s %s%n", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
                System.out.printf("  Driver Name      : %s %s%n", metaData.getDriverName(), metaData.getDriverVersion());
                System.out.printf("  Active Catalog   : %s%n", connection.getCatalog());
                System.out.printf("  Auto-Commit Mode : %b%n", connection.getAutoCommit());
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("  [!] Driver Loading Error: MySQL JDBC Driver class not found on classpath.");
            System.err.println("      Add 'mysql-connector-j.jar' to the classpath to connect to a live MySQL instance.");
        } catch (SQLException e) {
            System.err.printf("  [!] SQL / Connection Handshake Failed: %s%n", e.getMessage());
            System.err.printf("      SQL State: %s | Error Code: %d%n", e.getSQLState(), e.getErrorCode());
            System.out.println("  Note: If local MySQL service is offline, this is expected behavior.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("  Connection closed gracefully in finally block.");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
            System.out.println("================================================================");
        }
        return false;
    }

    /**
     * Viva-oriented educational breakdown of the JDBC architecture.
     */
    public static void printJdbcVivaArchitecture() {
        System.out.println("================================================================");
        System.out.println("              JDBC SYLLABUS ARCHITECTURAL GUIDE                 ");
        System.out.println("================================================================");
        System.out.println("1. JDBC ARCHITECTURE LAYERS:");
        System.out.println("   [Java Application (CANDIDEX)]");
        System.out.println("                 |");
        System.out.println("                 v");
        System.out.println("       [JDBC API (java.sql.*)]");
        System.out.println("                 |");
        System.out.println("                 v");
        System.out.println("       [DriverManager Class]");
        System.out.println("                 |");
        System.out.println("                 v");
        System.out.println("     [JDBC Driver (Type 4 Thin)]");
        System.out.println("                 |");
        System.out.println("                 v");
        System.out.println("      [MySQL Relational DBMS]");
        System.out.println();
        System.out.println("2. JDBC DRIVER TYPES:");
        System.out.println("   - Type 1: JDBC-ODBC Bridge (Legacy, removed in Java 8).");
        System.out.println("   - Type 2: Native-API Driver (Part Java, part C/C++ client libraries).");
        System.out.println("   - Type 3: Network-Protocol Driver (Middleware server translation).");
        System.out.println("   - Type 4: Pure Java / Thin Driver (Direct socket connection to DB).");
        System.out.println();
        System.out.println("3. DATABASE URL ANATOMY:");
        System.out.println("   Format: jdbc:<subprotocol>://<host>:<port>/<database>");
        System.out.println("   Example: jdbc:mysql://localhost:3306/candidex_db");
        System.out.println();
        System.out.println("4. CORE JDBC INTERFACES & CLASSES:");
        System.out.println("   - DriverManager: Manages loaded drivers & factory for Connections.");
        System.out.println("   - Connection: Represents the physical session established with DB.");
        System.out.println("   - SQLException: Checked exception reporting database access errors.");
        System.out.println("================================================================");
    }
}
