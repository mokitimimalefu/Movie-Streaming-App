package com.evc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends VBox {
    
    private Consumer<String> onMenuItemClicked;
    private final List<MenuItem> menuItems = new ArrayList<>();
    private MenuItem currentActiveItem;
    private boolean darkMode = true;

    public Sidebar() {
        this.getStyleClass().add("sidebar");
        this.setPrefWidth(220);
        this.setPadding(new Insets(20, 0, 20, 0));
        this.setStyle("-fx-background-color: #0f0f0f;");

        VBox topMenu = new VBox(10);
        topMenu.getChildren().addAll(
                createMenuItem("Home"),
                createMenuItem("Trending"),
                createMenuItem("Coming Soon"),
                createMenuItem("Categories")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox libraryMenu = new VBox(10);
        libraryMenu.setPadding(new Insets(20, 0, 20, 0));
        libraryMenu.getChildren().addAll(
                createMenuItem("History"),
                createMenuItem("Saved"),
                createMenuItem("My Library"),
                createMenuItem("Downloads")
        );

        VBox bottomMenu = new VBox(10);
        bottomMenu.getChildren().add(createMenuItem("Settings"));

        this.getChildren().addAll(topMenu, libraryMenu, spacer, bottomMenu);

        // Set the default active screen
        setActive("Categories");
    }

    public void setOnMenuItemClicked(Consumer<String> callback) {
        this.onMenuItemClicked = callback;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        this.setStyle("-fx-background-color: " + (darkMode ? "#0f0f0f" : "#e4e4e7") + ";");
        setActive(currentActiveItem == null ? "" : currentActiveItem.text);
    }

    private HBox createMenuItem(String text) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 20, 10, 20));
        item.setCursor(Cursor.HAND);
        item.setStyle("-fx-background-color: transparent;");

        Region activeLine = new Region();
        activeLine.setPrefWidth(4);
        activeLine.setPrefHeight(20);
        activeLine.setStyle("-fx-background-color: transparent;");

        Label icon = new Label("■");
        icon.setFont(Font.font(12));
        icon.setTextFill(Color.web("#777777"));

        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        label.setTextFill(Color.web("#aaaaaa"));

        item.getChildren().addAll(activeLine, icon, label);

        MenuItem menuItem = new MenuItem(text, item, activeLine, icon, label);
        menuItems.add(menuItem);

        // Hover effects
        item.setOnMouseEntered(e -> {
            if (menuItem != currentActiveItem) {
                item.setStyle(darkMode
                        ? "-fx-background-color: rgba(255,255,255,0.05);"
                        : "-fx-background-color: rgba(0,0,0,0.05);");
                label.setTextFill(darkMode ? Color.WHITE : Color.web("#171717"));
                icon.setTextFill(darkMode ? Color.web("#cccccc") : Color.web("#555555"));
            }
        });
        item.setOnMouseExited(e -> {
            if (menuItem != currentActiveItem) {
                item.setStyle("-fx-background-color: transparent;");
                label.setTextFill(Color.web(darkMode ? "#aaaaaa" : "#444444"));
                icon.setTextFill(Color.web("#777777"));
            }
        });

        // Click action
        item.setOnMouseClicked(e -> {
            setActive(text);
            if (onMenuItemClicked != null) {
                onMenuItemClicked.accept(text);
            }
        });

        return item;
    }

    // This method loops through all items, removes the blue, and adds it to the clicked one
    public void setActive(String text) {
        for (MenuItem mi : menuItems) {
            boolean isTarget = mi.text.equals(text);

            if (isTarget) {
                currentActiveItem = mi;
                mi.line.setStyle("-fx-background-color: #3b82f6;");
                mi.icon.setTextFill(Color.web("#3b82f6"));
                mi.label.setTextFill(darkMode ? Color.WHITE : Color.web("#171717"));
                mi.container.setStyle("-fx-background-color: transparent;"); // Clear hover
            } else {
                mi.line.setStyle("-fx-background-color: transparent;");
                mi.icon.setTextFill(Color.web("#777777"));
                mi.label.setTextFill(Color.web(darkMode ? "#aaaaaa" : "#444444"));
                mi.container.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    // Helper class to keep track of the UI elements for each menu item
    private static class MenuItem {
        String text;
        HBox container;
        Region line;
        Label icon;
        Label label;

        MenuItem(String text, HBox container, Region line, Label icon, Label label) {
            this.text = text;
            this.container = container;
            this.line = line;
            this.icon = icon;
            this.label = label;
        }
    }
}