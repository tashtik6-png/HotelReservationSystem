package com.astana.main;

import com.astana.dao.HotelDAO;
import com.astana.entities.Room;
import com.astana.entities.VipRoom;
import java.util.Scanner;

/**
 * Main Entry Point for the Hotel Astana Management System.
 * Handles the User Interface and coordinates between Entities and the DAO.
 */
public class HotelSystem {
    public static void main(String[] args) {
        // Initialize the Data Access Object
        HotelDAO dao = new HotelDAO();
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   WELCOME TO HOTEL ASTANA SYSTEM   ");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Add Standard Room");
            System.out.println("2. Add VIP Suite (20% Luxury Tax)");
            System.out.println("3. View Available Rooms");
            System.out.println("4. Book a Room");
            System.out.println("5. Cancel a Reservation");
            System.out.println("6. Delete Room (Admin)");
            System.out.println("7. Exit");
            System.out.print("Select an option: ");

            // Input validation to prevent crashes on non-integer input
            if (!scanner.hasNextInt()) {
                System.out.println("[ERROR] Please enter a valid number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Add Standard Room
                    System.out.print("Enter Room Number: ");
                    int roomNum = scanner.nextInt();
                    System.out.print("Enter Base Price: ");
                    double price = scanner.nextDouble();
                    // Demonstrating Encapsulation
                    dao.addRoom(new Room(roomNum, "Standard", price));
                    break;

                case 2: // Add VIP Suite
                    System.out.print("Enter VIP Room Number: ");
                    int vNum = scanner.nextInt();
                    System.out.print("Enter Base Price: ");
                    double vPrice = scanner.nextDouble();
                    // POLYMORPHISM: Passing a VipRoom as a Room object
                    dao.addRoom(new VipRoom(vNum, vPrice));
                    break;

                case 3: // View Available
                    dao.viewAvailableRooms();
                    break;

                case 4: // Booking Process
                    // UX Improvement: Show rooms first
                    System.out.println("\n--- STARTING RESERVATION ---");
                    dao.viewAvailableRooms();

                    System.out.print("\nEnter Room Number to Book: ");
                    int bookRoom = scanner.nextInt();

                    System.out.print("Enter Guest Name: ");
                    scanner.nextLine(); // Clear buffer
                    String name = scanner.nextLine();

                    System.out.print("Enter Guest Email: ");
                    String email = scanner.next();

                    // Step 1: Register the Guest and get ID
                    int guestId = dao.registerGuest(name, email);

                    if (guestId != -1) {
                        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
                        String date = scanner.next();
                        // Step 2: Create Reservation (Transactional)
                        dao.makeReservation(bookRoom, guestId, date);
                    }
                    break;

                case 5: // Cancel Reservation
                    System.out.print("Enter Room Number to free up: ");
                    int cancelRoom = scanner.nextInt();
                    dao.cancelReservation(cancelRoom);
                    break;

                case 6: // Admin Delete Room
                    System.out.print("Enter Room Number to PERMANENTLY delete: ");
                    int delNum = scanner.nextInt();
                    System.out.print("Confirm deletion? (y/n): ");
                    if (scanner.next().equalsIgnoreCase("y")) {
                        dao.deleteRoom(delNum);
                    }
                    break;

                case 7: // Exit
                    System.out.println("Thank you for using Hotel Astana System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("[INVALID] Please choose a number between 1 and 7.");
            }
        }
    }
}