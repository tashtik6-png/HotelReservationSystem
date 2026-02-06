Hotel "Astana" Reservation System
Developer: Margulan Zhamantayev

Course: ST-2504 Object-Oriented Programming, Astana IT University

Instructor: Mr. Imran Khaider

Date: February 2026

Project Description
The Hotel Reservation System is a console-based Java application designed to simulate professional hotel operations. This project demonstrates advanced Object-Oriented Programming (OOP) principles, including Encapsulation, Composition, and Transactional Integrity, integrated with a MySQL database.

MVP Features
Room Management: Admin capabilities to add rooms and define types.

Guest Registration: System to capture and store unique guest profiles.

Booking Engine: Real-time reservation creation with double-booking prevention.

Operational Tools: Functionality to cancel bookings and view live availability.

Post-MVP Additions: Dynamic pricing logic and room categorization (Standard, Deluxe, Suite).

Technical Stack
Language: Java (JDK 17+)

Database: MySQL 8.0

Driver: JDBC (MySQL Connector/J)

Environment: IntelliJ IDEA

🔧 Setup & Installation
1. Database Setup
Execute the following script in your MySQL Workbench or Command Line to initialize the schema:

SQL
CREATE DATABASE hotel_astana;
-- Run the full database/setup.sql script provided in this repo
2. Add JDBC Driver
Ensure the mysql-connector-j.jar is added to your project's library dependencies:

In IntelliJ: File -> Project Structure -> Libraries -> +.

3. Configuration
Open HotelSystem.java and update the DatabaseConfig class with your local credentials:

Java
private static final String USER = "your_username";
private static final String PASS = "your_password";
Project Structure
src/: Contains the Java source code (HotelSystem.java).

lib/: Contains the MySQL JDBC driver.

database/: Contains the setup.sql file to recreate the database.

ProjectPlanDocumentation.pdf: Original project requirements and timeline.

Acknowledgments
Special thanks to Astana IT University for the architectural guidance on OOP principles.