import java.sql.*;
import java.util.Scanner;

/**
 * Project: Hotel Reservation System for Hotel "Astana"
 * Developer: Margulan Zhamantayev
 * Features: All MVP requirements (Room Mgmt, Guest Reg, Booking, Cancellation, Availability)
 */

class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_astana";
    private static final String USER = "root";
    private static final String PASS = "Gadzelkin123"; // Update this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

class Hotel {

    // MVP FEATURE 1: Room Management (Adding Rooms)
    public void addRoom(int number, String type, double price) {
        String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, number);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            System.out.println("Room " + number + " added to the system.");
        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    // MVP FEATURE 2: Guest Registration
    public int registerGuest(String name, String email) {
        String sql = "INSERT INTO guests (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
        return -1;
    }

    // MVP FEATURE 3: Booking Engine (Double-Booking Prevention via Transactions)
    public void makeReservation(int roomNum, int guestId, String date) {
        String checkSql = "SELECT is_available FROM rooms WHERE room_number = ?";
        String bookSql = "INSERT INTO reservations (room_number, guest_id, check_in) VALUES (?, ?, ?)";
        String updateRoomSql = "UPDATE rooms SET is_available = FALSE WHERE room_number = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Check availability first
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, roomNum);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next() || !rs.getBoolean("is_available")) {
                    System.out.println("Error: Room " + roomNum + " is already booked or doesn't exist.");
                    return;
                }
            }

            // Proceed with booking
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql)) {
                bookStmt.setInt(1, roomNum);
                bookStmt.setInt(2, guestId);
                bookStmt.setDate(3, Date.valueOf(date));
                bookStmt.executeUpdate();

                updateStmt.setInt(1, roomNum);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("Reservation successful for Room " + roomNum);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed: " + e.getMessage());
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // MVP FEATURE 4: Operational Tools (View Available Rooms)
    public void viewAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE";
        System.out.println("\n--- Available Rooms ---");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("Room %d | Type: %s | Price: $%.2f%n",
                        rs.getInt("room_number"), rs.getString("type"), rs.getDouble("price"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // MVP FEATURE 4: Operational Tools (Cancel Reservation)
    public void cancelReservation(int roomNum) {
        String deleteRes = "DELETE FROM reservations WHERE room_number = ?";
        String updateRoom = "UPDATE rooms SET is_available = TRUE WHERE room_number = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement dStmt = conn.prepareStatement(deleteRes);
                 PreparedStatement uStmt = conn.prepareStatement(updateRoom)) {
                dStmt.setInt(1, roomNum);
                dStmt.executeUpdate();
                uStmt.setInt(1, roomNum);
                uStmt.executeUpdate();
                conn.commit();
                System.out.println("Reservation for Room " + roomNum + " cancelled.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Cancellation failed.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

public class HotelSystem {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- HOTEL ASTANA MVP MENU ---");
            System.out.println("1. Add New Room (Admin)\n2. Register Guest & Book\n3. View Available Rooms\n4. Cancel Booking\n5. Exit");
            System.out.print("Select: ");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Room Num: "); int num = sc.nextInt(); sc.nextLine();
                    System.out.print("Type: "); String type = sc.nextLine();
                    System.out.print("Price: "); double price = sc.nextDouble();
                    hotel.addRoom(num, type, price);
                    break;
                case 2:
                    System.out.print("Guest Name: "); String name = sc.nextLine();
                    System.out.print("Guest Email: "); String email = sc.nextLine();
                    int gId = hotel.registerGuest(name, email);
                    hotel.viewAvailableRooms();
                    System.out.print("Select Room: "); int rNum = sc.nextInt();
                    hotel.makeReservation(rNum, gId, "2026-02-06");
                    break;
                case 3:
                    hotel.viewAvailableRooms();
                    break;
                case 4:
                    System.out.print("Enter Room Number to free up: ");
                    int cRoom = sc.nextInt();
                    hotel.cancelReservation(cRoom);
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }
}
