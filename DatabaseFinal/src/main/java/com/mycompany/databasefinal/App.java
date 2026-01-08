package com.mycompany.databasefinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Set the Main Menu as the initial scene
        scene = new Scene(loadFXML("GameVaultHub"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Game Vault Management System"); // Set window title
        stage.show();
    }

    // This method allows you to change the scene dynamically (for scene transitions)
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Helper method to load FXML files based on the provided name
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Main entry point of the application
    public static void main(String[] args) {
        launch();
    }
}