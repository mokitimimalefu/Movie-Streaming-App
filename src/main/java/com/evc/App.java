package com.evc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainController controller = new MainController();
        StackPane root = controller.getRootLayout();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/com/evc/styles.css").toExternalForm());

        primaryStage.setTitle("EVC - E-Video Cloud");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        // Request focus on the root so keyboard events are captured immediately
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}