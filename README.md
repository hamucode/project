# Health & Fitness Club Management System
COMP 3005 Final Project - Fall 2025

## Team Members
- [Muhammad Salameh 101295139]
- [Abdurrahman Al Choughri 101274144]

## Project Structure
- `/sql` - Database schema and sample data
- `/src` - Java application source code
- `/docs` - Additional documents, ERM etc

## Setup Instructions (Complete)

### A. Database Setup
1.  **Install PostgreSQL:** Ensure PostgreSQL is installed and running locally (default port 5432).
2.  **Download JDBC Driver:** Download the **PostgreSQL JDBC Driver** (e.g., `postgresql-42.x.x.jar`) and place it in your project's root directory or a dedicated `lib` folder.
3.  **Create Database:** Connect to PostgreSQL and execute the command to create the database:
    ```sql
    CREATE DATABASE fitness_club;
    ```
4.  **Run DDL & DML:** Execute the SQL scripts using `psql` to set up the schema and insert test data:
    ```bash
    psql -d fitness_club -f sql/DDL.sql
    psql -d fitness_club -f sql/DML.sql
    ```
5.  **Configure Connection:** Open `/src/Main.java` and update the `DB_USER` and `DB_PASSWORD` variables to match your local PostgreSQL credentials.

### B. Compilation and Execution
1.  **Compile Code:** Compile all Java source files, making sure to include the JDBC driver in the classpath (`-cp`).
    ```bash
    javac -cp .:path/to/postgresql-42.x.x.jar src/*.java
    ```
    *(Note: Replace `.` with your specific classpath separator if needed.)*
2.  **Run Application:** Execute the main class, including the driver in the classpath:
    ```bash
    java -cp .:path/to/postgresql-42.x.x.jar src/Main
    ```
    
---

##  Test Login Credentials (from DML.sql)

Use the following credentials to quickly log in and test the system's roles:

| Role | Email | Password |
| :--- | :--- | :--- |
| **Member** | `john.doe@email.com` | `password123` |
| **Trainer** | `alex.thompson@gym.com` | `trainer123` |
| **Admin** | `admin@gym.com` | `admin123` |

---

## Implementation Progress
- [x] Database Schema (DDL)
- [x] Sample Data (DML)
- [x] MemberFunctions - User Registration 
- [x] MemberFunctions - Profile Management
- [x] MemberFunctions - Dashboard
- [x] MemberFunctions - PT Scheduling
- [x] MemberFunctions - Log Health Metrics
- [x] TrainerFunctions - Set Availability
- [x] TrainerFunctions - View Schedule
- [x] TrainerFunctions - Member Lookup
- [x] AdminFunctions - Room Booking
- [x] AdminFunctions - Class Management

## Video Demo
[Link to demo video - TBD]
