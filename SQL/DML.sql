-- ============================================================================
-- DML.sql - Health and Fitness Club Management System
-- Sample Data Insertions (Minimum 5 rows per table)
-- ============================================================================

-- 1. Insert Members (5+ rows)
INSERT INTO Members (first_name, last_name, email, password, date_of_birth, gender, join_date) VALUES
('John', 'Doe', 'john.doe@email.com', 'hashed_password_1', '1990-05-15', 'Male', '2024-01-10'),
('Jane', 'Smith', 'jane.smith@email.com', 'hashed_password_2', '1985-08-22', 'Female', '2024-02-14'),
('Michael', 'Johnson', 'michael.j@email.com', 'hashed_password_3', '1992-11-30', 'Male', '2024-03-05'),
('Emily', 'Brown', 'emily.brown@email.com', 'hashed_password_4', '1988-03-17', 'Female', '2024-03-20'),
('David', 'Wilson', 'david.wilson@email.com', 'hashed_password_5', '1995-07-08', 'Male', '2024-04-12'),
('Sarah', 'Martinez', 'sarah.m@email.com', 'hashed_password_6', '1993-09-25', 'Female', '2024-05-01'),
('Chris', 'Anderson', 'chris.a@email.com', 'hashed_password_7', '1987-12-03', 'Male', '2024-06-10');

-- 2. Insert Trainers (5+ rows)
INSERT INTO Trainers (first_name, last_name, email, password) VALUES
('Alex', 'Thompson', 'alex.thompson@gym.com', 'trainer_pass_1'),
('Lisa', 'Garcia', 'lisa.garcia@gym.com', 'trainer_pass_2'),
('Marcus', 'Lee', 'marcus.lee@gym.com', 'trainer_pass_3'),
('Rachel', 'Patel', 'rachel.patel@gym.com', 'trainer_pass_4'),
('Kevin', 'Davis', 'kevin.davis@gym.com', 'trainer_pass_5');

-- 3. Insert Admins (5+ rows)
INSERT INTO Admins (name, email, password) VALUES
('Admin Master', 'admin@gym.com', 'admin_pass_1'),
('Susan Roberts', 'susan.r@gym.com', 'admin_pass_2'),
('James Mitchell', 'james.m@gym.com', 'admin_pass_3'),
('Patricia White', 'patricia.w@gym.com', 'admin_pass_4'),
('Robert Taylor', 'robert.t@gym.com', 'admin_pass_5');

-- 4. Insert Rooms (5+ rows)
INSERT INTO Rooms (room_name, max_capacity) VALUES
('Studio A', 20),
('Studio B', 15),
('Yoga Room', 25),
('Spin Studio', 30),
('Private Training Room 1', 2),
('Private Training Room 2', 2),
('Multipurpose Hall', 50);

-- 5. Insert GroupClasses (5+ rows)
INSERT INTO GroupClasses (class_name, schedule_time, duration_minutes, capacity, trainer_id, room_id) VALUES
('Morning Yoga', '2024-12-02 07:00:00', 60, 25, 1, 3),
('HIIT Bootcamp', '2024-12-02 18:00:00', 45, 20, 2, 1),
('Spin Class', '2024-12-03 06:30:00', 50, 30, 3, 4),
('Evening Pilates', '2024-12-03 19:00:00', 60, 15, 4, 2),
('Strength Training', '2024-12-04 17:00:00', 55, 20, 5, 1),
('Zumba Dance', '2024-12-05 18:30:00', 50, 25, 2, 3),
('Boxing Fundamentals', '2024-12-06 17:00:00', 60, 15, 3, 2);

-- 6. Insert PersonalSessions (5+ rows)
INSERT INTO PersonalSessions (date, start_time, end_time, status, member_id, trainer_id, room_id) VALUES
('2024-12-02', '09:00:00', '10:00:00', 'Booked', 1, 1, 5),
('2024-12-02', '14:00:00', '15:00:00', 'Booked', 2, 2, 6),
('2024-12-03', '10:00:00', '11:00:00', 'Completed', 3, 3, 5),
('2024-12-03', '16:00:00', '17:00:00', 'Booked', 4, 4, 6),
('2024-12-04', '11:00:00', '12:00:00', 'Booked', 5, 5, 5),
('2024-12-05', '08:00:00', '09:00:00', 'Cancelled', 6, 1, 6),
('2024-12-06', '15:00:00', '16:00:00', 'Booked', 7, 2, 5);

-- 7. Insert FitnessGoals (5+ rows)
INSERT INTO FitnessGoals (member_id, goal_type, target_value, target_date, status) VALUES
(1, 'Weight Loss', 75.00, '2025-03-01', 'In Progress'),
(2, 'Muscle Gain', 65.00, '2025-06-01', 'In Progress'),
(3, 'Body Fat Reduction', 15.00, '2025-04-01', 'In Progress'),
(4, 'Weight Loss', 68.00, '2025-02-15', 'In Progress'),
(5, 'Endurance Building', 80.00, '2025-05-01', 'In Progress'),
(6, 'Weight Loss', 70.00, '2025-03-15', 'Achieved'),
(7, 'Muscle Gain', 85.00, '2025-07-01', 'In Progress');

-- 8. Insert HealthMetrics (5+ rows per member for tracking)
INSERT INTO HealthMetrics (member_id, date_recorded, weight, heart_rate) VALUES
-- John Doe (member_id = 1)
(1, '2024-01-10 08:00:00', 82.5, 72),
(1, '2024-02-10 08:00:00', 80.3, 70),
(1, '2024-03-10 08:00:00', 78.8, 68),
(1, '2024-04-10 08:00:00', 77.2, 67),
(1, '2024-05-10 08:00:00', 76.0, 65),
-- Jane Smith (member_id = 2)
(2, '2024-02-14 09:00:00', 62.0, 75),
(2, '2024-03-14 09:00:00', 62.8, 74),
(2, '2024-04-14 09:00:00', 63.5, 73),
(2, '2024-05-14 09:00:00', 64.0, 72),
(2, '2024-06-14 09:00:00', 64.5, 71),
-- Michael Johnson (member_id = 3)
(3, '2024-03-05 10:00:00', 88.0, 80),
(3, '2024-04-05 10:00:00', 86.5, 78),
(3, '2024-05-05 10:00:00', 85.0, 76),
(3, '2024-06-05 10:00:00', 84.0, 75),
(3, '2024-07-05 10:00:00', 83.2, 74),
-- Emily Brown (member_id = 4)
(4, '2024-03-20 11:00:00', 71.5, 68),
(4, '2024-04-20 11:00:00', 70.8, 67),
(4, '2024-05-20 11:00:00', 70.0, 66),
(4, '2024-06-20 11:00:00', 69.2, 65),
(4, '2024-07-20 11:00:00', 68.5, 64),
-- David Wilson (member_id = 5)
(5, '2024-04-12 07:30:00', 75.0, 70),
(5, '2024-05-12 07:30:00', 75.8, 69),
(5, '2024-06-12 07:30:00', 76.5, 68),
(5, '2024-07-12 07:30:00', 77.2, 67),
(5, '2024-08-12 07:30:00', 78.0, 66);

-- 9. Insert TrainerAvailability (5+ rows)
INSERT INTO TrainerAvailability (trainer_id, day_of_week, start_time, end_time) VALUES
-- Alex Thompson availability
(1, 'Monday', '06:00:00', '14:00:00'),
(1, 'Wednesday', '06:00:00', '14:00:00'),
(1, 'Friday', '06:00:00', '14:00:00'),
-- Lisa Garcia availability
(2, 'Monday', '14:00:00', '22:00:00'),
(2, 'Tuesday', '14:00:00', '22:00:00'),
(2, 'Thursday', '14:00:00', '22:00:00'),
-- Marcus Lee availability
(3, 'Tuesday', '08:00:00', '16:00:00'),
(3, 'Thursday', '08:00:00', '16:00:00'),
(3, 'Saturday', '08:00:00', '16:00:00'),
-- Rachel Patel availability
(4, 'Monday', '16:00:00', '21:00:00'),
(4, 'Wednesday', '16:00:00', '21:00:00'),
(4, 'Friday', '16:00:00', '21:00:00'),
-- Kevin Davis availability
(5, 'Tuesday', '10:00:00', '18:00:00'),
(5, 'Thursday', '10:00:00', '18:00:00'),
(5, 'Sunday', '10:00:00', '18:00:00');

-- 10. Insert ClassRegistrations (5+ rows)
INSERT INTO ClassRegistrations (member_id, class_id, registration_date) VALUES
(1, 1, '2024-11-25 10:00:00'),  -- John in Morning Yoga
(1, 3, '2024-11-25 10:05:00'),  -- John in Spin Class
(2, 2, '2024-11-26 14:00:00'),  -- Jane in HIIT Bootcamp
(2, 6, '2024-11-26 14:05:00'),  -- Jane in Zumba Dance
(3, 5, '2024-11-27 09:00:00'),  -- Michael in Strength Training
(4, 4, '2024-11-27 15:00:00'),  -- Emily in Evening Pilates
(5, 3, '2024-11-28 11:00:00'),  -- David in Spin Class
(6, 1, '2024-11-28 12:00:00'),  -- Sarah in Morning Yoga
(7, 7, '2024-11-29 13:00:00');  -- Chris in Boxing Fundamentals

-- ============================================================================
-- END OF DML.sql
-- ============================================================================