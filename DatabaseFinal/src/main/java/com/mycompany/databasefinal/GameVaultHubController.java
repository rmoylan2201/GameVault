package com.mycompany.databasefinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GameVaultHubController {

    @FXML private Button manageCustomersButton;
    @FXML private Button viewGamesButton;
    @FXML private Button manageGamesButton;
    @FXML private Button manageOrdersButton;
    @FXML private Button manageRentalsButton;
    @FXML private Button viewPaymentsButton;
    @FXML private Button logoutButton;
    @FXML private Label titleLabel;
    @FXML private HBox menuBox;
    
    // Begins by adding functionality to all of the buttons within the scene
    @FXML
    private void initialize() {
        manageCustomersButton.setOnAction(e -> switchScene("CustomerManagement"));
        viewGamesButton.setOnAction(e -> switchScene("GamesScene"));
        manageGamesButton.setOnAction(e -> switchScene("InventoryScene"));
        manageOrdersButton.setOnAction(e -> switchScene("OrderScene"));
        manageRentalsButton.setOnAction(e -> switchScene("RentalsScene"));
        viewPaymentsButton.setOnAction(e -> switchScene("PaymentsScene"));
        logoutButton.setOnAction(e -> logout());
    }
    
    // Helper method used to switch between scenes
    private void switchScene(String fxmlName) {
        try {
            App.setRoot(fxmlName);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging this properly in production
        }
    }

    private void logout() {
        Platform.exit(); 
    }
}