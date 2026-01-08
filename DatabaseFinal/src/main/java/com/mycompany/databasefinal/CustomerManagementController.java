package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerManagementController {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> fNameColumn;
    @FXML
    private TableColumn<Customer, String> lNameColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;
    @FXML
    private TableColumn<Customer, Boolean> isMemberColumn;

    @FXML
    private TextField fNameField, lNameField, emailField;
    @FXML
    private CheckBox isMemberCheckBox;
    @FXML
    private Button addButton, updateButton, deleteButton, backButton;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        idColumn.setCellValueFactory(data -> data.getValue().customerIdProperty().asObject());
        fNameColumn.setCellValueFactory(data -> data.getValue().firstNameProperty());
        lNameColumn.setCellValueFactory(data -> data.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        isMemberColumn.setCellValueFactory(data -> data.getValue().isMemberProperty());
        isMemberColumn.setCellFactory(column -> new TableCell<Customer, Boolean>() {
            // Using an anonymous inner class to override updateItem method so that a customer's membership status is visually seen as either "Active" or "Inactive" instead of TRUE/FALSE
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item ? "Active" : "Inactive");
            }
        });
        loadCustomers();
    }
    
    // Queries the Customers table from the DB for all of its information, using it to fill the table of the UI
    private void loadCustomers() {
        customerList.clear();
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Customers")) {
            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getBoolean("is_member")
                );
                customerList.add(c);
            }
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Calls the AddCustomer stored procedure within the DB to add a new customer to the Customer DB table, then refreshes the UI table
    @FXML
    private void handleAdd(ActionEvent event) {
        String fName = fNameField.getText();
        String lName = lNameField.getText();
        String email = emailField.getText();
        boolean isMember = isMemberCheckBox.isSelected();
        String call = "{ CALL AddCustomer(?, ?, ?, ?) }";
        try (Connection conn = DatabaseUtil.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, email);
            stmt.setBoolean(4, isMember);
            stmt.execute();
            loadCustomers();
            customerTable.refresh();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Queries the database to find and delete any customer with the matching customer id that was selected
    @FXML
    private void handleDelete(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM Customers WHERE customer_id = ?";
            try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selected.getCustomerId());
                stmt.executeUpdate();
                loadCustomers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Updates the selected customer records within the Customers DB table with the frontend inputs
    @FXML
    private void handleUpdate(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        String fName = fNameField.getText();
        String lName = lNameField.getText();
        String email = emailField.getText();
        boolean isMember = isMemberCheckBox.isSelected();
        String sql = "UPDATE Customers SET first_name = ?, last_name = ?, email = ?, is_member = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, email);
            stmt.setBoolean(4, isMember);
            stmt.setInt(5, selected.getCustomerId());
            stmt.executeUpdate();
            loadCustomers();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Simply clears the TextFields within the UI
    private void clearForm() {
        fNameField.clear();
        lNameField.clear();
        emailField.clear();
        isMemberCheckBox.setSelected(false);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            App.setRoot("GameVaultHub"); // Navigate back to the main menu scene
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
