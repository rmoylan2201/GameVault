package com.mycompany.databasefinal;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Payment {

    private final IntegerProperty paymentId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();
    private final StringProperty paymentMethod = new SimpleStringProperty();
    private final DoubleProperty amountPaid = new SimpleDoubleProperty();
    private final IntegerProperty orderId = new SimpleIntegerProperty();

    // Constructor
    public Payment(int paymentId, LocalDate paymentDate, String paymentMethod, double amountPaid, int orderId) {
        setPaymentId(paymentId);
        setPaymentDate(paymentDate);
        setPaymentMethod(paymentMethod);
        setAmountPaid(amountPaid);
        setOrderId(orderId);
    }

    // Payment ID
    public int getPaymentId() {
        return paymentId.get();
    }

    public void setPaymentId(int value) {
        paymentId.set(value);
    }

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDate value) {
        paymentDate.set(value);
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String value) {
        paymentMethod.set(value);
    }

    public double getAmountPaid() {
        return amountPaid.get();
    }

    public void setAmountPaid(double value) {
        amountPaid.set(value);
    }

    public int getOrderId() {
        return orderId.get();
    }

    public void setOrderId(int value) {
        orderId.set(value);
    }
}
