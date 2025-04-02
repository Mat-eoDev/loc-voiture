package com.example.locvoiturefx.controllers;

import com.example.locvoiturefx.dao.VehiculeDAO;
import com.example.locvoiturefx.models.Vehicule;
import com.example.locvoiturefx.utils.Session;
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

import java.util.List;
import java.util.stream.Collectors;

public class VehiculeController {

    @FXML
    private TableView<Vehicule> vehiculeTable;
    @FXML
    private TableColumn<Vehicule, Integer> colId;
    @FXML
    private TableColumn<Vehicule, String> colMarque;
    @FXML
    private TableColumn<Vehicule, String> colModele;
    @FXML
    private TableColumn<Vehicule, String> colImmatriculation;
    @FXML
    private TextField marqueField;
    @FXML
    private TextField modeleField;
    @FXML
    private TextField immatriculationField;
    @FXML
    private TextField searchVehiculeField;
    @FXML
    private Button addVehiculeButton;
    @FXML
    private Button deleteVehiculeButton;
    @FXML
    private Button editVehiculeButton;
    @FXML
    private Button createUserButton;
    @FXML
    private Button listUserButton; // Bouton pour afficher la liste des utilisateurs

    private ObservableList<Vehicule> vehiculeList = FXCollections.observableArrayList();
    private ObservableList<Vehicule> filteredVehiculeList = FXCollections.observableArrayList();
    private int nextId = 1;

    private VehiculeDAO vehiculeDAO = new VehiculeDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colImmatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));

        List<Vehicule> listFromDb = vehiculeDAO.getAllVehicules();
        if (!listFromDb.isEmpty()) {
            vehiculeList.addAll(listFromDb);
            nextId = listFromDb.stream().mapToInt(Vehicule::getId).max().orElse(0) + 1;
        }
        vehiculeTable.setItems(vehiculeList);

        // Si l'utilisateur connecté est EMPLOYE, désactiver certaines actions
        if (Session.getCurrentUser() != null) {
            String role = Session.getCurrentUser().getRole();
            if ("EMPLOYE".equals(role)) {
                addVehiculeButton.setDisable(true);
                createUserButton.setDisable(true);
                listUserButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleAddVehicule() {
        String marque = marqueField.getText();
        String modele = modeleField.getText();
        String plateInput = immatriculationField.getText();

        if (marque.isEmpty() || modele.isEmpty() || plateInput.isEmpty()) {
            showAlert("Erreur", "Champs manquants", "Tous les champs sont obligatoires.");
            return;
        }

        String formattedPlate = formatLicensePlate(plateInput);
        if (formattedPlate == null) {
            showAlert("Erreur", "Format de plaque invalide", "Format attendu: XX-XXX-XX (pour 7 caractères) ou XX-XXX-XXX (pour 8 caractères).");
            return;
        }

        Vehicule v = new Vehicule(nextId++, marque, modele, formattedPlate);
        vehiculeList.add(v);
        vehiculeDAO.addVehicule(v);
        clearFields();
    }

    @FXML
    private void handleDeleteVehicule() {
        Vehicule selected = vehiculeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            vehiculeList.remove(selected);
            vehiculeDAO.deleteVehicule(selected.getId());
        } else {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner un véhicule à supprimer.");
        }
    }

    @FXML
    private void handleEditVehicule() {
        Vehicule selected = vehiculeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            marqueField.setText(selected.getMarque());
            modeleField.setText(selected.getModele());
            immatriculationField.setText(selected.getImmatriculation());

            editVehiculeButton.setOnAction(e -> {
                String newMarque = marqueField.getText();
                String newModele = modeleField.getText();
                String newPlateInput = immatriculationField.getText();
                if (newMarque.isEmpty() || newModele.isEmpty() || newPlateInput.isEmpty()) {
                    showAlert("Erreur", "Champs manquants", "Tous les champs sont obligatoires pour la modification.");
                    return;
                }
                String formattedPlate = formatLicensePlate(newPlateInput);
                if (formattedPlate == null) {
                    showAlert("Erreur", "Format de plaque invalide", "Format attendu: XX-XXX-XX ou XX-XXX-XXX.");
                    return;
                }
                selected.setMarque(newMarque);
                selected.setModele(newModele);
                selected.setImmatriculation(formattedPlate);
                vehiculeDAO.updateVehicule(selected);
                vehiculeTable.refresh();
                clearFields();
            });
        } else {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner un véhicule à modifier.");
        }
    }

    @FXML
    private void handleSearchVehicule() {
        String query = searchVehiculeField.getText().toLowerCase();
        if (query.isEmpty()) {
            vehiculeTable.setItems(vehiculeList);
            return;
        }
        filteredVehiculeList.setAll(vehiculeList.stream()
                .filter(v -> v.getImmatriculation().toLowerCase().contains(query)
                        || v.getMarque().toLowerCase().contains(query)
                        || v.getModele().toLowerCase().contains(query))
                .collect(Collectors.toList()));
        vehiculeTable.setItems(filteredVehiculeList);
    }

    @FXML
    private void handleReservations() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/reservation.fxml"));
            Stage stage = (Stage) vehiculeTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) vehiculeTable.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateUserScreen() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/userCreation.fxml"));
            Stage stage = (Stage) vehiculeTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserList() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/userList.fxml"));
            Stage stage = (Stage) vehiculeTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

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

    private void clearFields() {
        marqueField.clear();
        modeleField.clear();
        immatriculationField.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
