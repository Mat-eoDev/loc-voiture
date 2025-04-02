package com.example.locvoiturefx.dao;

import com.example.locvoiturefx.models.Vehicule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAO {

    public VehiculeDAO() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS vehicules (" +
                    "id INTEGER PRIMARY KEY, " +
                    "marque TEXT NOT NULL, " +
                    "modele TEXT NOT NULL, " +
                    "immatriculation TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVehicule(Vehicule v) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicules (id, marque, modele, immatriculation) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, v.getId());
            pstmt.setString(2, v.getMarque());
            pstmt.setString(3, v.getModele());
            pstmt.setString(4, v.getImmatriculation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vehicule> getAllVehicules() {
        List<Vehicule> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM vehicules")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String marque = rs.getString("marque");
                String modele = rs.getString("modele");
                String immatriculation = rs.getString("immatriculation");
                list.add(new Vehicule(id, marque, modele, immatriculation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteVehicule(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vehicules WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVehicule(Vehicule v) {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE vehicules SET marque = ?, modele = ?, immatriculation = ? WHERE id = ?")) {
            pstmt.setString(1, v.getMarque());
            pstmt.setString(2, v.getModele());
            pstmt.setString(3, v.getImmatriculation());
            pstmt.setInt(4, v.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
