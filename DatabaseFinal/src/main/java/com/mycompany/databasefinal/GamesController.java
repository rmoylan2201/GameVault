package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GamesController {

    @FXML
    private TableView<Game> gameTable, topSellingGameTable;
    @FXML
    private TableColumn<Game, Integer> IdColumn, yearColumn, stockColumn, topSellingYearColumn, totalSalesColumn;
    @FXML
    private TableColumn<Game, String> titleColumn, genreColumn, platformColumn, topSellingTitleColumn;
    @FXML
    private TableColumn<Game, Double> priceColumn, topSellingPriceColumn;
    @FXML
    private ComboBox<String> searchTypeComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton, hubButton;
    // Observable lists for holding game data
    private ObservableList<Game> gamesList = FXCollections.observableArrayList();
    private ObservableList<Game> topSellingGamesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize search type combo box
        searchTypeComboBox.setItems(FXCollections.observableArrayList("ID", "Title", "Genre", "Platform"));
        searchTypeComboBox.setValue("Title"); // Default search type
        // Set up table columns with property value factories
        setTableColumnBindings();
        // Fetch and display data
        fetchGameData();
        fetchTopSellingGamesData();
    }

    // Helper method to bind the two table's columns
    private void setTableColumnBindings() {
        IdColumn.setCellValueFactory(cellData -> cellData.getValue().getGameIdProperty().asObject());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().getGenreProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getYearProperty().asObject());
        platformColumn.setCellValueFactory(cellData -> cellData.getValue().getPlatformProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().getStockQuantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());
        topSellingTitleColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
        topSellingYearColumn.setCellValueFactory(cellData -> cellData.getValue().getYearProperty().asObject());
        topSellingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty().asObject());
        totalSalesColumn.setCellValueFactory(cellData -> cellData.getValue().getSalesProperty().asObject());
    }

    // Method to fetch all games in stock from the database using the GameDetails view
    @FXML
    private void fetchGameData() {
        String sql = "SELECT * FROM GameDetails";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                gamesList.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gameTable.setItems(gamesList);
    }

    // Method to fetch top-selling games from the database using TopSellingGames view and then limiting it to the first 5
    @FXML
    private void fetchTopSellingGamesData() {
        String sql = "SELECT * FROM TopSellingGames LIMIT 5";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Game game = mapTopSellingResultSetToGame(rs);
                topSellingGamesList.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        topSellingGameTable.setItems(topSellingGamesList);
    }

    // Method to map ResultSet to a Game objects
    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        int gameId = rs.getInt("game_id");
        String title = rs.getString("title");
        String genre = rs.getString("genre_name");
        int year = rs.getInt("year_released");
        String platform = rs.getString("platform_name");
        int stockQuantity = rs.getInt("quantity_in_stock");
        double price = rs.getDouble("price");
        return new Game(gameId, title, genre, year, platform, stockQuantity, price, 0); // 0 for sales
    }

    // Method to map ResultSet to a Game object for top-selling gamess
    private Game mapTopSellingResultSetToGame(ResultSet rs) throws SQLException {
        int gameId = rs.getInt("game_id");
        String title = rs.getString("game_title");
        int year = rs.getInt("year_released");
        double price = rs.getDouble("game_price");
        int totalSales = rs.getInt("total_sales");
        return new Game(gameId, title, "", year, "", 0, price, totalSales); // Missing details in top-selling data
    }

    // Method to handle functionality for the search button. Calls the SearchGames procedure from within the DB and passes the parameters (filter type and the text field's data)
    @FXML
    private void handleSearch() {
        String filterType = searchTypeComboBox.getValue();
        String searchValue = searchTextField.getText();
        ObservableList<Game> filteredGames = FXCollections.observableArrayList();
        String sql = "{CALL SearchGames(?, ?)}";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, filterType);
            stmt.setString(2, searchValue);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Game game = mapResultSetToGame(rs);
                    filteredGames.add(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gameTable.setItems(filteredGames);
    }

    // Method to open the Game Vault Hub scene
    @FXML
    private void openHub() {
        try {
            App.setRoot("GameVaultHub");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
