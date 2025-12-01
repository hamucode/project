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
     * TODO: Define weekly availability windows for trainer
     * - Add availability slot (day_of_week, start_time, end_time)
     * - Check for overlapping slots before inserting
     * - View current availability
     *
     * SQL Operations:
     * - INSERT INTO TrainerAvailability
     * - SELECT to check for overlaps: WHERE NOT (end_time <= ? OR start_time >= ?)
     * - SELECT to display current schedule
     *
     * Test Cases:
     * - Add Monday 9am-5pm availability
     * - Add overlapping slot (e.g., Monday 3pm-7pm when 9am-5pm exists)
     * - Add non-overlapping slot (e.g., Tuesday 9am-5pm)
     */
    public void setAvailability(int trainerId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] Set Availability - Not yet implemented");
    }

    /**
     * FUNCTION 2: SCHEDULE VIEW
     * TODO: View all assigned sessions and classes for trainer
     * - Show upcoming personal training sessions (with member names)
     * - Show group classes assigned
     * - Display room locations and time slots
     *
     * SQL Operations:
     * - SELECT * FROM v_TrainerSchedule WHERE trainer_id = ? AND start_datetime >= NOW()
     * - ORDER BY start_datetime
     *
     * Test Cases:
     * - Display all upcoming sessions/classes
     * - Show empty schedule if no assignments
     */
    public void viewSchedule(int trainerId) {
        // TODO: Implement this function
        System.out.println("\n[TODO] View Schedule - Not yet implemented");
    }


    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper: Check if trainer exists
     */
    private boolean trainerExists(int trainerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Trainers WHERE trainer_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, trainerId);
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
     * Helper: Format time display
     */
    private String formatTime(java.sql.Time time) {
        if (time == null) return "N/A";
        return time.toString().substring(0, 5); // Returns HH:MM format
    }
}