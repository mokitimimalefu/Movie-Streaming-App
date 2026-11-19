package com.evc.view;

import com.evc.model.Movie;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FeaturedMovies extends FlowPane {
    private VideoPlayer videoPlayer;

    public FeaturedMovies(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
        this.setHgap(16);
        this.setVgap(16);
        this.setPadding(new Insets(20, 0, 20, 0));
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWrapLength(900);

        Movie f1 = new Movie("Pacific Rim", "2013", "Action", "/com/evc/images/posters/pacific-rim.jpg", "5GUYJT9YDZk");
        Movie f2 = new Movie("The Batman", "2022", "Action", "/com/evc/images/posters/batman.jpg", "mqqft2x_Aa4");
        StackPane first = createBanner(f1);
        StackPane second = createBanner(f2);
        this.getChildren().addAll(first, second);
        this.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            double bannerWidth = width < 760
                    ? Math.max(280, width)
                    : Math.max(280, (width - 16) / 2);
            first.setPrefWidth(bannerWidth);
            second.setPrefWidth(bannerWidth);
        });
    }

    private StackPane createBanner(Movie movie) {
        StackPane banner = new StackPane();
        banner.getStyleClass().add("featured-banner");
        banner.setPrefHeight(280);
        banner.setMinHeight(220);
        banner.setCursor(Cursor.HAND);

        ImageView bgImage = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(movie.getImagePath()));
            bgImage.setImage(img);
            bgImage.fitWidthProperty().bind(banner.widthProperty());
            bgImage.setFitHeight(280);
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.widthProperty().bind(banner.widthProperty());
            clip.heightProperty().bind(banner.heightProperty());
            clip.setArcWidth(30); clip.setArcHeight(30);
            bgImage.setClip(clip);
        } catch (Exception e) {}

        Region gradient = new Region();
        gradient.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(0,0,0,0.2) 50%, transparent 100%); -fx-background-radius: 15;");

        VBox content = new VBox(10);
        content.setAlignment(Pos.BOTTOM_LEFT);
        content.setPadding(new Insets(20));
        content.setMaxWidth(500);

        Label title = new Label(movie.getTitle());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.getStyleClass().add("featured-title");

        Label info = new Label(movie.getYear() + "  ⭐ 8.5");
        info.getStyleClass().add("featured-info");

        IconButton watchBtn = new IconButton("/com/evc/images/icons/play.png", "Watch", true);
        watchBtn.setOnMouseClicked(event -> {
            videoPlayer.playVideo(movie);
            event.consume();
        });

        content.getChildren().addAll(title, info, watchBtn);
        StackPane.setAlignment(content, Pos.BOTTOM_LEFT);

        banner.getChildren().addAll(bgImage, gradient, content);
        banner.setOnMouseClicked(e -> videoPlayer.playVideo(movie));
        return banner;
    }
}