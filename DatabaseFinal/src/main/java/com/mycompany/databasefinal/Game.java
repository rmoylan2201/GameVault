package com.mycompany.databasefinal;

import javafx.beans.property.*;

public class Game {
    private IntegerProperty gameId;
    private StringProperty title;
    private StringProperty genre;
    private IntegerProperty year;
    private StringProperty platform;
    private IntegerProperty stockQuantity;
    private DoubleProperty price;
    private IntegerProperty sales;

    // Default constructor
    public Game() {
        this.gameId = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.genre = new SimpleStringProperty();
        this.year = new SimpleIntegerProperty();
        this.platform = new SimpleStringProperty();
        this.stockQuantity = new SimpleIntegerProperty();
        this.price = new SimpleDoubleProperty();
        this.sales = new SimpleIntegerProperty();
    }

    // Constructor with parameters
    public Game(int gameId, String title, String genre, int year, String platform, int stockQuantity, double price, int sales) {
        this.gameId = new SimpleIntegerProperty(gameId);
        this.title = new SimpleStringProperty(title);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleIntegerProperty(year);
        this.platform = new SimpleStringProperty(platform);
        this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        this.price = new SimpleDoubleProperty(price);
        this.sales = new SimpleIntegerProperty(sales);
    }

    // Getters for Properties
    public IntegerProperty getGameIdProperty() {
        return gameId;
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public StringProperty getGenreProperty() {
        return genre;
    }

    public IntegerProperty getYearProperty() {
        return year;
    }

    public StringProperty getPlatformProperty() {
        return platform;
    }

    public IntegerProperty getStockQuantityProperty() {
        return stockQuantity;
    }

    public DoubleProperty getPriceProperty() {
        return price;
    }

    public IntegerProperty getSalesProperty() {
        return sales;
    }
}