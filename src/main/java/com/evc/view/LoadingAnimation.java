package com.evc.view;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class LoadingAnimation extends StackPane {
    private Label statusLabel;

    public LoadingAnimation() {
        this.getStyleClass().add("loading-bg");
        this.setAlignment(Pos.CENTER);

        Arc spinner = new Arc(0, 0, 45, 45, 90, 270);
        spinner.setFill(Color.TRANSPARENT);
        spinner.setStroke(Color.web("#e50914"));
        spinner.setStrokeWidth(6);
        spinner.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        RotateTransition rotate = new RotateTransition(Duration.millis(900), spinner);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();

        Label logo = new Label("EVC");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        StackPane spinnerBox = new StackPane(spinner, logo);

        HBox dots = new HBox(8);
        dots.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            Circle dot = new Circle(6, Color.WHITE);
            dots.getChildren().add(dot);
            TranslateTransition bounce = new TranslateTransition(Duration.millis(400), dot);
            bounce.setFromY(0);
            bounce.setToY(-12);
            bounce.setCycleCount(Animation.INDEFINITE);
            bounce.setAutoReverse(true);
            bounce.setDelay(Duration.millis(i * 130));
            bounce.play();
        }

        statusLabel = new Label("Loading trailer from YouTube...");
        statusLabel.setTextFill(Color.LIGHTGRAY);
        statusLabel.setFont(Font.font("Segoe UI", 14));

        VBox box = new VBox(20, spinnerBox, dots, statusLabel);
        box.setAlignment(Pos.CENTER);
        this.getChildren().add(box);
    }

    public void reset() { statusLabel.setText("Loading trailer from YouTube..."); }
    public void showError() { statusLabel.setText("Failed to load. Check internet and try again."); }
}