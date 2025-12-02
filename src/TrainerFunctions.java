import java.sql.*;
import java.util.Scanner;

public class TrainerFunctions {

    private Connection connection;
    private Scanner scanner;

    public TrainerFunctions(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    /**
     * FUNCTION 1: SET AVAILABILITY
     * Define weekly availability windows for trainer.
     * Prevents overlapping time slots to ensure data accuracy.
     */
    public void setAvailability(int trainerId) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- SET AVAILABILITY ---");
            System.out.println("1. Add New Availability Slot");
            System.out.println("2. View Current Availability");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addAvailability(trainerId);
                    break;
                case 2:
                    viewAvailability(trainerId);
                    break;
                case 3:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addAvailability(int trainerId) {
        try {
            System.out.println("\nSelect Day of Week:");
            System.out.println("1. Monday");
            System.out.println("2. Tuesday");
            System.out.println("3. Wednesday");
            System.out.println("4. Thursday");
            System.out.println("5. Friday");
            System.out.println("6. Saturday");
            System.out.println("7. Sunday");
            System.out.print("Enter choice (1-7): ");

            int dayChoice = getIntInput();
            String dayOfWeek = getDayString(dayChoice);

            if (dayOfWeek == null) {
                System.out.println("Invalid day selection!");
                return;
            }

            System.out.print("Enter start time (HH:MM): ");
            String startTimeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Enter end time (HH:MM): ");
            String endTimeStr = scanner.nextLine().trim() + ":00";

            java.sql.Time startTime = java.sql.Time.valueOf(startTimeStr);
            java.sql.Time endTime = java.sql.Time.valueOf(endTimeStr);

            // Validation: End time after start time
            if (!endTime.after(startTime)) {
                System.out.println("ERROR: End time must be after start time!");
                return;
            }

            // Check for overlap [cite: 18, 48]
            // Overlap logic: (StartA < EndB) AND (EndA > StartB)
            String checkSql = "SELECT COUNT(*) FROM TrainerAvailability " +
                    "WHERE trainer_id = ? AND day_of_week = ? " +
                    "AND start_time < ? AND end_time > ?";

            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, trainerId);
            checkStmt.setString(2, dayOfWeek);
            checkStmt.setTime(3, endTime);   // Existing Start < New End
            checkStmt.setTime(4, startTime); // Existing End > New Start

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("ERROR: This slot overlaps with an existing availability!");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();

            // Insert new slot
            String sql = "INSERT INTO TrainerAvailability (trainer_id, day_of_week, start_time, end_time) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, trainerId);
            stmt.setString(2, dayOfWeek);
            stmt.setTime(3, startTime);
            stmt.setTime(4, endTime);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Success! Availability added for " + dayOfWeek +
                        " (" + formatTime(startTime) + " - " + formatTime(endTime) + ")");
            }
            stmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid time format! Use HH:MM");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    private void viewAvailability(int trainerId) {
        try {
            // Order by Day of Week (Monday=1...Sunday=7) and then Start Time
            String sql = "SELECT day_of_week, start_time, end_time FROM TrainerAvailability " +
                    "WHERE trainer_id = ? " +
                    "ORDER BY CASE day_of_week " +
                    "WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3 " +
                    "WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 " +
                    "WHEN 'Sunday' THEN 7 END, start_time";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- CURRENT AVAILABILITY ---");
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.println(rs.getString("day_of_week") + ": " +
                        formatTime(rs.getTime("start_time")) + " - " +
                        formatTime(rs.getTime("end_time")));
            }
            if (!hasRows) System.out.println("No availability set.");
            System.out.println("----------------------------");

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * FUNCTION 2: SCHEDULE VIEW
     * View all assigned sessions and classes for the trainer.
     */
    public void viewSchedule(int trainerId) {
        System.out.println("\n--- MY SCHEDULE ---");
        try {
            // Uses the view v_TrainerSchedule to combine Personal Sessions and Group Classes
            String sql = "SELECT type, activity, start_datetime, room_name " +
                    "FROM v_TrainerSchedule " +
                    "WHERE trainer_id = ? AND start_datetime >= CURRENT_DATE " +
                    "ORDER BY start_datetime";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            boolean hasRows = false;
            System.out.println("Upcoming Sessions & Classes:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            while (rs.next()) {
                hasRows = true;
                System.out.println("Type:     " + rs.getString("type"));
                System.out.println("Activity: " + rs.getString("activity"));
                System.out.println("Time:     " + rs.getTimestamp("start_datetime"));
                System.out.println("Room:     " + rs.getString("room_name"));
                System.out.println("----------------------------------------");
            }

            if (!hasRows) {
                System.out.println("No upcoming sessions or classes found.");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * FUNCTION 3: MEMBER PROFILE LOOKUP (Read-Only)
     * Search for a member and view their profile data (Goals, Metrics).
     * Strictly read-only access[cite: 20, 22, 50].
     */
    public void searchMember(int trainerId) {
        System.out.println("\n--- MEMBER LOOKUP ---");
        System.out.print("Enter member's name (first or last): ");
        String searchName = scanner.nextLine().trim();

        try {
            // Step 1: Search for members
            String sql = "SELECT member_id, first_name, last_name, email FROM Members " +
                    "WHERE first_name ILIKE ? OR last_name ILIKE ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + searchName + "%");
            stmt.setString(2, "%" + searchName + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            System.out.println("\nSearch Results:");
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("member_id") +
                        " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                        " | Email: " + rs.getString("email"));
                System.out.println();
            }
            rs.close();
            stmt.close();

            if (!found) {
                System.out.println("No members found with that name.");
                return;
            }

            // Step 2: Select a member to view details
            System.out.print("\nEnter Member ID to view details (or 0 to cancel): ");
            int memberId = getIntInput();

            if (memberId == 0) return;

            viewMemberDetails(memberId);

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Helper to view member details (Goals and latest metrics)
     */
    private void viewMemberDetails(int memberId) {
        try {
            // View Active Goals
            System.out.println("\n--- FITNESS GOALS ---");
            String goalSql = "SELECT goal_type, target_value, status FROM FitnessGoals " +
                    "WHERE member_id = ? AND status = 'In Progress'";
            PreparedStatement goalStmt = connection.prepareStatement(goalSql);
            goalStmt.setInt(1, memberId);
            ResultSet goalRs = goalStmt.executeQuery();

            boolean hasGoals = false;
            while (goalRs.next()) {
                hasGoals = true;
                System.out.println("- " + goalRs.getString("goal_type") +
                        ": Target " + goalRs.getDouble("target_value"));
            }
            if (!hasGoals) System.out.println("No active goals.");
            goalStmt.close();

            // View Latest Health Metric
            System.out.println("\n--- LATEST HEALTH METRICS ---");
            String metricSql = "SELECT weight, heart_rate, date_recorded FROM HealthMetrics " +
                    "WHERE member_id = ? ORDER BY date_recorded DESC LIMIT 1";
            PreparedStatement metricStmt = connection.prepareStatement(metricSql);
            metricStmt.setInt(1, memberId);
            ResultSet metricRs = metricStmt.executeQuery();

            if (metricRs.next()) {
                System.out.println("Weight: " + metricRs.getDouble("weight") + " kg");
                System.out.println("Heart Rate: " + metricRs.getInt("heart_rate") + " bpm");
                System.out.println("Recorded: " + metricRs.getTimestamp("date_recorded"));
            } else {
                System.out.println("No health metrics recorded.");
            }
            metricStmt.close();

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper: Get integer input with error handling
     */
    private int getIntInput() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) return 0;
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }
        }
    }

    /**
     * Helper: Map integer choice to day string
     */
    private String getDayString(int choice) {
        switch (choice) {
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            case 7: return "Sunday";
            default: return null;
        }
    }

    /**
     * Helper: Format time display
     */
    private String formatTime(java.sql.Time time) {
        if (time == null) return "N/A";
        return time.toString().substring(0, 5); // Returns HH:MM format
    }
}