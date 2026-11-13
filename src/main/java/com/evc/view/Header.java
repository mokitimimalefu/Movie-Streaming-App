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

public class Header extends HBox {
    public Header() {
        this.getStyleClass().add("header");
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(15, 30, 15, 30));
        this.setSpacing(30);

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoText = new Label("EVC");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logoText.getStyleClass().add("header-logo-text");
        Label subText = new Label("E-Video Cloud");
        subText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        subText.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(logoText, subText);

        HBox searchBox = new HBox();
        searchBox.getStyleClass().add("header-search-box");
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setMaxWidth(400);
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        Label searchIcon = new Label("🔍");
        searchIcon.setTextFill(Color.web("#3b82f6"));
        searchIcon.setCursor(Cursor.HAND);
        searchBox.getChildren().addAll(searchField, searchIcon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(20);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);
        Label bellIcon = new Label("🔔");
        bellIcon.setTextFill(Color.web("#a0a0a0"));
        bellIcon.setCursor(Cursor.HAND);
        Label bookmarkIcon = new Label("🔖");
        bookmarkIcon.setTextFill(Color.web("#a0a0a0"));
        bookmarkIcon.setCursor(Cursor.HAND);
        rightIcons.getChildren().addAll(bellIcon, bookmarkIcon);

        this.getChildren().addAll(logoBox, searchBox, spacer, rightIcons);
    }
}