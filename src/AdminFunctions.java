import java.sql.*;
import java.util.Scanner;

public class AdminFunctions {

    private Connection connection;
    private Scanner scanner;

    public AdminFunctions(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    /**
     * FUNCTION 1: ROOM BOOKING MANAGEMENT
     * TODO: Assign rooms for sessions/classes and prevent double-booking
     * - Check room availability for a given date/time
     * - Assign room to personal session
     * - View all bookings for a specific room
     * - Prevent overlapping bookings
     *
     * SQL Operations:
     * - SELECT FROM PersonalSessions WHERE room_id = ? AND date = ? 
     *   AND NOT (end_time <= ? OR start_time >= ?)
     * - SELECT FROM GroupClasses WHERE room_id = ? (check time conflicts)
     * - UPDATE PersonalSessions SET room_id = ? WHERE session_id = ?
     * - Display all bookings (UNION of PersonalSessions and GroupClasses)
     *
     * Test Cases:
     * - Assign available room to session
     * - Prevent double-booking conflict
     * - View complete room schedule for a day
     */
    public void manageRoomBooking() {
        // TODO: Implement this function
        System.out.println("\n[TODO] Room Booking Management - Not yet implemented");
    }

    /**
     * FUNCTION 2: CLASS SCHEDULE MANAGEMENT
     * TODO: Create, update, or cancel group fitness classes
     * - Create new class (validate trainer availability and room)
     * - Update class schedule (time, trainer, room)
     * - Cancel/delete class
     * - View all classes with enrollment count
     *
     * SQL Operations:
     * - INSERT INTO GroupClasses (class_name, schedule_time, duration_minutes, capacity, trainer_id, room_id)
     * - UPDATE GroupClasses SET schedule_time = ?, trainer_id = ?, room_id = ? WHERE class_id = ?
     * - DELETE FROM GroupClasses WHERE class_id = ?
     * - SELECT with JOIN to Trainers, Rooms, and COUNT from ClassRegistrations
     *
     * Test Cases:
     * - Create new "Yoga" class
     * - Update class time
     * - Assign trainer who's unavailable at that time
     * - Cancel class (delete)
     * - View all classes with enrollment numbers
     */
    public void manageClassSchedule() {
        // TODO: Implement this function
        System.out.println("\n[TODO] Class Schedule Management - Not yet implemented");
    }





    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper: Check if admin exists
     */
    private boolean adminExists(int adminId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Admins WHERE admin_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, adminId);
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
     * Helper: Check if room exists
     */
    private boolean roomExists(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE room_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, roomId);
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
     * Helper: Display room schedule for a specific date
     */
    private void displayRoomSchedule(int roomId, String date) throws SQLException {
        // TODO: Helper to show all bookings for a room on a specific date
        System.out.println("\n[TODO] Display room schedule helper - Not yet implemented");
    }
}