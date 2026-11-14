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

        VBox topMenu = new VBox(10);
        topMenu.getChildren().addAll(
                createMenuItem("Home", false),
                createMenuItem("Trending", false),
                createMenuItem("Coming Soon", false),
                createMenuItem("Categories", true)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox libraryMenu = new VBox(10);
        libraryMenu.setPadding(new Insets(20, 0, 20, 0));
        libraryMenu.getChildren().addAll(
                createMenuItem("History", false),
                createMenuItem("Saved", false),
                createMenuItem("My Library", false),
                createMenuItem("Downloads", false)
        );

        VBox bottomMenu = new VBox(10);
        bottomMenu.getChildren().addAll(createMenuItem("Settings", false));

        this.getChildren().addAll(topMenu, libraryMenu, spacer, bottomMenu);
    }

    public void setOnMenuItemClicked(Consumer<String> callback) {
        this.onMenuItemClicked = callback;
    }

    private HBox createMenuItem(String text, boolean isActive) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 20, 10, 20));
        item.setCursor(Cursor.HAND);
        item.getStyleClass().add(isActive ? "sidebar-menu-item-active" : "sidebar-menu-item");

        Label icon = new Label("■");
        icon.getStyleClass().add(isActive ? "sidebar-icon-active" : "sidebar-icon-inactive");

        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        label.getStyleClass().add(isActive ? "sidebar-text-active" : "sidebar-text-inactive");

        Region activeLine = new Region();
        activeLine.setPrefWidth(4);
        activeLine.setStyle(isActive ? "-fx-background-color: #3b82f6;" : "-fx-background-color: transparent;");

        item.getChildren().addAll(activeLine, icon, label);

        // THIS IS THE NEW PART: Click Event
        item.setOnMouseClicked(e -> {
            if (onMenuItemClicked != null) {
                onMenuItemClicked.accept(text);
            }
        });

        return item;
    }
}