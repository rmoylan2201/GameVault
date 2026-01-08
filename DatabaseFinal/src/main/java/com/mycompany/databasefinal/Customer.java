package com.mycompany.databasefinal;
import javafx.beans.property.*;

public class Customer {

    private final IntegerProperty customerId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final BooleanProperty isMember;

    public Customer(int customerId, String firstName, String lastName, String email, boolean isMember) {
        this.customerId = new SimpleIntegerProperty(customerId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.isMember = new SimpleBooleanProperty(isMember);
    }

    // Property Getters (used by TableView)
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public BooleanProperty isMemberProperty() {
        return isMember;
    }

    // Regular Getters
    public int getCustomerId() {
        return customerId.get();
    }

    public String getFirstName() {
        return firstName.get();
    }
    
    public String getLastName() {
        return lastName.get();
    }
    
    public String getEmail() {
        return email.get();
    }
    
    public boolean getIsMember() {
        return isMember.get();
    }
}