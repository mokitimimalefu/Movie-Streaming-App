package com.evc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class Sidebar extends VBox {
    
    private Consumer<String> onMenuItemClicked;

    public Sidebar() {
        this.getStyleClass().add("sidebar");
        this.setPrefWidth(220);
        this.setPadding(new Insets(20, 0, 20, 0));
        this.setStyle("-fx-background-color: #0f0f0f;");

        // --- TOP MENU ---
        VBox topMenu = new VBox(10);
        topMenu.getChildren().addAll(
                createMenuItem("Home", false),
                createMenuItem("Trending", false),
                createMenuItem("Coming Soon", false),
                createMenuItem("Categories", true)
        );

        // Spacer — pushes bottom items down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // --- LIBRARY MENU ---
        VBox libraryMenu = new VBox(10);
        libraryMenu.setPadding(new Insets(20, 0, 20, 0));
        libraryMenu.getChildren().addAll(
                createMenuItem("History", false),
                createMenuItem("Saved", false),
                createMenuItem("My Library", false),
                createMenuItem("Downloads", false)
        );

        // --- BOTTOM MENU ---
        VBox bottomMenu = new VBox(10);
        bottomMenu.getChildren().add(createMenuItem("Settings", false));

        // Assemble sidebar
        this.getChildren().addAll(topMenu, libraryMenu, spacer, bottomMenu);
    }

    // Register callback for when items are clicked
    public void setOnMenuItemClicked(Consumer<String> callback) {
        this.onMenuItemClicked = callback;
    }

    // Create individual menu items
    private HBox createMenuItem(String text, boolean isActive) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 20, 10, 20));
        item.setCursor(Cursor.HAND);
        item.setStyle("-fx-background-color: transparent;");

        // Active indicator line
        Region activeLine = new Region();
        activeLine.setPrefWidth(4);
        activeLine.setPrefHeight(20);
        activeLine.setStyle(isActive ? "-fx-background-color: #3b82f6;" : "-fx-background-color: transparent;");

        // Icon
        Label icon = new Label("■");
        icon.setFont(Font.font(12));
        icon.setTextFill(isActive ? javafx.scene.paint.Color.web("#3b82f6") : javafx.scene.paint.Color.web("#777777"));

        // Text label
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        label.setTextFill(isActive ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.web("#aaaaaa"));

        item.getChildren().addAll(activeLine, icon, label);

        // Hover effect
        item.setOnMouseEntered(e -> {
            if (!isActive) {
                item.setStyle("-fx-background-color: rgba(255,255,255,0.05);");
                label.setTextFill(javafx.scene.paint.Color.WHITE);
                icon.setTextFill(javafx.scene.paint.Color.web("#cccccc"));
            }
        });
        item.setOnMouseExited(e -> {
            if (!isActive) {
                item.setStyle("-fx-background-color: transparent;");
                label.setTextFill(javafx.scene.paint.Color.web("#aaaaaa"));
                icon.setTextFill(javafx.scene.paint.Color.web("#777777"));
            }
        });

        // Click handler — send text back to MainController
        item.setOnMouseClicked(e -> {
            if (onMenuItemClicked != null) {
                onMenuItemClicked.accept(text);
            }
        });

        return item;
    }
}
