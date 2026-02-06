-- -----------------------------------------------------
-- 1. Database Creation
-- -----------------------------------------------------
CREATE DATABASE IF NOT EXISTS hotel_astana;
USE hotel_astana;

-- -----------------------------------------------------
-- 2. Table Structures (DDL)
-- -----------------------------------------------------

-- Create Rooms table first (it has no foreign keys)
CREATE TABLE rooms (
    room_number INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- Create Guests table (it has no foreign keys)
CREATE TABLE guests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- Create Reservations table (Depends on Rooms and Guests)
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number INT,
    guest_id INT,
    check_in DATE,
    FOREIGN KEY (room_number) REFERENCES rooms(room_number),
    FOREIGN KEY (guest_id) REFERENCES guests(id)
);

-- -----------------------------------------------------
-- 3. Seed Data (DML)
-- -----------------------------------------------------
INSERT INTO rooms (room_number, type, price) VALUES (101, 'Standard', 50.0);
INSERT INTO rooms (room_number, type, price) VALUES (102, 'Deluxe', 85.0);
INSERT INTO rooms (room_number, type, price) VALUES (201, 'Suite', 150.0);