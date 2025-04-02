package com.example.locvoiturefx.controllers;

import com.example.locvoiturefx.dao.ReservationDAO;
import com.example.locvoiturefx.dao.VehiculeDAO;
import com.example.locvoiturefx.models.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {

    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, Integer> colResId;
    @FXML
    private TableColumn<Reservation, String> colClient;
    @FXML
    private TableColumn<Reservation, String> colImmatriculation;
    @FXML
    private TableColumn<Reservation, String> colStartDate;
    @FXML
    private TableColumn<Reservation, String> colEndDate;
    @FXML
    private TableColumn<Reservation, Long> colDaysRemaining;

    @FXML
    private TextField clientField;
    @FXML
    private TextField licensePlateField;
    @FXML
    private TextField startDateField;
    @FXML
    private TextField endDateField;
    @FXML
    private TextField searchField;

    @FXML
    private Button editReservationButton;
    @FXML
    private Button deleteReservationButton;

    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ObservableList<Reservation> filteredList = FXCollections.observableArrayList();
    private int nextResId = 1;

    private ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colImmatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colDaysRemaining.setCellValueFactory(new PropertyValueFactory<>("daysRemaining"));

        List<Reservation> listFromDb = reservationDAO.getAllReservations();
        if (!listFromDb.isEmpty()) {
            reservationList.addAll(listFromDb);
            nextResId = listFromDb.stream().mapToInt(Reservation::getId).max().orElse(0) + 1;
        }
        reservationTable.setItems(reservationList);
    }

    @FXML
    private void handleAddReservation() {
        String client = clientField.getText();
        String plate = licensePlateField.getText();
        String start = startDateField.getText();
        String end = endDateField.getText();

        if (client.isEmpty() || plate.isEmpty() || start.isEmpty() || end.isEmpty()) {
            showAlert("Erreur", "Champs manquants", "Tous les champs sont obligatoires.");
            return;
        }

        String formattedPlate = formatLicensePlate(plate);
        if (formattedPlate == null) {
            showAlert("Erreur", "Format de plaque invalide",
                    "Format attendu: XX-XXX-XX (pour 7 caractères) ou XX-XXX-XXX (pour 8 caractères).");
            return;
        }

        // Vérifier que le véhicule existe dans la gestion des véhicules
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        boolean vehicleExists = vehiculeDAO.getAllVehicules().stream()
                .anyMatch(v -> v.getImmatriculation().equalsIgnoreCase(formattedPlate));
        if (!vehicleExists) {
            showAlert("Erreur", "Véhicule inexistant",
                    "Le véhicule avec la plaque " + formattedPlate + " n'existe pas dans la gestion des véhicules.");
            return;
        }

        // Vérifier que le véhicule n'est pas déjà réservé pour la période donnée
        if (isVehicleReserved(formattedPlate, start, end)) {
            showAlert("Erreur", "Véhicule déjà réservé",
                    "Ce véhicule est déjà réservé pour la période indiquée.");
            return;
        }

        Reservation r = new Reservation(nextResId++, client, formattedPlate, start, end);
        reservationList.add(r);
        reservationDAO.addReservation(r);
        clearFields();
    }

    @FXML
    private void handleDeleteReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reservationList.remove(selected);
            reservationDAO.deleteReservation(selected.getId());
        } else {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner une réservation à supprimer.");
        }
    }

    @FXML
    private void handleEditReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            clientField.setText(selected.getClientName());
            licensePlateField.setText(selected.getImmatriculation());
            startDateField.setText(selected.getStartDate());
            endDateField.setText(selected.getEndDate());

            editReservationButton.setOnAction(e -> {
                selected.setClientName(clientField.getText());
                String formattedPlate = formatLicensePlate(licensePlateField.getText());
                if (formattedPlate == null) {
                    showAlert("Erreur", "Format de plaque invalide", "Format attendu: XX-XXX-XX ou XX-XXX-XXX.");
                    return;
                }
                if (isVehicleReserved(formattedPlate, startDateField.getText(), endDateField.getText(), selected.getId())) {
                    showAlert("Erreur", "Véhicule déjà réservé", "Ce véhicule est déjà réservé pour la période indiquée.");
                    return;
                }
                selected.setImmatriculation(formattedPlate);
                selected.setStartDate(startDateField.getText());
                selected.setEndDate(endDateField.getText());
                reservationDAO.updateReservation(selected);
                reservationTable.refresh();
                clearFields();
            });
        } else {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner une réservation à modifier.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicule.fxml"));
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            reservationTable.setItems(reservationList);
            return;
        }
        filteredList.setAll(reservationList.stream()
                .filter(r -> r.getImmatriculation().toLowerCase().contains(query) ||
                        r.getClientName().toLowerCase().contains(query))
                .collect(Collectors.toList()));
        reservationTable.setItems(filteredList);
    }

    // Formate la plaque pour accepter 7 ou 8 caractères après nettoyage.
    private String formatLicensePlate(String plate) {
        String cleaned = plate.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        System.out.println("Cleaned plate: [" + cleaned + "], length=" + cleaned.length());
        if (cleaned.length() == 7) {
            return cleaned.substring(0, 2) + "-" + cleaned.substring(2, 5) + "-" + cleaned.substring(5, 7);
        } else if (cleaned.length() == 8) {
            return cleaned.substring(0, 2) + "-" + cleaned.substring(2, 5) + "-" + cleaned.substring(5, 8);
        } else {
            return null;
        }
    }

    // Vérifie si le véhicule est déjà réservé pour la période donnée
    private boolean isVehicleReserved(String plate, String start, String end) {
        LocalDate newStart = LocalDate.parse(start);
        LocalDate newEnd = LocalDate.parse(end);
        for (Reservation r : reservationList) {
            if (r.getImmatriculation().equalsIgnoreCase(plate)) {
                LocalDate existingStart = LocalDate.parse(r.getStartDate());
                LocalDate existingEnd = LocalDate.parse(r.getEndDate());
                if (!(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd))) {
                    return true;
                }
            }
        }
        return false;
    }

    // Variante pour ignorer une réservation lors de la modification
    private boolean isVehicleReserved(String plate, String start, String end, int ignoreId) {
        LocalDate newStart = LocalDate.parse(start);
        LocalDate newEnd = LocalDate.parse(end);
        for (Reservation r : reservationList) {
            if (r.getId() == ignoreId) continue;
            if (r.getImmatriculation().equalsIgnoreCase(plate)) {
                LocalDate existingStart = LocalDate.parse(r.getStartDate());
                LocalDate existingEnd = LocalDate.parse(r.getEndDate());
                if (!(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearFields() {
        clientField.clear();
        licensePlateField.clear();
        startDateField.clear();
        endDateField.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
