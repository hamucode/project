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
     * Assign rooms for sessions/classes and prevent double-booking.
     */
    public void manageRoomBooking() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- ROOM BOOKING MANAGEMENT ---");
            System.out.println("1. View Room Schedule");
            System.out.println("2. Assign/Reassign Room for Session");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewRoomScheduleFlow();
                    break;
                case 2:
                    reassignSessionRoom();
                    break;
                case 3:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void viewRoomScheduleFlow() {
        try {
            System.out.print("Enter Room ID: ");
            int roomId = getIntInput();
            if (!roomExists(roomId)) {
                System.out.println("ERROR: Room not found.");
                return;
            }

            System.out.print("Enter Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            java.sql.Date.valueOf(dateStr); // Validate format

            displayRoomSchedule(roomId, dateStr);

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid date format.");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    private void reassignSessionRoom() {
        System.out.println("\n--- ASSIGN ROOM TO SESSION ---");
        try {
            System.out.print("Enter Session ID: ");
            int sessionId = getIntInput();

            // Check if session exists
            String sql = "SELECT date, start_time, end_time FROM PersonalSessions WHERE session_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("ERROR: Session not found.");
                rs.close();
                stmt.close();
                return;
            }

            Date date = rs.getDate("date");
            Time startTime = rs.getTime("start_time");
            Time endTime = rs.getTime("end_time");
            rs.close();
            stmt.close();

            System.out.print("Enter New Room ID: ");
            int newRoomId = getIntInput();

            if (!roomExists(newRoomId)) {
                System.out.println("ERROR: Target room does not exist.");
                return;
            }

            // Prevent double-booking
            if (checkRoomConflict(newRoomId, date, startTime, endTime)) {
                System.out.println("ERROR: Target room is already booked at that time!");
                return;
            }

            // Assign Room
            String updateSql = "UPDATE PersonalSessions SET room_id = ? WHERE session_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSql);
            updateStmt.setInt(1, newRoomId);
            updateStmt.setInt(2, sessionId);

            int rows = updateStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("SUCCESS: Room assigned successfully.");
            }
            updateStmt.close();

        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * FUNCTION 2: CLASS SCHEDULE MANAGEMENT
     * Define new classes, assign trainers/rooms/time, update schedules.
     */
    public void manageClassSchedule() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- CLASS MANAGEMENT ---");
            System.out.println("1. Define New Class");
            System.out.println("2. Update Class Schedule");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    defineNewClass();
                    break;
                case 2:
                    updateClassSchedule();
                    break;
                case 3:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Define new classes, assign trainers/rooms/time.
     */
    private void defineNewClass() {
        System.out.println("\n--- DEFINE NEW CLASS ---");
        try {
            System.out.print("Class Name: ");
            String className = scanner.nextLine().trim();

            System.out.print("Schedule Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();

            System.out.print("Start Time (HH:MM): ");
            String timeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Duration (minutes): ");
            int duration = getIntInput();

            System.out.print("Capacity: ");
            int capacity = getIntInput();

            System.out.print("Trainer ID: ");
            int trainerId = getIntInput();

            System.out.print("Room ID: ");
            int roomId = getIntInput();

            // Construct Timestamp
            String timestampStr = dateStr + " " + timeStr;
            Timestamp scheduleTime = Timestamp.valueOf(timestampStr);

            // Calculate End Time for checks
            long durationMillis = duration * 60 * 1000;
            Time sqlStartTime = Time.valueOf(timeStr);
            Time sqlEndTime = new Time(sqlStartTime.getTime() + durationMillis);
            Date sqlDate = Date.valueOf(dateStr);

            // Validate Room & Trainer Availability
            if (!roomExists(roomId)) {
                System.out.println("ERROR: Room not found.");
                return;
            }
            if (checkRoomConflict(roomId, sqlDate, sqlStartTime, sqlEndTime)) {
                System.out.println("ERROR: Room is already booked.");
                return;
            }
            if (!checkTrainerAvailability(trainerId, sqlDate, sqlStartTime, sqlEndTime)) {
                System.out.println("ERROR: Trainer is unavailable.");
                return;
            }

            // Insert Class
            String sql = "INSERT INTO GroupClasses (class_name, schedule_time, duration_minutes, capacity, trainer_id, room_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, className);
            stmt.setTimestamp(2, scheduleTime);
            stmt.setInt(3, duration);
            stmt.setInt(4, capacity);
            stmt.setInt(5, trainerId);
            stmt.setInt(6, roomId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("SUCCESS: Class created successfully.");
            }
            stmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid date/time format.");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    /**
     * Update schedules (assign new trainer/room/time).
     */
    private void updateClassSchedule() {
        System.out.println("\n--- UPDATE CLASS SCHEDULE ---");
        try {
            System.out.print("Enter Class ID to update: ");
            int classId = getIntInput();

            // Check existence
            String checkSql = "SELECT * FROM GroupClasses WHERE class_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, classId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("ERROR: Class not found.");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();

            System.out.print("Enter New Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();

            System.out.print("Enter New Start Time (HH:MM): ");
            String timeStr = scanner.nextLine().trim() + ":00";

            System.out.print("Enter New Duration (minutes): ");
            int duration = getIntInput();

            System.out.print("Enter New Trainer ID: ");
            int trainerId = getIntInput();

            System.out.print("Enter New Room ID: ");
            int roomId = getIntInput();

            // Validations
            String timestampStr = dateStr + " " + timeStr;
            Timestamp scheduleTime = Timestamp.valueOf(timestampStr);
            long durationMillis = duration * 60 * 1000;
            Time sqlStartTime = Time.valueOf(timeStr);
            Time sqlEndTime = new Time(sqlStartTime.getTime() + durationMillis);
            Date sqlDate = Date.valueOf(dateStr);

            if (!roomExists(roomId)) {
                System.out.println("ERROR: Room not found.");
                return;
            }

            if (checkRoomConflict(roomId, sqlDate, sqlStartTime, sqlEndTime)) {
                System.out.println("ERROR: Room is already booked.");
                return;
            }
            if (!checkTrainerAvailability(trainerId, sqlDate, sqlStartTime, sqlEndTime)) {
                System.out.println("ERROR: Trainer is unavailable.");
                return;
            }

            // Update
            String sql = "UPDATE GroupClasses SET schedule_time = ?, duration_minutes = ?, trainer_id = ?, room_id = ? " +
                    "WHERE class_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, scheduleTime);
            stmt.setInt(2, duration);
            stmt.setInt(3, trainerId);
            stmt.setInt(4, roomId);
            stmt.setInt(5, classId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("SUCCESS: Class schedule updated.");
            }
            stmt.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid date/time format.");
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private boolean checkRoomConflict(int roomId, Date date, Time startTime, Time endTime) throws SQLException {
        // Check Personal Sessions
        String sessionSql = "SELECT COUNT(*) FROM PersonalSessions WHERE room_id = ? AND date = ? " +
                "AND status != 'Cancelled' AND NOT (end_time <= ? OR start_time >= ?)";
        PreparedStatement sessionStmt = connection.prepareStatement(sessionSql);
        sessionStmt.setInt(1, roomId);
        sessionStmt.setDate(2, date);
        sessionStmt.setTime(3, startTime);
        sessionStmt.setTime(4, endTime);
        ResultSet sRs = sessionStmt.executeQuery();
        sRs.next();
        boolean sessionConflict = sRs.getInt(1) > 0;
        sRs.close();
        sessionStmt.close();

        if (sessionConflict) return true;

        // Check Group Classes
        String classSql = "SELECT COUNT(*) FROM GroupClasses WHERE room_id = ? " +
                "AND DATE(schedule_time) = ? " +
                "AND NOT ((schedule_time + (duration_minutes || ' minutes')::interval)::time <= ? " +
                "OR schedule_time::time >= ?)";
        PreparedStatement classStmt = connection.prepareStatement(classSql);
        classStmt.setInt(1, roomId);
        classStmt.setDate(2, date);
        classStmt.setTime(3, startTime);
        classStmt.setTime(4, endTime);
        ResultSet cRs = classStmt.executeQuery();
        cRs.next();
        boolean classConflict = cRs.getInt(1) > 0;
        cRs.close();
        classStmt.close();

        return classConflict;
    }

    private boolean checkTrainerAvailability(int trainerId, Date date, Time startTime, Time endTime) throws SQLException {
        // 1. Check Availability Window
        String dayOfWeek = date.toLocalDate().getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

        String availSql = "SELECT COUNT(*) FROM TrainerAvailability WHERE trainer_id = ? " +
                "AND day_of_week = ? AND start_time <= ? AND end_time >= ?";
        PreparedStatement availStmt = connection.prepareStatement(availSql);
        availStmt.setInt(1, trainerId);
        availStmt.setString(2, dayOfWeek);
        availStmt.setTime(3, startTime);
        availStmt.setTime(4, endTime);
        ResultSet rs = availStmt.executeQuery();
        rs.next();
        boolean isAvailable = rs.getInt(1) > 0;
        rs.close();
        availStmt.close();

        if (!isAvailable) return false;

        // 2. Check Personal Session Conflicts
        String sessionSql = "SELECT COUNT(*) FROM PersonalSessions WHERE trainer_id = ? AND date = ? " +
                "AND status != 'Cancelled' AND NOT (end_time <= ? OR start_time >= ?)";
        PreparedStatement sessStmt = connection.prepareStatement(sessionSql);
        sessStmt.setInt(1, trainerId);
        sessStmt.setDate(2, date);
        sessStmt.setTime(3, startTime);
        sessStmt.setTime(4, endTime);
        ResultSet sRs = sessStmt.executeQuery();
        sRs.next();
        boolean hasSessionConflict = sRs.getInt(1) > 0;
        sRs.close();
        sessStmt.close();

        if (hasSessionConflict) return false;

        // 3. Check Group Class Conflicts
        String classSql = "SELECT COUNT(*) FROM GroupClasses WHERE trainer_id = ? AND DATE(schedule_time) = ? " +
                "AND NOT ((schedule_time + (duration_minutes || ' minutes')::interval)::time <= ? " +
                "OR schedule_time::time >= ?)";
        PreparedStatement classStmt = connection.prepareStatement(classSql);
        classStmt.setInt(1, trainerId);
        classStmt.setDate(2, date);
        classStmt.setTime(3, startTime);
        classStmt.setTime(4, endTime);
        ResultSet cRs = classStmt.executeQuery();
        cRs.next();
        boolean hasClassConflict = cRs.getInt(1) > 0;
        cRs.close();
        classStmt.close();

        return !hasClassConflict;
    }

    private boolean roomExists(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE room_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, roomId);
        ResultSet rs = stmt.executeQuery();
        boolean exists = false;
        if (rs.next()) exists = rs.getInt(1) > 0;
        rs.close();
        stmt.close();
        return exists;
    }

    private void displayRoomSchedule(int roomId, String dateStr) throws SQLException {
        String sql = "SELECT type, name, start_t, end_t FROM (" +
                "  SELECT 'Class' as type, class_name as name, schedule_time::time as start_t, " +
                "  (schedule_time + (duration_minutes || ' minutes')::interval)::time as end_t " +
                "  FROM GroupClasses WHERE room_id = ? AND DATE(schedule_time) = ? " +
                "  UNION ALL " +
                "  SELECT 'Session' as type, 'Personal Training' as name, start_time as start_t, " +
                "  end_time as end_t " +
                "  FROM PersonalSessions WHERE room_id = ? AND date = ? AND status != 'Cancelled' " +
                ") as combined_schedule ORDER BY start_t";

        PreparedStatement stmt = connection.prepareStatement(sql);
        java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);
        stmt.setInt(1, roomId);
        stmt.setDate(2, sqlDate);
        stmt.setInt(3, roomId);
        stmt.setDate(4, sqlDate);

        ResultSet rs = stmt.executeQuery();
        System.out.println("\n--- SCHEDULE ---");
        boolean hasRows = false;
        while (rs.next()) {
            hasRows = true;
            System.out.printf("%-10s %-20s %s - %s%n",
                    rs.getString("type"), rs.getString("name"),
                    rs.getTime("start_t"), rs.getTime("end_t"));
        }
        if (!hasRows) System.out.println("No bookings.");
        rs.close();
        stmt.close();
    }

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
}