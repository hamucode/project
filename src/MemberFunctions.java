import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MemberFunctions {

    private Connection connection;
    private static Scanner scanner;

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
     * Update personal details, fitness goals, and input new health metrics
     */
    public void profileManagement(int memberId) {
        boolean inProfileMenu = true;
        while (inProfileMenu) {
            System.out.println("\n========================================");
            System.out.println("       PROFILE MANAGEMENT");
            System.out.println("========================================");
            System.out.println("1. Update Personal Details");
            System.out.println("2. Manage Fitness Goals");
            System.out.println("3. Input Health Metrics");
            System.out.println("4. Back to Member Menu");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    updatePersonalDetails(memberId);
                    break;
                case 2:
                    manageFitnessGoals(memberId);
                    break;
                case 3:
                    inputHealthMetrics(memberId);
                    break;
                case 4:
                    System.out.println("\nReturning to Member Menu...");
                    inProfileMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        }
    }

    /**
     * Update member's personal details
     */
    private void updatePersonalDetails(int memberId) {
        System.out.println("\n--- UPDATE PERSONAL DETAILS ---");

        try {
            System.out.print("Enter new first name (or press Enter to skip): ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Enter new last name (or press Enter to skip): ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Enter new email (or press Enter to skip): ");
            String email = scanner.nextLine().trim().toLowerCase();

            // Build dynamic UPDATE query based on what user provided
            StringBuilder sql = new StringBuilder("UPDATE Members SET ");
            boolean hasUpdates = false;

            if (!firstName.isEmpty()) {
                sql.append("first_name = ?");
                hasUpdates = true;
            }

            if (!lastName.isEmpty()) {
                if (hasUpdates) sql.append(", ");
                sql.append("last_name = ?");
                hasUpdates = true;
            }

            if (!email.isEmpty()) {
                if (!isValidEmail(email)) {
                    System.out.println("Invalid email format!");
                    return;
                }
                if (emailExists(email)) {
                    System.out.println("Email already in use!");
                    return;
                }
                if (hasUpdates) sql.append(", ");
                sql.append("email = ?");
                hasUpdates = true;
            }

            if (!hasUpdates) {
                System.out.println("No updates provided.");
                return;
            }

            sql.append(" WHERE member_id = ?");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (!firstName.isEmpty()) {
                stmt.setString(paramIndex++, firstName);
            }
            if (!lastName.isEmpty()) {
                stmt.setString(paramIndex++, lastName);
            }
            if (!email.isEmpty()) {
                stmt.setString(paramIndex++, email);
            }
            stmt.setInt(paramIndex, memberId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Personal details updated successfully!");
            } else {
                System.out.println("Update failed!");
            }

            stmt.close();

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Manage fitness goals (add or update)
     */
    private void manageFitnessGoals(int memberId) {
        System.out.println("\n--- MANAGE FITNESS GOALS ---");
        System.out.println("1. Add New Goal");
        System.out.println("2. Update Existing Goal");

        System.out.print("Choice: ");

        int choice = getIntInput();

        if (choice == 1) {
            addFitnessGoal(memberId);
        } else if (choice == 2) {
            updateFitnessGoal(memberId);

        } else {
            System.out.println("Invalid choice!");
        }
    }

    /**
     * Add a new fitness goal
     */
    private void addFitnessGoal(int memberId) {
        System.out.println("\n--- ADD FITNESS GOAL ---");

        try {
            System.out.print("Goal Type (e.g., Weight Loss, Muscle Gain): ");
            String goalType = scanner.nextLine().trim();

            System.out.print("Target Value (e.g., 75.5): ");
            double targetValue = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Target Date (YYYY-MM-DD): ");
            String targetDateStr = scanner.nextLine().trim();

            if (goalType.isEmpty() || targetValue <= 0) {
                System.out.println("Invalid input!");
                return;
            }

            java.sql.Date targetDate = java.sql.Date.valueOf(targetDateStr);

            String sql = "INSERT INTO FitnessGoals (member_id, goal_type, target_value, target_date, status) " +
                    "VALUES (?, ?, ?, ?, 'In Progress')";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, memberId);
            stmt.setString(2, goalType);
            stmt.setDouble(3, targetValue);
            stmt.setDate(4, targetDate);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Fitness goal added successfully!");
            }

            stmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format! Use YYYY-MM-DD");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Update existing fitness goal
     */
    private void updateFitnessGoal(int memberId) {
        System.out.println("\n--- UPDATE FITNESS GOAL ---");

        try {
            // Show current goals
            String sql = "SELECT goal_id, goal_type, target_value, target_date, status " +
                    "FROM FitnessGoals WHERE member_id = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nYour Goals:");
            boolean hasGoals = false;
            while (rs.next()) {
                hasGoals = true;
                System.out.println("ID: " + rs.getInt("goal_id") + " | " +
                        rs.getString("goal_type") + " | Target: " +
                        rs.getDouble("target_value") + " | Status: " +
                        rs.getString("status"));
            }

            rs.close();
            stmt.close();

            if (!hasGoals) {
                System.out.println("No goals found.");
                return;
            }

            System.out.print("\nEnter Goal ID to update: ");
            int goalId = getIntInput();

            System.out.print("Enter new target value (or 0 to skip): ");
            double newTarget = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter new target date (YYYY-MM-DD or press Enter to skip): ");
            String newDateStr = scanner.nextLine().trim();

            // Build update query
            StringBuilder updateSql = new StringBuilder("UPDATE FitnessGoals SET ");
            boolean hasUpdates = false;

            if (newTarget > 0) {
                updateSql.append("target_value = ?");
                hasUpdates = true;
            }

            if (!newDateStr.isEmpty()) {
                if (hasUpdates) updateSql.append(", ");
                updateSql.append("target_date = ?");
                hasUpdates = true;
            }

            if (!hasUpdates) {
                System.out.println("No updates provided.");
                return;
            }

            updateSql.append(" WHERE goal_id = ? AND member_id = ?");

            PreparedStatement updateStmt = connection.prepareStatement(updateSql.toString());
            int paramIndex = 1;

            if (newTarget > 0) {
                updateStmt.setDouble(paramIndex++, newTarget);
            }
            if (!newDateStr.isEmpty()) {
                updateStmt.setDate(paramIndex++, java.sql.Date.valueOf(newDateStr));
            }
            updateStmt.setInt(paramIndex++, goalId);
            updateStmt.setInt(paramIndex, memberId);

            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Goal updated successfully!");
            } else {
                System.out.println("Goal not found!");
            }

            updateStmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format!");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Input new health metrics (weight, heart rate)
     */
    private void inputHealthMetrics(int memberId) {
        System.out.println("\n--- INPUT HEALTH METRICS ---");

        try {
            System.out.print("Enter weight (kg): ");
            double weight = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter heart rate (bpm): ");
            int heartRate = Integer.parseInt(scanner.nextLine().trim());

            if (weight <= 0 || heartRate <= 0) {
                System.out.println("Invalid values! Must be positive.");
                return;
            }

            String sql = "INSERT INTO HealthMetrics (member_id, weight, heart_rate, date_recorded) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, memberId);
            stmt.setDouble(2, weight);
            stmt.setInt(3, heartRate);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Health metrics recorded successfully!");
            }

            stmt.close();

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * FUNCTION 3: DASHBOARD DISPLAY
     * Show latest health metrics, goals, classes, and upcoming sessions
     */
    public void viewDashboard(int memberId) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║          MEMBER DASHBOARD                  ║");
        System.out.println("╚════════════════════════════════════════════╝");

        try {
            // Get member info
            displayMemberInfo(memberId);

            // Display latest health metrics
            displayLatestHealthMetrics(memberId);

            // Display active fitness goals
            displayActiveFitnessGoals(memberId);

            // Display class participation count
//            displayClassParticipation(memberId);

            // Display upcoming sessions
            displayUpcomingSessions(memberId);

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Display member basic information
     */
    private void displayMemberInfo(int memberId) throws SQLException {
        String sql = "SELECT first_name, last_name, email, join_date FROM Members WHERE member_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("\nMEMBER INFORMATION");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Member Since: " + rs.getDate("join_date"));
        }

        rs.close();
        stmt.close();
    }

    /**
     * Display latest health metrics
     */
    private void displayLatestHealthMetrics(int memberId) throws SQLException {
        String sql = "SELECT weight, heart_rate, date_recorded " +
                "FROM HealthMetrics " +
                "WHERE member_id = ? " +
                "ORDER BY date_recorded DESC LIMIT 1";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nLATEST HEALTH METRICS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (rs.next()) {
            System.out.println("Weight: " + rs.getDouble("weight") + " kg");
            System.out.println("Heart Rate: " + rs.getInt("heart_rate") + " bpm");
            System.out.println("Recorded: " + rs.getTimestamp("date_recorded"));
        } else {
            System.out.println("No health metrics recorded yet.");
            System.out.println("Use 'Log Health Metrics' to add your first entry!");
        }

        rs.close();
        stmt.close();
    }

    /**
     * Display active fitness goals with progress
     */
    private void displayActiveFitnessGoals(int memberId) throws SQLException {
        String sql = "SELECT goal_type, target_value, target_date, status " +
                "FROM FitnessGoals " +
                "WHERE member_id = ? AND status = 'In Progress' " +
                "ORDER BY target_date";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nACTIVE FITNESS GOALS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        boolean hasGoals = false;
        while (rs.next()) {
            hasGoals = true;
            System.out.println("Goal: " + rs.getString("goal_type"));
            System.out.println("Target: " + rs.getDouble("target_value"));
            System.out.println("Target Date: " + rs.getDate("target_date"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - -");
        }

        if (!hasGoals) {
            System.out.println("No active goals set.");
            System.out.println("Use 'Profile Management' to add fitness goals!");
        }

        rs.close();
        stmt.close();
    }

    /**
     * Display total class participation count
     */
//    private void displayClassParticipation(int memberId) throws SQLException {
//        String sql = "SELECT COUNT(*) as class_count FROM ClassRegistrations WHERE member_id = ?";
//
//        PreparedStatement stmt = connection.prepareStatement(sql);
//        stmt.setInt(1, memberId);
//        ResultSet rs = stmt.executeQuery();
//
//        System.out.println("\nCLASS PARTICIPATION");
//        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
//
//        if (rs.next()) {
//            int classCount = rs.getInt("class_count");
//            System.out.println("Total Classes Registered: " + classCount);
//        }
//
//        rs.close();
//        stmt.close();
//    }

    /**
     * Display upcoming personal training sessions and group classes
     */
    private void displayUpcomingSessions(int memberId) throws SQLException {
        String sql = "SELECT type, activity, start_datetime, room_name, trainer_name " +
                "FROM v_MemberSchedule " +
                "WHERE member_id = ? AND start_datetime > NOW() " +
                "ORDER BY start_datetime LIMIT 5";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nUPCOMING SCHEDULE (Next 5 Events)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        boolean hasSessions = false;
        int count = 1;

        while (rs.next()) {
            hasSessions = true;
            System.out.println(count + ". " + rs.getString("type"));
            System.out.println("   Activity: " + rs.getString("activity"));
            System.out.println("   Time: " + rs.getTimestamp("start_datetime"));
            System.out.println("   Room: " + rs.getString("room_name"));
            System.out.println("   Trainer: " + rs.getString("trainer_name"));
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - -");
            count++;
        }

        if (!hasSessions) {
            System.out.println("No upcoming sessions scheduled.");
            System.out.println("Use 'Schedule PT Session'!");
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        rs.close();
        stmt.close();
    }

    /**
     * FUNCTION 4: PERSONAL TRAINING SESSION SCHEDULING
     * Book or reschedule training with a trainer, validating availability and room conflicts
     */
    public void schedulePersonalSession(int memberId) {
        System.out.println("\n========================================");
        System.out.println("   PERSONAL TRAINING SESSION SCHEDULING");
        System.out.println("========================================");
        System.out.println("1. Book New Session");
        System.out.println("2. Reschedule Existing Session");
        System.out.println("3. Back to Menu");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                bookNewSession(memberId);
                break;
            case 2:
                rescheduleSession(memberId);
                break;
            case 3:
                System.out.println("Returning to menu...");
                break;
            default:
                System.out.println("Invalid choice! Please select 1-3.");
        }
    }

    /**
     * Book a new personal training session
     */
    private void bookNewSession(int memberId) {
        System.out.println("\n--- BOOK NEW SESSION ---");

        try {
            // Get trainer ID
            System.out.print("Enter Trainer ID: ");
            int trainerId = getIntInput();

            // Get session details
            System.out.print("Enter session date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();

            System.out.print("Enter start time (HH:MM): ");
            String startTimeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Enter end time (HH:MM): ");
            String endTimeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Enter Room ID: ");
            int roomId = getIntInput();

            java.sql.Date sessionDate = java.sql.Date.valueOf(dateStr);
            java.sql.Time startTime = java.sql.Time.valueOf(startTimeStr);
            java.sql.Time endTime = java.sql.Time.valueOf(endTimeStr);

            // Validate: end time after start time
            if (!endTime.after(startTime)) {
                System.out.println("ERROR: End time must be after start time!");
                return;
            }

            // Check trainer availability
            String dayOfWeek = sessionDate.toLocalDate().getDayOfWeek().toString();
            dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

            String checkTrainerSql = "SELECT COUNT(*) FROM TrainerAvailability " +
                    "WHERE trainer_id = ? AND day_of_week = ? " +
                    "AND start_time <= ? AND end_time >= ?";

            PreparedStatement checkTrainerStmt = connection.prepareStatement(checkTrainerSql);
            checkTrainerStmt.setInt(1, trainerId);
            checkTrainerStmt.setString(2, dayOfWeek);
            checkTrainerStmt.setTime(3, startTime);
            checkTrainerStmt.setTime(4, endTime);
            ResultSet trainerRs = checkTrainerStmt.executeQuery();

            trainerRs.next();
            if (trainerRs.getInt(1) == 0) {
                System.out.println("ERROR: Trainer not available at requested time!");
                trainerRs.close();
                checkTrainerStmt.close();
                return;
            }
            trainerRs.close();
            checkTrainerStmt.close();

            // Check room availability (no conflicts)
            String checkRoomSql = "SELECT COUNT(*) FROM PersonalSessions " +
                    "WHERE room_id = ? AND date = ? AND status != 'Cancelled' " +
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement checkRoomStmt = connection.prepareStatement(checkRoomSql);
            checkRoomStmt.setInt(1, roomId);
            checkRoomStmt.setDate(2, sessionDate);
            checkRoomStmt.setTime(3, startTime);
            checkRoomStmt.setTime(4, endTime);
            ResultSet roomRs = checkRoomStmt.executeQuery();

            roomRs.next();
            if (roomRs.getInt(1) > 0) {
                System.out.println("ERROR: Room is already booked at that time!");
                roomRs.close();
                checkRoomStmt.close();
                return;
            }
            roomRs.close();
            checkRoomStmt.close();

            // Insert session
            String insertSql = "INSERT INTO PersonalSessions (date, start_time, end_time, status, member_id, trainer_id, room_id) " +
                    "VALUES (?, ?, ?, 'Booked', ?, ?, ?)";

            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setDate(1, sessionDate);
            insertStmt.setTime(2, startTime);
            insertStmt.setTime(3, endTime);
            insertStmt.setInt(4, memberId);
            insertStmt.setInt(5, trainerId);
            insertStmt.setInt(6, roomId);

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nSUCCESS! Session booked.");
            } else {
                System.out.println("ERROR: Failed to book session.");
            }

            insertStmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid date/time format!");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Reschedule an existing session
     */
    private void rescheduleSession(int memberId) {
        System.out.println("\n--- RESCHEDULE SESSION ---");

        try {
            System.out.print("Enter Session ID to reschedule: ");
            int sessionId = getIntInput();

            System.out.print("Enter new date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();

            System.out.print("Enter new start time (HH:MM): ");
            String startTimeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Enter new end time (HH:MM): ");
            String endTimeStr = scanner.nextLine().trim() + ":00";

            java.sql.Date newDate = java.sql.Date.valueOf(dateStr);
            java.sql.Time newStartTime = java.sql.Time.valueOf(startTimeStr);
            java.sql.Time newEndTime = java.sql.Time.valueOf(endTimeStr);

            // Validate: end time after start time
            if (!newEndTime.after(newStartTime)) {
                System.out.println("ERROR: End time must be after start time!");
                return;
            }

            // Get trainer_id and room_id for this session
            String getSessionSql = "SELECT trainer_id, room_id FROM PersonalSessions " +
                    "WHERE session_id = ? AND member_id = ?";
            PreparedStatement getSessionStmt = connection.prepareStatement(getSessionSql);
            getSessionStmt.setInt(1, sessionId);
            getSessionStmt.setInt(2, memberId);
            ResultSet sessionRs = getSessionStmt.executeQuery();

            if (!sessionRs.next()) {
                System.out.println("ERROR: Session not found!");
                sessionRs.close();
                getSessionStmt.close();
                return;
            }

            int trainerId = sessionRs.getInt("trainer_id");
            int roomId = sessionRs.getInt("room_id");
            sessionRs.close();
            getSessionStmt.close();

            // Check trainer availability for new time
            String dayOfWeek = newDate.toLocalDate().getDayOfWeek().toString();
            dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

            String checkTrainerSql = "SELECT COUNT(*) FROM TrainerAvailability " +
                    "WHERE trainer_id = ? AND day_of_week = ? " +
                    "AND start_time <= ? AND end_time >= ?";

            PreparedStatement checkTrainerStmt = connection.prepareStatement(checkTrainerSql);
            checkTrainerStmt.setInt(1, trainerId);
            checkTrainerStmt.setString(2, dayOfWeek);
            checkTrainerStmt.setTime(3, newStartTime);
            checkTrainerStmt.setTime(4, newEndTime);
            ResultSet trainerRs = checkTrainerStmt.executeQuery();

            trainerRs.next();
            if (trainerRs.getInt(1) == 0) {
                System.out.println("ERROR: Trainer not available at new time!");
                trainerRs.close();
                checkTrainerStmt.close();
                return;
            }
            trainerRs.close();
            checkTrainerStmt.close();

            // Check room availability (exclude current session)
            String checkRoomSql = "SELECT COUNT(*) FROM PersonalSessions " +
                    "WHERE room_id = ? AND date = ? AND status != 'Cancelled' " +
                    "AND session_id != ? " +
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement checkRoomStmt = connection.prepareStatement(checkRoomSql);
            checkRoomStmt.setInt(1, roomId);
            checkRoomStmt.setDate(2, newDate);
            checkRoomStmt.setInt(3, sessionId);
            checkRoomStmt.setTime(4, newStartTime);
            checkRoomStmt.setTime(5, newEndTime);
            ResultSet roomRs = checkRoomStmt.executeQuery();

            roomRs.next();
            if (roomRs.getInt(1) > 0) {
                System.out.println("ERROR: Room conflict at new time!");
                roomRs.close();
                checkRoomStmt.close();
                return;
            }
            roomRs.close();
            checkRoomStmt.close();

            // Update session
            String updateSql = "UPDATE PersonalSessions " +
                    "SET date = ?, start_time = ?, end_time = ? " +
                    "WHERE session_id = ? AND member_id = ?";

            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setDate(1, newDate);
            updateStmt.setTime(2, newStartTime);
            updateStmt.setTime(3, newEndTime);
            updateStmt.setInt(4, sessionId);
            updateStmt.setInt(5, memberId);

            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nSUCCESS! Session rescheduled.");
            } else {
                System.out.println("ERROR: Failed to reschedule session.");
            }

            updateStmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid date/time format!");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

//    /**
//     * FUNCTION 5: GROUP CLASS REGISTRATION
//     * TODO: Register for group fitness classes
//     */
//    public void registerForClass(int memberId) {
//        // TODO: Implement this function
//        System.out.println("\n[TODO] Class Registration - Not yet implemented");
//    }

    /**
     * FUNCTION 6: HEALTH METRICS LOGGING
     */
    public void logHealthMetrics(int memberId)  {
        try{

        String sql = "SELECT weight, heart_rate, date_recorded " +
                "FROM HealthMetrics " +
                "WHERE member_id = ? " +
                "ORDER BY date_recorded";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nHEALTH METRICS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        boolean hasResults = false;
        while(rs.next()) {
            hasResults = true;
            System.out.println("Weight: " + rs.getDouble("weight") + " kg");
            System.out.println("Heart Rate: " + rs.getInt("heart_rate") + " bpm");
            System.out.println("Recorded: " + rs.getTimestamp("date_recorded"));
            System.out.printf("");
            System.out.println();
        } if(!hasResults) {
            System.out.println("No health metrics recorded yet.");
            System.out.println("Use 'Log Health Metrics' to add your first entry!");
        }

        rs.close();
        stmt.close();
    }catch (SQLException e){
            System.out.println("DATABASE ERROR: " + e.getMessage());
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