package com.evc.view;

import com.evc.model.Movie;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class CategoryBar extends VBox {
    public CategoryBar(String categoryName, List<Movie> movies, VideoPlayer videoPlayer) {
        this.setSpacing(10);
        this.setPadding(new Insets(10, 40, 20, 40));

        Label title = new Label(categoryName);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.getStyleClass().add("category-title");

        HBox movieRow = new HBox(15);
        for (Movie movie : movies) {
            movieRow.getChildren().add(new MovieCard(movie, videoPlayer));
        }

        ScrollPane scrollPane = new ScrollPane(movieRow);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefWidth(960);
        scrollPane.setMaxWidth(960);

        this.getChildren().addAll(title, scrollPane);
    }
}