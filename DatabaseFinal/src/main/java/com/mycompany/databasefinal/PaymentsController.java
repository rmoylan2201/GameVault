package com.mycompany.databasefinal;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PaymentsController {

    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, Integer> paymentIdColumn;
    @FXML
    private TableColumn<Payment, LocalDate> dateColumn;
    @FXML
    private TableColumn<Payment, String> methodColumn;
    @FXML
    private TableColumn<Payment, Double> amountColumn;
    @FXML
    private TableColumn<Payment, Integer> orderIdColumn;
    @FXML
    private PieChart paymentPieChart;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Label totalPaymentsLabel;
    @FXML
    private Label totalCountLabel;

    private final ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadPaymentsFromDatabase();
    }

    private void setupTableColumns() {
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
    }

    // Loads all payments from the database and populates the TableView and PieChart. Displays an error alert if the database connection or query fails
    private void loadPaymentsFromDatabase() {
        String query = "SELECT payment_id, payment_date, payment_method, amount_paid, order_id FROM payments";
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            paymentList.clear();
            while (rs.next()) {
                paymentList.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getDate("payment_date").toLocalDate(),
                        rs.getString("payment_method"),
                        rs.getDouble("amount_paid"),
                        rs.getInt("order_id")
                ));
            }
            paymentTable.setItems(paymentList);
            updatePieChart(paymentList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load payments from database.");
        }
    }
    
    // Filters the payment data based on the selected date range from the DatePickers. After validating the input dates, updates the TableView and PieChart with the filtered results.
    @FXML
    private void applyDateFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates", "Please select both a start and end date.");
            return;
        }
        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Date Range", "End date must be after or equal to start date.");
            return;
        }
        // Steaming the results of the filter into a ObservableList 
        ObservableList<Payment> filtered = paymentList.stream()
                .filter(payment -> !payment.getPaymentDate().isBefore(startDate) && !payment.getPaymentDate().isAfter(endDate))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        paymentTable.setItems(filtered);
        updatePieChart(filtered);
    }
    
    //Updates the PieChart to show distribution of payment methods. Also calculates and displays the total payment amount and number of transactions.
    private void updatePieChart(ObservableList<Payment> data) {
        // Streams by payment method and count occurrences (frequency of use)
        Map<String, Long> methodCounts = data.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.counting()));
        // Streams by payment method and sums the amounts (for the total spent)
        Map<String, Double> methodSums = data.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.summingDouble(Payment::getAmountPaid)));
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        double totalAmount = 0; // For total amount spent
        long totalCount = 0;    // For total count of payments
        // Loop through each payment method, adding all payment methods and the number of times they have been used into the piechart
        for (Map.Entry<String, Long> entry : methodCounts.entrySet()) {
            String method = entry.getKey();
            long count = entry.getValue();
            double totalSpent = methodSums.get(method);
            chartData.add(new PieChart.Data(method, count)); 
            totalAmount += totalSpent; // Sum of total amount spent across all methods
        }
        paymentPieChart.setData(chartData);
        // Updates the labels above the pie chart
        totalPaymentsLabel.setText(String.format("Total Payments: $%.2f", totalAmount));
        totalCountLabel.setText(String.format("Number of Payments: %d", data.size()));
    }
    
    // Displays a pop up alert with the issued type, title, and content
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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
