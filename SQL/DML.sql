-- ============================================================================
-- DML.sql - Health and Fitness Club Management System
-- Sample Data Insertions (Comprehensive Test Data)
-- ============================================================================

-- Clear existing data (optional - use if re-running)
-- TRUNCATE TABLE ClassRegistrations, PersonalSessions, GroupClasses, 
--                TrainerAvailability, HealthMetrics, FitnessGoals, 
--                Members, Trainers, Admins, Rooms RESTART IDENTITY CASCADE;

-- ============================================================================
-- 1. MEMBERS (10 members)
-- ============================================================================
INSERT INTO Members (first_name, last_name, email, password, date_of_birth, gender, join_date) VALUES
('John', 'Doe', 'john.doe@email.com', 'password123', '1990-05-15', 'Male', '2024-01-10'),
('Jane', 'Smith', 'jane.smith@email.com', 'password123', '1985-08-22', 'Female', '2024-02-14'),
('Michael', 'Johnson', 'michael.j@email.com', 'password123', '1992-11-30', 'Male', '2024-03-05'),
('Emily', 'Brown', 'emily.brown@email.com', 'password123', '1988-03-17', 'Female', '2024-03-20'),
('David', 'Wilson', 'david.wilson@email.com', 'password123', '1995-07-08', 'Male', '2024-04-12'),
('Sarah', 'Martinez', 'sarah.m@email.com', 'password123', '1993-09-25', 'Female', '2024-05-01'),
('Chris', 'Anderson', 'chris.a@email.com', 'password123', '1987-12-03', 'Male', '2024-06-10'),
('Ashley', 'Taylor', 'ashley.t@email.com', 'password123', '1991-04-18', 'Female', '2024-07-15'),
('Daniel', 'Thomas', 'daniel.t@email.com', 'password123', '1989-06-29', 'Male', '2024-08-20'),
('Jessica', 'Moore', 'jessica.m@email.com', 'password123', '1994-10-11', 'Female', '2024-09-05');

-- ============================================================================
-- 2. TRAINERS (7 trainers)
-- ============================================================================
INSERT INTO Trainers (first_name, last_name, email, password) VALUES
('Alex', 'Thompson', 'alex.thompson@gym.com', 'trainer123'),
('Lisa', 'Garcia', 'lisa.garcia@gym.com', 'trainer123'),
('Marcus', 'Lee', 'marcus.lee@gym.com', 'trainer123'),
('Rachel', 'Patel', 'rachel.patel@gym.com', 'trainer123'),
('Kevin', 'Davis', 'kevin.davis@gym.com', 'trainer123'),
('Sophia', 'Chen', 'sophia.chen@gym.com', 'trainer123'),
('Brandon', 'Williams', 'brandon.w@gym.com', 'trainer123');

-- ============================================================================
-- 3. ADMINS (5 admins)
-- ============================================================================
INSERT INTO Admins (name, email, password) VALUES
('Admin Master', 'admin@gym.com', 'admin123'),
('Susan Roberts', 'susan.r@gym.com', 'admin123'),
('James Mitchell', 'james.m@gym.com', 'admin123'),
('Patricia White', 'patricia.w@gym.com', 'admin123'),
('Robert Taylor', 'robert.t@gym.com', 'admin123');

-- ============================================================================
-- 4. ROOMS (10 rooms)
-- ============================================================================
INSERT INTO Rooms (room_name, max_capacity) VALUES
('Studio A', 20),
('Studio B', 15),
('Yoga Room', 25),
('Spin Studio', 30),
('Private Training Room 1', 2),
('Private Training Room 2', 2),
('Private Training Room 3', 2),
('Multipurpose Hall', 50),
('Boxing Ring', 10),
('Pilates Studio', 12);

-- ============================================================================
-- 5. GROUP CLASSES (15 classes - variety of times and days)
-- ============================================================================
INSERT INTO GroupClasses (class_name, schedule_time, duration_minutes, capacity, trainer_id, room_id) VALUES
-- Monday classes
('Morning Yoga', '2024-12-02 07:00:00', 60, 25, 1, 3),
('HIIT Bootcamp', '2024-12-02 18:00:00', 45, 20, 2, 1),
('Evening Spin', '2024-12-02 19:00:00', 50, 30, 3, 4),

-- Tuesday classes
('Power Yoga', '2024-12-03 06:30:00', 60, 25, 1, 3),
('Boxing Fundamentals', '2024-12-03 17:00:00', 60, 10, 3, 9),
('Pilates Core', '2024-12-03 19:00:00', 55, 12, 4, 10),

-- Wednesday classes
('Strength Training', '2024-12-04 17:00:00', 55, 20, 5, 1),
('Zumba Dance', '2024-12-04 18:30:00', 50, 25, 2, 3),

-- Thursday classes
('Morning Spin', '2024-12-05 06:00:00', 50, 30, 3, 4),
('CrossFit', '2024-12-05 18:00:00', 60, 20, 5, 8),

-- Friday classes
('Yoga Flow', '2024-12-06 07:00:00', 60, 25, 1, 3),
('Kickboxing', '2024-12-06 18:00:00', 60, 15, 6, 9),

-- Weekend classes
('Saturday HIIT', '2024-12-07 09:00:00', 45, 20, 2, 1),
('Sunday Stretch & Relax', '2024-12-08 10:00:00', 60, 25, 4, 3),
('Sunday Spin', '2024-12-08 17:00:00', 50, 30, 7, 4);

-- ============================================================================
-- 6. PERSONAL SESSIONS (20 sessions - past, present, future)
-- ============================================================================
INSERT INTO PersonalSessions (date, start_time, end_time, status, member_id, trainer_id, room_id) VALUES
-- Completed sessions (past)
('2024-11-20', '09:00:00', '10:00:00', 'Completed', 1, 1, 5),
('2024-11-21', '14:00:00', '15:00:00', 'Completed', 2, 2, 6),
('2024-11-22', '10:00:00', '11:00:00', 'Completed', 3, 3, 5),
('2024-11-23', '16:00:00', '17:00:00', 'Completed', 4, 4, 6),
('2024-11-25', '11:00:00', '12:00:00', 'Completed', 5, 5, 5),

-- Cancelled sessions
('2024-11-26', '08:00:00', '09:00:00', 'Cancelled', 6, 1, 6),
('2024-11-27', '15:00:00', '16:00:00', 'Cancelled', 7, 2, 5),

-- Upcoming booked sessions (future)
('2024-12-02', '09:00:00', '10:00:00', 'Booked', 1, 1, 5),
('2024-12-02', '14:00:00', '15:00:00', 'Booked', 2, 2, 6),
('2024-12-03', '10:00:00', '11:00:00', 'Booked', 3, 3, 7),
('2024-12-03', '16:00:00', '17:00:00', 'Booked', 4, 4, 5),
('2024-12-04', '11:00:00', '12:00:00', 'Booked', 5, 5, 6),
('2024-12-05', '08:00:00', '09:00:00', 'Booked', 6, 1, 7),
('2024-12-05', '15:00:00', '16:00:00', 'Booked', 7, 2, 5),
('2024-12-06', '09:00:00', '10:00:00', 'Booked', 8, 3, 6),
('2024-12-06', '13:00:00', '14:00:00', 'Booked', 9, 4, 7),
('2024-12-07', '10:00:00', '11:00:00', 'Booked', 10, 5, 5),
('2024-12-07', '14:00:00', '15:00:00', 'Booked', 1, 6, 6),
('2024-12-08', '11:00:00', '12:00:00', 'Booked', 2, 7, 7),
('2024-12-09', '09:00:00', '10:00:00', 'Booked', 3, 1, 5);

-- ============================================================================
-- 7. FITNESS GOALS (15 goals - variety of statuses)
-- ============================================================================
INSERT INTO FitnessGoals (member_id, goal_type, target_value, target_date, status) VALUES
-- In Progress goals
(1, 'Weight Loss', 75.00, '2025-03-01', 'In Progress'),
(2, 'Muscle Gain', 65.00, '2025-06-01', 'In Progress'),
(3, 'Body Fat Reduction', 15.00, '2025-04-01', 'In Progress'),
(4, 'Weight Loss', 68.00, '2025-02-15', 'In Progress'),
(5, 'Endurance Building', 80.00, '2025-05-01', 'In Progress'),
(7, 'Muscle Gain', 85.00, '2025-07-01', 'In Progress'),
(8, 'Weight Loss', 72.00, '2025-04-15', 'In Progress'),
(9, 'Strength Gain', 90.00, '2025-06-15', 'In Progress'),
(10, 'Body Fat Reduction', 18.00, '2025-05-20', 'In Progress'),

-- Achieved goals
(6, 'Weight Loss', 70.00, '2024-11-15', 'Achieved'),
(1, 'Body Fat Reduction', 20.00, '2024-10-01', 'Achieved'),

-- Abandoned goals
(3, 'Weight Loss', 75.00, '2024-09-01', 'Abandoned'),
(5, 'Muscle Gain', 85.00, '2024-08-15', 'Abandoned'),

-- Multiple goals for same member
(2, 'Body Fat Reduction', 22.00, '2025-08-01', 'In Progress'),
(4, 'Endurance Building', 75.00, '2025-06-30', 'In Progress');

-- ============================================================================
-- 8. HEALTH METRICS (50+ entries - time series data for tracking)
-- ============================================================================
INSERT INTO HealthMetrics (member_id, date_recorded, weight, heart_rate) VALUES
-- John Doe (member_id = 1) - showing weight loss progress
(1, '2024-01-10 08:00:00', 82.5, 72),
(1, '2024-02-10 08:00:00', 81.3, 71),
(1, '2024-03-10 08:00:00', 80.0, 70),
(1, '2024-04-10 08:00:00', 78.8, 69),
(1, '2024-05-10 08:00:00', 77.5, 68),
(1, '2024-06-10 08:00:00', 76.8, 67),
(1, '2024-07-10 08:00:00', 76.2, 66),

-- Jane Smith (member_id = 2) - showing muscle gain
(2, '2024-02-14 09:00:00', 62.0, 75),
(2, '2024-03-14 09:00:00', 62.5, 74),
(2, '2024-04-14 09:00:00', 63.0, 73),
(2, '2024-05-14 09:00:00', 63.5, 72),
(2, '2024-06-14 09:00:00', 64.0, 71),
(2, '2024-07-14 09:00:00', 64.3, 70),

-- Michael Johnson (member_id = 3)
(3, '2024-03-05 10:00:00', 88.0, 80),
(3, '2024-04-05 10:00:00', 87.0, 79),
(3, '2024-05-05 10:00:00', 86.0, 78),
(3, '2024-06-05 10:00:00', 85.2, 77),
(3, '2024-07-05 10:00:00', 84.5, 76),
(3, '2024-08-05 10:00:00', 84.0, 75),

-- Emily Brown (member_id = 4)
(4, '2024-03-20 11:00:00', 71.5, 68),
(4, '2024-04-20 11:00:00', 71.0, 67),
(4, '2024-05-20 11:00:00', 70.5, 67),
(4, '2024-06-20 11:00:00', 70.0, 66),
(4, '2024-07-20 11:00:00', 69.5, 66),
(4, '2024-08-20 11:00:00', 69.0, 65),

-- David Wilson (member_id = 5)
(5, '2024-04-12 07:30:00', 75.0, 70),
(5, '2024-05-12 07:30:00', 75.8, 69),
(5, '2024-06-12 07:30:00', 76.5, 69),
(5, '2024-07-12 07:30:00', 77.0, 68),
(5, '2024-08-12 07:30:00', 77.5, 68),
(5, '2024-09-12 07:30:00', 78.0, 67),

-- Sarah Martinez (member_id = 6) - achieved goal
(6, '2024-05-01 08:00:00', 74.0, 72),
(6, '2024-06-01 08:00:00', 73.0, 71),
(6, '2024-07-01 08:00:00', 72.0, 70),
(6, '2024-08-01 08:00:00', 71.0, 69),
(6, '2024-09-01 08:00:00', 70.5, 68),
(6, '2024-10-01 08:00:00', 70.0, 67),

-- Chris Anderson (member_id = 7)
(7, '2024-06-10 09:00:00', 82.0, 75),
(7, '2024-07-10 09:00:00', 83.0, 74),
(7, '2024-08-10 09:00:00', 83.8, 73),
(7, '2024-09-10 09:00:00', 84.5, 72),

-- Ashley Taylor (member_id = 8)
(8, '2024-07-15 10:00:00', 68.0, 70),
(8, '2024-08-15 10:00:00', 67.5, 69),
(8, '2024-09-15 10:00:00', 67.0, 68),

-- Daniel Thomas (member_id = 9)
(9, '2024-08-20 11:00:00', 85.0, 76),
(9, '2024-09-20 11:00:00', 86.0, 75),
(9, '2024-10-20 11:00:00', 87.0, 74),

-- Jessica Moore (member_id = 10)
(10, '2024-09-05 08:30:00', 65.0, 72),
(10, '2024-10-05 08:30:00', 64.5, 71),
(10, '2024-11-05 08:30:00', 64.0, 70);

-- ============================================================================
-- 9. TRAINER AVAILABILITY (30+ slots)
-- ============================================================================
INSERT INTO TrainerAvailability (trainer_id, day_of_week, start_time, end_time) VALUES
-- Alex Thompson (trainer_id = 1)
(1, 'Monday', '06:00:00', '14:00:00'),
(1, 'Wednesday', '06:00:00', '14:00:00'),
(1, 'Friday', '06:00:00', '14:00:00'),
(1, 'Saturday', '08:00:00', '12:00:00'),

-- Lisa Garcia (trainer_id = 2)
(2, 'Monday', '14:00:00', '22:00:00'),
(2, 'Tuesday', '14:00:00', '22:00:00'),
(2, 'Thursday', '14:00:00', '22:00:00'),
(2, 'Friday', '14:00:00', '22:00:00'),

-- Marcus Lee (trainer_id = 3)
(3, 'Tuesday', '08:00:00', '16:00:00'),
(3, 'Thursday', '08:00:00', '16:00:00'),
(3, 'Saturday', '08:00:00', '16:00:00'),
(3, 'Sunday', '10:00:00', '18:00:00'),

-- Rachel Patel (trainer_id = 4)
(4, 'Monday', '16:00:00', '21:00:00'),
(4, 'Wednesday', '16:00:00', '21:00:00'),
(4, 'Friday', '16:00:00', '21:00:00'),
(4, 'Sunday', '09:00:00', '14:00:00'),

-- Kevin Davis (trainer_id = 5)
(5, 'Tuesday', '10:00:00', '18:00:00'),
(5, 'Thursday', '10:00:00', '18:00:00'),
(5, 'Saturday', '09:00:00', '17:00:00'),
(5, 'Sunday', '10:00:00', '18:00:00'),

-- Sophia Chen (trainer_id = 6)
(6, 'Monday', '07:00:00', '15:00:00'),
(6, 'Wednesday', '07:00:00', '15:00:00'),
(6, 'Friday', '07:00:00', '15:00:00'),

-- Brandon Williams (trainer_id = 7)
(7, 'Tuesday', '12:00:00', '20:00:00'),
(7, 'Thursday', '12:00:00', '20:00:00'),
(7, 'Sunday', '11:00:00', '19:00:00');

-- ============================================================================
-- 10. CLASS REGISTRATIONS (30+ registrations)
-- ============================================================================
INSERT INTO ClassRegistrations (member_id, class_id, registration_date) VALUES
-- Morning Yoga (class_id = 1) - Popular class
(1, 1, '2024-11-25 10:00:00'),
(2, 1, '2024-11-25 10:05:00'),
(6, 1, '2024-11-25 11:00:00'),
(8, 1, '2024-11-26 09:00:00'),
(10, 1, '2024-11-26 14:00:00'),

-- HIIT Bootcamp (class_id = 2)
(2, 2, '2024-11-26 14:00:00'),
(4, 2, '2024-11-26 15:00:00'),
(7, 2, '2024-11-27 10:00:00'),

-- Evening Spin (class_id = 3)
(1, 3, '2024-11-25 10:05:00'),
(3, 3, '2024-11-27 09:00:00'),
(5, 3, '2024-11-28 11:00:00'),
(9, 3, '2024-11-28 16:00:00'),

-- Power Yoga (class_id = 4)
(2, 4, '2024-11-26 14:10:00'),
(6, 4, '2024-11-28 12:00:00'),
(8, 4, '2024-11-28 13:00:00'),

-- Boxing Fundamentals (class_id = 5)
(3, 5, '2024-11-27 09:05:00'),
(7, 5, '2024-11-29 13:00:00'),
(9, 5, '2024-11-29 14:00:00'),

-- Pilates Core (class_id = 6)
(4, 6, '2024-11-27 15:00:00'),
(8, 6, '2024-11-28 13:05:00'),
(10, 6, '2024-11-29 10:00:00'),

-- Strength Training (class_id = 7)
(5, 7, '2024-11-28 11:05:00'),
(7, 7, '2024-11-29 13:05:00'),

-- Zumba Dance (class_id = 8)
(2, 8, '2024-11-26 14:05:00'),
(6, 8, '2024-11-28 12:05:00'),

-- Morning Spin (class_id = 9)
(1, 9, '2024-11-25 10:10:00'),
(5, 9, '2024-11-28 11:10:00'),

-- CrossFit (class_id = 10)
(3, 10, '2024-11-27 09:10:00'),
(9, 10, '2024-11-29 14:05:00'),

-- Yoga Flow (class_id = 11)
(2, 11, '2024-11-26 14:15:00'),

-- Kickboxing (class_id = 12)
(7, 12, '2024-11-29 13:10:00'),

-- Saturday HIIT (class_id = 13)
(4, 13, '2024-11-27 15:05:00'),

-- Sunday Stretch & Relax (class_id = 14)
(6, 14, '2024-11-28 12:10:00'),

-- Sunday Spin (class_id = 15)
(10, 15, '2024-11-29 10:05:00');

-- ============================================================================
-- VERIFICATION QUERIES (Optional - uncomment to check data)
-- ============================================================================

-- SELECT COUNT(*) as member_count FROM Members;
-- SELECT COUNT(*) as trainer_count FROM Trainers;
-- SELECT COUNT(*) as admin_count FROM Admins;
-- SELECT COUNT(*) as room_count FROM Rooms;
-- SELECT COUNT(*) as class_count FROM GroupClasses;
-- SELECT COUNT(*) as session_count FROM PersonalSessions;
-- SELECT COUNT(*) as goal_count FROM FitnessGoals;
-- SELECT COUNT(*) as metric_count FROM HealthMetrics;
-- SELECT COUNT(*) as availability_count FROM TrainerAvailability;
-- SELECT COUNT(*) as registration_count FROM ClassRegistrations;

-- ============================================================================
-- END OF DML.sql
-- ============================================================================