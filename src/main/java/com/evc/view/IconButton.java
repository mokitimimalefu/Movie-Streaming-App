package com.evc.view;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class IconButton extends HBox {

    // =====================================================
    // 1. NORMAL VERSION - Dark pill, used in VideoPlayer
    //    PLAY / PAUSE / CLOSE at the bottom
    // =====================================================
    public IconButton(String iconPath, String text) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(8);
        this.setCursor(Cursor.HAND);
        this.getStyleClass().add("icon-btn");

        ImageView icon = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(iconPath));
            icon.setImage(img);
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            icon.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.getStyleClass().add("icon-btn-text");

        this.getChildren().addAll(icon, label);

        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #e50914; -fx-background-radius: 20;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 20;"));
    }

    // =====================================================
    // 2. SMALL VERSION - White pill, used in FeaturedMovies
    //    Small ▶ Watch button on top 2 movies, on the LEFT
    //    Usage: new IconButton("/com/evc/images/icons/play.png", "Watch", true)
    // =====================================================
    public IconButton(String iconPath, String text, boolean isSmall) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(6);
        this.setCursor(Cursor.HAND);
        // Small white pill like Netflix
        this.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 6 14 6 14;");

        ImageView icon = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(iconPath));
            icon.setImage(img);
            icon.setFitWidth(14);
            icon.setFitHeight(14);
            icon.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setStyle("-fx-text-fill: black;");

        this.getChildren().addAll(icon, label);

        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 20; -fx-padding: 6 14 6 14;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 6 14 6 14;"));
    }
}