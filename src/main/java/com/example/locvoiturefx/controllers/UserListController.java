package com.example.locvoiturefx.controllers;

import com.example.locvoiturefx.models.User;
import com.example.locvoiturefx.services.AuthService;
import com.example.locvoiturefx.utils.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.stream.Collectors;

public class UserListController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TextField searchUserField;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> filteredUserList = FXCollections.observableArrayList();

    private AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Charger la liste des utilisateurs depuis AuthService (ici, une liste statique)
        List<User> list = authService.getAllUsers();
        userList.addAll(list);
        userTable.setItems(userList);
    }

    @FXML
    private void handleSearchUser() {
        String query = searchUserField.getText().toLowerCase();
        if (query.isEmpty()) {
            userTable.setItems(userList);
            return;
        }
        filteredUserList.setAll(userList.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query) ||
                        u.getRole().toLowerCase().contains(query))
                .collect(Collectors.toList()));
        userTable.setItems(filteredUserList);
    }

    @FXML
    private void handleEditUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        // Création d'un dialogue personnalisé pour modifier l'utilisateur
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        // Bouton d'enregistrement
        ButtonType updateButtonType = new ButtonType("Enregistrer", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Création d'un formulaire dans une GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setText(selected.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(selected.getPassword());
        ComboBox<String> roleComboBox = new ComboBox<>();
        // Pour l'exemple, on permet de choisir parmi DIRECTEUR, MANAGER et EMPLOYE
        roleComboBox.setItems(FXCollections.observableArrayList("DIRECTEUR", "MANAGER", "EMPLOYE"));
        roleComboBox.setValue(selected.getRole());

        grid.add(new Label("Nom d'utilisateur:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convertir le résultat lorsque l'utilisateur clique sur Enregistrer
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new User(selected.getId(), usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedUser -> {
            selected.setUsername(updatedUser.getUsername());
            selected.setPassword(updatedUser.getPassword());
            selected.setRole(updatedUser.getRole());
            userTable.refresh();
            showAlert("Succès", "Utilisateur modifié", "Les informations de l'utilisateur ont été modifiées.");
        });
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Empêcher la suppression du compte actuellement connecté
            if (Session.getCurrentUser() != null && Session.getCurrentUser().getId() == selected.getId()) {
                showAlert("Erreur", "Suppression impossible", "Vous ne pouvez pas supprimer votre propre compte.");
                return;
            }
            userList.remove(selected);
            authService.getAllUsers().remove(selected);
            userTable.refresh();
            showAlert("Succès", "Utilisateur supprimé", "Le compte a été supprimé.");
        } else {
            showAlert("Erreur", "Sélection manquante", "Veuillez sélectionner un utilisateur à supprimer.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicule.fxml"));
            Stage stage = (Stage) userTable.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
