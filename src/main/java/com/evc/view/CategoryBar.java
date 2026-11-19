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
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CategoryBar extends VBox {
    public CategoryBar(String categoryName, List<Movie> movies, VideoPlayer videoPlayer) {
        this(categoryName, movies, videoPlayer, ignored -> false, ignored -> { });
    }

    public CategoryBar(
            String categoryName,
            List<Movie> movies,
            VideoPlayer videoPlayer,
            Predicate<Movie> isSaved,
            Consumer<Movie> onSaveToggled) {
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 20, 0));

        Label title = new Label(categoryName);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.getStyleClass().add("category-title");

        HBox movieRow = new HBox(15);
        for (Movie movie : movies) {
            movieRow.getChildren().add(
                    new MovieCard(movie, videoPlayer, isSaved, onSaveToggled, movies));
        }

        ScrollPane scrollPane = new ScrollPane(movieRow);
        scrollPane.getStyleClass().add("category-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(0);
        scrollPane.setPrefViewportWidth(960);
        scrollPane.setPrefWidth(960);
        scrollPane.setMaxWidth(960);

        this.getChildren().addAll(title, scrollPane);
    }
}