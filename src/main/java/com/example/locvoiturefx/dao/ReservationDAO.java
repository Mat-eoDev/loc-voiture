package com.example.locvoiturefx.dao;

import com.example.locvoiturefx.models.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public ReservationDAO() {
        // Création de la table "reservations" avec la colonne "immatriculation"
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "id INTEGER PRIMARY KEY, " +
                    "clientName TEXT NOT NULL, " +
                    "immatriculation TEXT NOT NULL, " +
                    "startDate TEXT NOT NULL, " +
                    "endDate TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter une réservation dans la base
    public void addReservation(Reservation r) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO reservations (id, clientName, immatriculation, startDate, endDate) VALUES (?, ?, ?, ?, ?)")
        ) {
            pstmt.setInt(1, r.getId());
            pstmt.setString(2, r.getClientName());
            pstmt.setString(3, r.getImmatriculation());
            pstmt.setString(4, r.getStartDate());
            pstmt.setString(5, r.getEndDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer toutes les réservations de la base
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reservations")
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String clientName = rs.getString("clientName");
                String immatriculation = rs.getString("immatriculation");
                String startDate = rs.getString("startDate");
                String endDate = rs.getString("endDate");
                list.add(new Reservation(id, clientName, immatriculation, startDate, endDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Méthode pour supprimer une réservation par ID
    public void deleteReservation(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM reservations WHERE id = ?")
        ) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour une réservation existante
    public void updateReservation(Reservation r) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE reservations SET clientName = ?, immatriculation = ?, startDate = ?, endDate = ? WHERE id = ?")
        ) {
            pstmt.setString(1, r.getClientName());
            pstmt.setString(2, r.getImmatriculation());
            pstmt.setString(3, r.getStartDate());
            pstmt.setString(4, r.getEndDate());
            pstmt.setInt(5, r.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
