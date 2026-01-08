module com.mycompany.databasefinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;


    opens com.mycompany.databasefinal to javafx.fxml;
    exports com.mycompany.databasefinal;
}
