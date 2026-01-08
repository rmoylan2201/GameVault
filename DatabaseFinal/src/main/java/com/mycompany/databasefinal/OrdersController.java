package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

public class OrdersController {

    @FXML private TextField gameIdField;
    @FXML private TextField customerIdField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> orderTypeCombo;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField quantityField;
    @FXML private DatePicker orderDatePicker;
    @FXML private Button hubButton;
    @FXML private Button insButton;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, String> customerNameCol;
    @FXML private TableColumn<Order, LocalDate> orderDateCol;
    @FXML private TableColumn<Order, String> employeeNameCol;
    @FXML private TableColumn<Order, String> orderTypeCol;
    @FXML private TableColumn<Order, String> gameTitleCol;
    @FXML private TableColumn<Order, Double> totalAmountCol;
    private ObservableList<Order> orderList = FXCollections.observableArrayList();

    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        fetchOrders();
    }

    // Set up the order and payment ComboBoxes
    private void setupComboBoxes() {
        orderTypeCombo.setItems(FXCollections.observableArrayList("Purchase", "Rental"));
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Debit Card", "Cash", "Paypal", "Venmo"));
    }

    // Set up the TableView columns
    private void setupTableColumns() {
        orderIdCol.setCellValueFactory(data -> data.getValue().orderIdProperty().asObject());
        customerNameCol.setCellValueFactory(data -> data.getValue().customerNameProperty());
        orderDateCol.setCellValueFactory(data -> data.getValue().orderDateProperty());
        employeeNameCol.setCellValueFactory(data -> data.getValue().employeeNameProperty());
        orderTypeCol.setCellValueFactory(data -> data.getValue().orderTypeProperty());
        gameTitleCol.setCellValueFactory(data -> data.getValue().gameTitleProperty());
        totalAmountCol.setCellValueFactory(data -> data.getValue().totalAmountProperty().asObject());
    }

    // Queries the OrderInformation view to supply the ordersTable with all order records and their information
    private void fetchOrders() {
        orderList.clear();
        String sql = "SELECT * FROM OrderInformation";  // Using the OrderInformation view
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getString("employee_name"),
                        rs.getString("order_type"),
                        rs.getString("game_title"),
                        rs.getDouble("total_amount")
                );
                orderList.add(order);
            }
            ordersTable.setItems(orderList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHub() {
        try {
            App.setRoot("GameVaultHub");  
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }

    // Calls the InsertOrderWithDetails stored procedure within the database to insert a new order and update the database's tables accordingly
    @FXML
    private void insertOrder() {
        String sql = "{CALL InsertOrderWithDetails(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseUtil.getConnection(); 
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, Integer.parseInt(customerIdField.getText()));
            cs.setInt(2, Integer.parseInt(employeeIdField.getText()));
            cs.setString(3, orderTypeCombo.getValue());
            cs.setString(4, paymentMethodCombo.getValue());
            cs.setDate(5, Date.valueOf(orderDatePicker.getValue()));
            cs.setInt(6, Integer.parseInt(gameIdField.getText()));
            cs.setInt(7, Integer.parseInt(quantityField.getText()));
            cs.execute();
            fetchOrders(); // Refresh the TableView with the new data
        } catch (SQLException e) {
            handleDatabaseError(e);
        } catch (NumberFormatException | NullPointerException e) {
            showErrorDialog("Please ensure all fields are filled out correctly.");
        }
    }

    // Handles errors that have been sent directly from the database
    private void handleDatabaseError(SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            showErrorDialog(e.getMessage());
        } else {
            e.printStackTrace();
            showErrorDialog("A database error occurred. Please check the input or try again later.");
        }
    }

    // Display an error dialog with a custom message
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("An error occurred while inserting the order");
        alert.setContentText(message);
        // Using USE_PREF_SIZE so that the alert pop up doesn't get cut off by its size
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); 
        alert.showAndWait();
    }
}