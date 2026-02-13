package com.astana.dao;

import com.astana.entities.Room;
import com.astana.entities.VipRoom;
import java.sql.*;

/**
 * Data Access Object (DAO) for Hotel Astana.
 * Handles all MySQL interactions for Rooms, Guests, and Reservations.
 */
public class HotelDAO {

    // --- ROOM MANAGEMENT (CRUD) ---

    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, room.getRoomNumber());
            stmt.setString(2, room.getType());
            // POLYMORPHISM: calls getPrice() from Room OR VipRoom automatically
            stmt.setDouble(3, room.getPrice());

            stmt.executeUpdate();
            System.out.println("[SUCCESS] Room " + room.getRoomNumber() + " added.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Could not add room: " + e.getMessage());
        }
    }

    public void viewAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- AVAILABLE ROOMS ---");
            System.out.println("No.\tType\t\tPrice");
            while (rs.next()) {
                System.out.println(rs.getInt("room_number") + "\t" +
                        rs.getString("type") + "\t\t$" +
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] View failed: " + e.getMessage());
        }
    }

    public void deleteRoom(int roomNum) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomNum);
            int rows = stmt.executeUpdate();
            if (rows > 0) System.out.println("[ADMIN] Room deleted.");
            else System.out.println("[WARN] Room not found.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Delete failed: " + e.getMessage());
        }
    }

    // --- GUEST & RESERVATION MANAGEMENT ---

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
            System.out.println("[ERROR] Registration failed: " + e.getMessage());
        }
        return -1;
    }

    /**
     * TRANSACTIONAL LOGIC:
     * Ensures room availability and booking are updated simultaneously.
     */
    public void makeReservation(int roomNum, int guestId, String date) {
        String updateRoomSql = "UPDATE rooms SET is_available = FALSE WHERE room_number = ?";
        String bookSql = "INSERT INTO reservations (room_number, guest_id, check_in) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql);
                 PreparedStatement bookStmt = conn.prepareStatement(bookSql)) {

                // 1. Update room status
                updateStmt.setInt(1, roomNum);
                int updated = updateStmt.executeUpdate();

                if (updated == 0) {
                    throw new SQLException("Room not found or unavailable.");
                }

                // 2. Create reservation record
                bookStmt.setInt(1, roomNum);
                bookStmt.setInt(2, guestId);
                bookStmt.setDate(3, Date.valueOf(date));
                bookStmt.executeUpdate();

                conn.commit(); // Save both changes
                System.out.println("[SUCCESS] Booking confirmed.");
            } catch (SQLException e) {
                conn.rollback(); // Undo everything if an error occurs
                System.out.println("[TRANSACTION FAILED] " + e.getMessage());
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void cancelReservation(int roomNum) {
        String deleteResSql = "DELETE FROM reservations WHERE room_number = ?";
        String makeAvailableSql = "UPDATE rooms SET is_available = TRUE WHERE room_number = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement delStmt = conn.prepareStatement(deleteResSql);
                 PreparedStatement updStmt = conn.prepareStatement(makeAvailableSql)) {

                delStmt.setInt(1, roomNum);
                int deleted = delStmt.executeUpdate();

                if (deleted > 0) {
                    updStmt.setInt(1, roomNum);
                    updStmt.executeUpdate();
                    conn.commit();
                    System.out.println("[SUCCESS] Reservation cancelled.");
                } else {
                    System.out.println("[WARN] No active reservation found for this room.");
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[ERROR] Cancellation failed.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}



