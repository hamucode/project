-- ============================================================================
-- DDL.sql - Health and Fitness Club Management System
-- Database Schema Definition
-- ============================================================================

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS ClassRegistrations CASCADE;
DROP TABLE IF EXISTS PersonalSessions CASCADE;
DROP TABLE IF EXISTS GroupClasses CASCADE;
DROP TABLE IF EXISTS TrainerAvailability CASCADE;
DROP TABLE IF EXISTS HealthMetrics CASCADE;
DROP TABLE IF EXISTS FitnessGoals CASCADE;
DROP TABLE IF EXISTS Members CASCADE;
DROP TABLE IF EXISTS Trainers CASCADE;
DROP TABLE IF EXISTS Admins CASCADE;
DROP TABLE IF EXISTS Rooms CASCADE;

-- ============================================================================
-- 1. Strong Entities
-- ============================================================================

CREATE TABLE Members (
    member_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    join_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE Trainers (
    trainer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Admins (
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Rooms (
    room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(50) NOT NULL,
    max_capacity INT CHECK (max_capacity > 0)
);

-- ============================================================================
-- 2. Operational Tables (Classes & Sessions)
-- ============================================================================

CREATE TABLE GroupClasses (
    class_id SERIAL PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    schedule_time TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    capacity INT NOT NULL CHECK (capacity > 0),
    trainer_id INT NOT NULL REFERENCES Trainers(trainer_id),
    room_id INT NOT NULL REFERENCES Rooms(room_id)
);

CREATE TABLE PersonalSessions (
    session_id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'Booked' CHECK (status IN ('Booked', 'Completed', 'Cancelled')),
    member_id INT NOT NULL REFERENCES Members(member_id),
    trainer_id INT NOT NULL REFERENCES Trainers(trainer_id),
    room_id INT NOT NULL REFERENCES Rooms(room_id),
    CONSTRAINT valid_time_range CHECK (end_time > start_time)
);

-- ============================================================================
-- 3. Weak Entities & Histories
-- ============================================================================

CREATE TABLE FitnessGoals (
    goal_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Members(member_id) ON DELETE CASCADE,
    goal_type VARCHAR(50) NOT NULL,
    target_value NUMERIC(5,2) NOT NULL,
    target_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'In Progress' CHECK (status IN ('In Progress', 'Achieved', 'Abandoned'))
);

CREATE TABLE HealthMetrics (
    metric_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Members(member_id) ON DELETE CASCADE,
    date_recorded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    weight NUMERIC(5,2),
    heart_rate INT,
    CONSTRAINT positive_metrics CHECK (weight > 0 AND heart_rate > 0)
);

CREATE TABLE TrainerAvailability (
    avail_id SERIAL PRIMARY KEY,
    trainer_id INT NOT NULL REFERENCES Trainers(trainer_id) ON DELETE CASCADE,
    day_of_week VARCHAR(15) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

-- ============================================================================
-- 4. Many-to-Many Relationships
-- ============================================================================

CREATE TABLE ClassRegistrations (
    registration_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL REFERENCES Members(member_id),
    class_id INT NOT NULL REFERENCES GroupClasses(class_id),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(member_id, class_id)
);

-- ============================================================================
-- 5. Views
-- ============================================================================

-- View: Member Schedule (combining personal sessions and group classes)
CREATE VIEW v_MemberSchedule AS
SELECT 
    m.member_id,
    'Group Class' AS type,
    gc.class_name AS activity,
    gc.schedule_time AS start_datetime,
    (gc.schedule_time + (gc.duration_minutes || ' minutes')::interval) AS end_datetime,
    r.room_name,
    t.first_name || ' ' || t.last_name AS trainer_name
FROM ClassRegistrations cr
JOIN GroupClasses gc ON cr.class_id = gc.class_id
JOIN Rooms r ON gc.room_id = r.room_id
JOIN Trainers t ON gc.trainer_id = t.trainer_id
JOIN Members m ON cr.member_id = m.member_id
UNION ALL
SELECT 
    ps.member_id,
    'Personal Session' AS type,
    '1-on-1 Training' AS activity,
    (ps.date + ps.start_time) AS start_datetime,
    (ps.date + ps.end_time) AS end_datetime,
    r.room_name,
    t.first_name || ' ' || t.last_name AS trainer_name
FROM PersonalSessions ps
JOIN Rooms r ON ps.room_id = r.room_id
JOIN Trainers t ON ps.trainer_id = t.trainer_id;

-- View: Trainer Schedule (showing all assigned sessions and classes)
CREATE VIEW v_TrainerSchedule AS
SELECT 
    t.trainer_id,
    'Group Class' AS type,
    gc.class_name AS activity,
    gc.schedule_time AS start_datetime,
    r.room_name
FROM GroupClasses gc
JOIN Trainers t ON gc.trainer_id = t.trainer_id
JOIN Rooms r ON gc.room_id = r.room_id
UNION ALL
SELECT 
    t.trainer_id,
    'Personal Session' AS type,
    'Client: ' || m.first_name || ' ' || m.last_name AS activity,
    (ps.date + ps.start_time) AS start_datetime,
    r.room_name
FROM PersonalSessions ps
JOIN Trainers t ON ps.trainer_id = t.trainer_id
JOIN Rooms r ON ps.room_id = r.room_id
JOIN Members m ON ps.member_id = m.member_id;

-- ============================================================================
-- 6. Functions
-- ============================================================================

-- Function: Check class capacity before registration
CREATE OR REPLACE FUNCTION check_class_capacity()
RETURNS TRIGGER AS $$
DECLARE
    current_count INT;
    max_cap INT;
BEGIN
    -- Get current registration count for the class
    SELECT COUNT(*) INTO current_count 
    FROM ClassRegistrations 
    WHERE class_id = NEW.class_id;
    
    -- Get the max capacity of the class
    SELECT capacity INTO max_cap 
    FROM GroupClasses 
    WHERE class_id = NEW.class_id;
    
    -- Validation
    IF current_count >= max_cap THEN
        RAISE EXCEPTION 'Registration failed: Class is fully booked.';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 7. Triggers
-- ============================================================================

-- Trigger: Prevent overbooking a class's capacity
CREATE TRIGGER trg_check_class_capacity
BEFORE INSERT ON ClassRegistrations
FOR EACH ROW
EXECUTE FUNCTION check_class_capacity();

-- ============================================================================
-- 8. Indexes (for performance optimization)
-- ============================================================================

-- Index: Speed up health metrics queries by member and date
CREATE INDEX idx_health_metrics_member_date 
ON HealthMetrics(member_id, date_recorded DESC);

-- Index: Speed up personal session lookups by date and trainer
CREATE INDEX idx_personal_sessions_date_trainer 
ON PersonalSessions(date, trainer_id);

-- Index: Speed up class registration queries
CREATE INDEX idx_class_registrations_member 
ON ClassRegistrations(member_id);

-- Index: Speed up trainer availability lookups
CREATE INDEX idx_trainer_availability_trainer 
ON TrainerAvailability(trainer_id, day_of_week);

-- speed up member look up since it is the largest table as there are more members than trainers for example
CREATE INDEX idx_member_name_search 
ON Member (LOWER(LastName), LOWER(FirstName));

-- ============================================================================
-- END OF DDL.sql
-- ============================================================================
