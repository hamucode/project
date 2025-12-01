import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MemberFunctions {

    private Connection connection;
    private Scanner scanner;

    public MemberFunctions(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    /**
     * FUNCTION 1: USER REGISTRATION
     * Creates a new member account with validation
     */
    public void userRegistration() {
        System.out.println("\n========== USER REGISTRATION ==========");

        try {
            // Collect user input
            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim().toLowerCase();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dobInput = scanner.nextLine().trim();

            System.out.print("Gender (Male/Female): ");
            String gender = scanner.nextLine().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || password.isEmpty()) {
                System.out.println("ERROR: All fields are required!");
                return;
            }

            if (!isValidEmail(email)) {
                System.out.println("ERROR: Invalid email format!");
                return;
            }

            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(dobInput, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: Invalid date format! Use YYYY-MM-DD");
                return;
            }

            // Check age (must be 18+)
            int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

            // Check if email already exists
            if (emailExists(email)) {
                System.out.println("ERROR: Email already registered! " +
                        "Please use a different email.");
                return;
            }

            // Insert new member into database
            String sql = "INSERT INTO Members (first_name, last_name, email, password, date_of_birth, gender, join_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";

            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setDate(5, Date.valueOf(dateOfBirth));
            stmt.setString(6, gender);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int memberId = generatedKeys.getInt(1);
                    System.out.println("\nSUCCESS! Registration complete!");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.out.println("Member ID: " + memberId);
                    System.out.println("Name: " + firstName + " " + lastName);
                    System.out.println("Email: " + email);
                    System.out.println("Age: " + age + " years");
                    System.out.println("Join Date: " + LocalDate.now());
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                }
            }

            stmt.close();

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * FUNCTION 2: PROFILE MANAGEMENT
     * TODO: Implement update personal details and fitness goals
     */
    public void profileManagement(int memberId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] Profile Management - Not yet implemented");
    }

    /**
     * FUNCTION 3: DASHBOARD DISPLAY
     * TODO: Show latest health metrics, goals, classes, and upcoming sessions
     */
    public void viewDashboard(int memberId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] Dashboard Display - Not yet implemented");
    }

    /**
     * FUNCTION 4: PERSONAL TRAINING SESSION SCHEDULING
     * TODO: Book or reschedule PT sessions with validation
     */
    public void schedulePersonalSession(int memberId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] PT Session Scheduling - Not yet implemented");
    }

    /**
     * FUNCTION 5: GROUP CLASS REGISTRATION
     * TODO: Register for group fitness classes
     */
    public void registerForClass(int memberId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] Class Registration - Not yet implemented");
    }

    /**
     * FUNCTION 6: HEALTH METRICS LOGGING
     * TODO: Add new health metric entries (weight, heart rate)
     */
    public void logHealthMetrics(int memberId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] Health Metrics Logging - Not yet implemented");
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Check if email already exists in database
     */
    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Members WHERE email = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }

        rs.close();
        stmt.close();
        return exists;
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}