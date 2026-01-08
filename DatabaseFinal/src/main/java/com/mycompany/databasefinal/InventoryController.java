package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class InventoryController {

    @FXML
    private TextField gameTitleField, priceField, gameIdField, stockQuantityField;
    @FXML
    private ComboBox<String> genreComboBox, platformComboBox;
    @FXML
    private DatePicker releaseYearField, restockDateField;
    @FXML
    private Button updateBtn, addBtn, hubBtn;
    private ObservableList<String> genres = FXCollections.observableArrayList();
    private ObservableList<String> platforms = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadGenres();
        loadPlatforms();
    }

    // Loads all genres_names from the DB Genres table to fill the genreComboBox 
    private void loadGenres() {
        String query = "SELECT genre_name FROM Genres";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                genres.add(rs.getString("genre_name"));
            }
            genreComboBox.setItems(genres);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load genres.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Loads all platform_names from the DB Platforms table to fill the platformComboBox
    private void loadPlatforms() {
        String query = "SELECT platform_name FROM Platforms";
        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                platforms.add(rs.getString("platform_name"));
            }
            platformComboBox.setItems(platforms);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load platforms.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Add a new game to the database and inventory, validates user input and shows alerts if any data is missing or invalid
    @FXML
    private void addNewGame() {
        String title = gameTitleField.getText();
        String genre = genreComboBox.getValue();
        String platform = platformComboBox.getValue();
        LocalDate releaseDate = releaseYearField.getValue();
        String priceText = priceField.getText();
        if (isFieldEmpty(title, genre, platform, releaseDate, priceText)) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }
        try {
            int releaseYear = releaseDate.getYear();
            double price = Double.parseDouble(priceText);
            int genreId = getGenreIdFromName(genre);
            int platformId = getPlatformIdFromName(platform);
            int gameId = addGameToDatabase(title, genreId, platformId, releaseYear, price);
             // Adds entry to Inventory table with default stock
            if (gameId != -1) {
                addInventoryRecord(gameId);
                showAlert("Success", "New game and inventory record added successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to retrieve game ID after insertion.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input for price.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Error", "Error while adding the new game.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Check if required fields are empty
    private boolean isFieldEmpty(String title, String genre, String platform, LocalDate releaseDate, String priceText) {
        return title.isEmpty() || genre == null || platform == null || releaseDate == null || priceText.isEmpty();
    }

    // Get genre ID by quering for any genre_ids in the DB Genres table that matches genre names
    private int getGenreIdFromName(String genreName) throws SQLException {
        String sql = "SELECT genre_id FROM Genres WHERE genre_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genreName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("genre_id");
            }
            return -1;
        }
    }

    // Get platform ID by quering for any platform_ids in the DB Genres table that matches platform names
    private int getPlatformIdFromName(String platformName) throws SQLException {
        String sql = "SELECT platform_id FROM Platforms WHERE platform_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, platformName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("platform_id");
            }
            return -1;
        }
    }

    // Inserts a new game record into the Games table and returns the generated game_id.
    private int addGameToDatabase(String title, int genreId, int platformId, int releaseYear, double price) throws SQLException {
        String sql = "INSERT INTO Games (title, genre_id, platform_id, year_released, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setInt(2, genreId);
            stmt.setInt(3, platformId);
            stmt.setInt(4, releaseYear);
            stmt.setDouble(5, price);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating game failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating game failed, no ID obtained.");
                }
            }
        }
    }

    // Creates an inventory entry for the new game with quantity = 0 and restock_date = NULL
    private void addInventoryRecord(int gameId) throws SQLException {
        String sql = "INSERT INTO Inventory (game_id, quantity_in_stock, restock_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            stmt.setInt(2, 0); // Default quantity = 0
            stmt.setDate(3, null); // Default restock date = NULL
            stmt.executeUpdate();
        }
    }

    // Handles the event of updating the stock quantity and restock date of an existing game
    @FXML
    private void updateStock() {
        String stockQuantityText = stockQuantityField.getText();
        LocalDate restockDate = restockDateField.getValue();
        if (stockQuantityText.isEmpty() || restockDate == null) {
            showAlert("Error", "Please enter a stock quantity and choose a restock date.", Alert.AlertType.ERROR);
            return;
        }
        try {
            int stockQuantity = Integer.parseInt(stockQuantityText);
            updateInventory(stockQuantity, restockDate);
            showAlert("Success", "Stock updated successfully.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid stock quantity. Please enter a valid number.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Error", "An error occurred while updating the stock.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Update the inventory record in the database
    private void updateInventory(int stockQuantity, LocalDate restockDate) throws SQLException {
        String updateStockSql = "UPDATE Inventory SET quantity_in_stock = ?, restock_date = ? WHERE game_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
            updateStmt.setInt(1, stockQuantity);
            updateStmt.setDate(2, Date.valueOf(restockDate));
            updateStmt.setInt(3, Integer.parseInt(gameIdField.getText())); 
            updateStmt.executeUpdate();
        }
    }

    // Show an alert with a message
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openHub() {
        try {
            App.setRoot("GameVaultHub");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}