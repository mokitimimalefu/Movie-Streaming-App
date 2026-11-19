package com.evc.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class Header extends HBox {
    private TextField searchField;
    private HBox logoBox;
    private HBox searchBox;
    private Label subText;

    public Header() {
        this(query -> { });
    }

    public Header(Consumer<String> onSearchChanged) {
        this.getStyleClass().add("header");
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(15, 30, 15, 30));
        this.setSpacing(30);

        logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoText = new Label("EVC");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logoText.getStyleClass().add("header-logo-text");
        subText = new Label("E-Video Cloud");
        subText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        subText.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(logoText, subText);

        searchBox = new HBox();
        searchBox.getStyleClass().add("header-search-box");
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(400);
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                onSearchChanged.accept(newValue));
        searchField.setOnAction(event -> onSearchChanged.accept(searchField.getText()));
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Label searchIcon = new Label("🔍");
        searchIcon.setTextFill(Color.web("#e50914"));
        searchIcon.setCursor(Cursor.HAND);
        searchIcon.setOnMouseClicked(event -> onSearchChanged.accept(searchField.getText()));
        searchBox.getChildren().addAll(searchField, searchIcon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        

        this.getChildren().addAll(logoBox, searchBox, spacer);
    }

    public void clearSearch() {
        searchField.clear();
    }

    public void setCompact(boolean compact) {
        subText.setVisible(!compact);
        subText.setManaged(!compact);
        this.setPadding(compact
                ? new Insets(10, 16, 10, 16)
                : new Insets(15, 30, 15, 30));
        this.setSpacing(compact ? 12 : 30);
        searchBox.setMaxWidth(compact ? 280 : 500);
    }
}