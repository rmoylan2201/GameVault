package com.mycompany.databasefinal;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Order {

    private final IntegerProperty orderId;
    private final StringProperty customerName;
    private final ObjectProperty<LocalDate> orderDate;
    private final StringProperty employeeName;
    private final StringProperty orderType;
    private final StringProperty gameTitle;
    private final DoubleProperty totalAmount;

    public Order(int orderId, String customerName, LocalDate orderDate, String employeeName, String orderType, String gameTitle, double totalAmount) {
        this.orderId = new SimpleIntegerProperty(orderId);
        this.customerName = new SimpleStringProperty(customerName);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.orderType = new SimpleStringProperty(orderType);
        this.gameTitle = new SimpleStringProperty(gameTitle);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
    }

    // Property Getters
    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public ObjectProperty<LocalDate> orderDateProperty() {
        return orderDate;
    }

    public StringProperty employeeNameProperty() {
        return employeeName;
    }

    public StringProperty orderTypeProperty() {
        return orderType;
    }

    public StringProperty gameTitleProperty() {
        return gameTitle;
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }
}
