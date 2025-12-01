import java.sql.*;
import java.util.Scanner;

public class Main {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/fitness_club";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    private static Connection connection;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            // Initialize database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            scanner = new Scanner(System.in);

            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║  Health & Fitness Club Management System   ║");
            System.out.println("╚════════════════════════════════════════════╝");

            // Main application loop
            boolean running = true;
            while (running) {
                running = showMainMenu();
            }

            // Cleanup
            System.out.println("\nThank you for using the Fitness Club Management System!");
            scanner.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main menu - Role selection and registration
     * @return true to continue, false to exit
     */
    private static boolean showMainMenu() {
        System.out.println("\n========================================");
        System.out.println("           MAIN MENU");
        System.out.println("========================================");
        System.out.println("1. Login as Member");
        System.out.println("2. Login as Trainer");
        System.out.println("3. Login as Admin");
        System.out.println("4. Register New Member Account");
        System.out.println("5. Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                memberLogin();
                break;
            case 2:
                trainerLogin();
                break;
            case 3:
                adminLogin();
                break;
            case 4:
                registerNewMember();
                break;
            case 5:
                return false; // Exit the application
            default:
                System.out.println("Invalid choice! Please select 1-5.");
        }

        return true; // Continue running
    }

    // ============================================================================
    // LOGIN FUNCTIONS
    // ============================================================================

    /**
     * Member login - authenticate and show member menu
     */
    private static void memberLogin() {
        System.out.println("\n--- MEMBER LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT member_id, first_name, last_name FROM Members WHERE email = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int memberId = rs.getInt("member_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                System.out.println("\nLogin successful! Welcome, " + firstName + " " + lastName + "!");
                memberMenu(memberId);
            } else {
                System.out.println("Invalid email or password!");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    /**
     * Trainer login - authenticate and show trainer menu
     */
    private static void trainerLogin() {
        System.out.println("\n--- TRAINER LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT trainer_id, first_name, last_name FROM Trainers WHERE email = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int trainerId = rs.getInt("trainer_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                System.out.println("\nLogin successful! Welcome, Trainer " + firstName + " " + lastName + "!");
                trainerMenu(trainerId);
            } else {
                System.out.println("Invalid email or password!");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    /**
     * Admin login - authenticate and show admin menu
     */
    private static void adminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT admin_id, name FROM Admins WHERE email = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int adminId = rs.getInt("admin_id");
                String name = rs.getString("name");

                System.out.println("\nLogin successful! Welcome, Admin " + name + "!");
                adminMenu(adminId);
            } else {
                System.out.println("Invalid email or password!");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    /**
     * Register new member account
     */
    private static void registerNewMember() {
        MemberFunctions memberFunctions = new MemberFunctions(connection, scanner);
        memberFunctions.userRegistration();
    }

    // ============================================================================
    // ROLE-SPECIFIC MENUS
    // ============================================================================

    /**
     * Member menu - display member operations
     */
    private static void memberMenu(int memberId) {
        MemberFunctions memberFunctions = new MemberFunctions(connection, scanner);

        boolean inMemberMenu = true;
        while (inMemberMenu) {
            System.out.println("\n========================================");
            System.out.println("          MEMBER MENU");
            System.out.println("========================================");
            System.out.println("1. View Dashboard");
            System.out.println("2. Profile Management");
            System.out.println("3. Schedule Personal Training Session");
            System.out.println("4. Register for Group Class");
            System.out.println("5. Log Health Metrics");
            System.out.println("6. Logout");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    memberFunctions.viewDashboard(memberId);
                    break;
                case 2:
                    memberFunctions.profileManagement(memberId);
                    break;
                case 3:
                    memberFunctions.schedulePersonalSession(memberId);
                    break;
                case 4:
                    memberFunctions.registerForClass(memberId);
                    break;
                case 5:
                    memberFunctions.logHealthMetrics(memberId);
                    break;
                case 6:
                    System.out.println("\n👋 Logging out...");
                    inMemberMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select 1-6.");
            }
        }
    }

    /**
     * Trainer menu - display trainer operations
     */
    private static void trainerMenu(int trainerId) {
        TrainerFunctions trainerFunctions = new TrainerFunctions(connection, scanner);

        boolean inTrainerMenu = true;
        while (inTrainerMenu) {
            System.out.println("\n========================================");
            System.out.println("          TRAINER MENU");
            System.out.println("========================================");
            System.out.println("1. Set Availability");
            System.out.println("2. View Schedule");
            System.out.println("3. Search Member Profile");
            System.out.println("4. Logout");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    trainerFunctions.setAvailability(trainerId);
                    break;
                case 2:
                    trainerFunctions.viewSchedule(trainerId);
                    break;
                case 3:
                    trainerFunctions.searchMember(trainerId);
                    break;
                case 4:
                    System.out.println("\nLogging out...");
                    inTrainerMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        }
    }

    /**
     * Admin menu - display admin operations
     */
    private static void adminMenu(int adminId) {
        AdminFunctions adminFunctions = new AdminFunctions(connection, scanner);

        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n========================================");
            System.out.println("          ADMIN MENU");
            System.out.println("========================================");
            System.out.println("1. Manage Room Bookings");
            System.out.println("2. Manage Class Schedule");
            System.out.println("3. Monitor Equipment Maintenance");
            System.out.println("4. Process Billing & Payments");
            System.out.println("5. Logout");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    adminFunctions.manageRoomBooking();
                    break;
                case 2:
                    adminFunctions.manageClassSchedule();
                    break;
                case 3:
                    adminFunctions.monitorEquipmentMaintenance();
                    break;
                case 4:
                    adminFunctions.processBillingAndPayments();
                    break;
                case 5:
                    System.out.println("\nLogging out...");
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select 1-5.");
            }
        }
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Get integer input with error handling
     */
    private static int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }
}