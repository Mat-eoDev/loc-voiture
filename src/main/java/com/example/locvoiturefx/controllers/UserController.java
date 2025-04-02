package com.example.locvoiturefx.controllers;

import com.example.locvoiturefx.models.User;
import com.example.locvoiturefx.services.AuthService;
import com.example.locvoiturefx.utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label statusLabel;

    private AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Selon le rôle de l'utilisateur connecté, on limite les rôles que l'on peut créer
        String currentRole = "";
        if (Session.getCurrentUser() != null) {
            currentRole = Session.getCurrentUser().getRole();
        }
        if ("DIRECTEUR".equals(currentRole)) {
            roleComboBox.setItems(FXCollections.observableArrayList("DIRECTEUR", "MANAGER"));
        } else if ("MANAGER".equals(currentRole)) {
            roleComboBox.setItems(FXCollections.observableArrayList("EMPLOYE"));
        } else {
            roleComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleCreateUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("Tous les champs sont obligatoires.");
            return;
        }

        int id = authService.getAllUsers().size() + 1;
        User newUser = new User(id, username, password, role);

        boolean success = authService.addUser(newUser);
        if (success) {
            statusLabel.setText("Utilisateur créé avec succès.");
            usernameField.clear();
            passwordField.clear();
            roleComboBox.getSelectionModel().clearSelection();
        } else {
            statusLabel.setText("Nom d'utilisateur déjà existant.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicule.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
