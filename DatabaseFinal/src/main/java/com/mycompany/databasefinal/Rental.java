package com.mycompany.databasefinal;

import java.time.LocalDate;

public class Rental {
    private int rentalId;
    private int customerId;
    private String customerName;
    private int gameId;
    private String gameName;
    private LocalDate receivedDate;
    private LocalDate returnedDate;

    // Constructor for Active Rentals
    public Rental(int rentalId, int customerId, int gameId, LocalDate receivedDate) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.gameId = gameId;
        this.receivedDate = receivedDate;
        this.returnedDate = null;  // Default for active rentals
    }

    // Constructor for Rental History
    public Rental(int rentalId, String customerName, String gameName, LocalDate receivedDate, LocalDate returnedDate) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.gameName = gameName;
        this.receivedDate = receivedDate;
        this.returnedDate = returnedDate;
    }

    // Getters
    public int getRentalId() {
        return rentalId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getGameId() {
        return gameId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGameName() {
        return gameName;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    // Additional helper method to convert SQL Date to LocalDate 
    public static LocalDate convertSqlDateToLocalDate(java.sql.Date date) {
        return date != null ? date.toLocalDate() : null;
    }
}