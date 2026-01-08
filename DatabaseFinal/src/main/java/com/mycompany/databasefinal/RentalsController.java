package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;

public class RentalsController {

    @FXML
    private TableView<Rental> allRentalsTable;
    @FXML
    private TableView<Rental> activeRentalsTable;
    @FXML
    private Label activeRentalCountLabel;
    @FXML
    private Button markAsReturnedButton;
    @FXML
    private TableColumn<Rental, Integer> rentalIdCol;
    @FXML
    private TableColumn<Rental, Integer> rentalIdCol2;
    @FXML
    private TableColumn<Rental, Integer> customerIdCol;
    @FXML
    private TableColumn<Rental, String> customerNameCol;
    @FXML
    private TableColumn<Rental, Integer> gameIdCol;
    @FXML
    private TableColumn<Rental, String> gameNameCol;
    @FXML
    private TableColumn<Rental, LocalDate> rentDateCol;
    @FXML
    private TableColumn<Rental, LocalDate> rentDateCol2;
    @FXML
    private TableColumn<Rental, LocalDate> returnDateCol;

    private ObservableList<Rental> activeRentalList = FXCollections.observableArrayList();
    private ObservableList<Rental> rentalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        setupRowColorCoding();
        refreshRentalsTable();
        refreshActiveRentalsTable();
    }

    private void setupTables() {
        // Configure columns for allRentalsTable
        rentalIdCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalIdCol2.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        gameIdCol.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        gameNameCol.setCellValueFactory(new PropertyValueFactory<>("gameName"));
        rentDateCol.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        rentDateCol2.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        // Add columns to the tables
        activeRentalsTable.getColumns().addAll(rentalIdCol, customerIdCol, rentDateCol, returnDateCol);
        allRentalsTable.getColumns().addAll(rentalIdCol2, customerNameCol, gameNameCol, rentDateCol2, returnDateCol);
    }

    private void setupRowColorCoding() {
        activeRentalsTable.setRowFactory(tv -> new TableRow<Rental>() {
            @Override // Using an anonymous inner class to override updateItem method from TableRow class to setStyle of rows to red if "overdue" or green if not "overdue"
            protected void updateItem(Rental rental, boolean empty) {
                super.updateItem(rental, empty);
                if (rental != null && !empty) {
                    LocalDate rentDate = rental.getReceivedDate();
                    // Using java.time ChronoUnit.DAYS (enumeration value for days) to find out the days between rentDate, and the current date
                    long daysElapsed = ChronoUnit.DAYS.between(rentDate, LocalDate.now());
                    // Updates row colors accordingly based on whether it is over 14 days or less than
                    setStyle(daysElapsed > 14 ? "-fx-background-color: #ffcccc;" : "-fx-background-color: #ccffcc;");
                } else {
                    setStyle(""); // Reset style if row is empty or rental is null
                }
            }
        });
    }

    // Pulling any rentals from Rentals Table where the rental_date is NULL (aka the rental hasn't been returned yet) to put in the ActiveRentalsTable
    private void refreshActiveRentalsTable() {
        activeRentalList.clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT rental_id, customer_id, game_id, received_date, returned_date FROM Rentals WHERE returned_date IS NULL";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Rental rental = new Rental(
                        rs.getInt("rental_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("game_id"),
                        Rental.convertSqlDateToLocalDate(rs.getDate("received_date"))
                );
                activeRentalList.add(rental);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        activeRentalsTable.getItems().setAll(activeRentalList);
        activeRentalCountLabel.setText("Active Rentals: " + activeRentalList.size());
    }

    // refreshRentalsTable queries RentalHistory, a view from the database that provides the columns with its needed components
    private void refreshRentalsTable() {
        rentalList.clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM RentalHistory";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Rental rental = new Rental(
                        rs.getInt("rental_id"),
                        rs.getString("customer_name"),
                        rs.getString("game_title"),
                        Rental.convertSqlDateToLocalDate(rs.getDate("received_date")),
                        Rental.convertSqlDateToLocalDate(rs.getDate("returned_date"))
                );
                rentalList.add(rental);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        allRentalsTable.getItems().setAll(rentalList);
    }
    
    // Upon pressing the markAsReturned button, the returned_date within the Rentals DB table is updated so that the selected record's returned date is updated to today's date
    @FXML
    private void handleMarkAsReturned() {
        Rental selectedRental = activeRentalsTable.getSelectionModel().getSelectedItem();
        if (selectedRental != null) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "UPDATE Rentals SET returned_date = CURDATE() WHERE rental_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, selectedRental.getRentalId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            refreshActiveRentalsTable();
            refreshRentalsTable();
        } else {
            showAlert("No rental selected", "Please select a rental to mark as returned.");
        }
    }
    
    // Handles potential errors such as an invalid rental being selected and instead visually shows a pop up that alerts what the error is
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void openHub() {
        try {
            App.setRoot("GameVaultHub");  // Switch to the hub scene
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
