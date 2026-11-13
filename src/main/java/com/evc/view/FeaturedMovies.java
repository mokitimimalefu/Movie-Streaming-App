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

public class FeaturedMovies extends HBox {
    private VideoPlayer videoPlayer;

    public FeaturedMovies(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
        this.setSpacing(20);
        this.setPadding(new Insets(20, 40, 20, 40));
        this.setAlignment(Pos.CENTER_LEFT);

        Movie f1 = new Movie("Pacific Rim", "2013", "Action", "/com/evc/images/posters/pacific-rim.jpg", "5GUYJT9YDZk");
        Movie f2 = new Movie("The Batman", "2022", "Action", "/com/evc/images/posters/batman.jpg", "mqqft2x_Aa4");
        this.getChildren().addAll(createBanner(f1), createBanner(f2));
    }

    private StackPane createBanner(Movie movie) {
        StackPane banner = new StackPane();
        banner.getStyleClass().add("featured-banner");
        banner.setPrefSize(500, 280);
        banner.setCursor(Cursor.HAND);

        ImageView bgImage = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(movie.getImagePath()));
            bgImage.setImage(img);
            bgImage.setFitWidth(500);
            bgImage.setFitHeight(280);
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(500, 280);
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

        // SMALL button, on the LEFT, like your original
        IconButton watchBtn = new IconButton("/com/evc/images/icons/play.png", "Watch", true);
        // Align it left
        HBox btnRow = new HBox(watchBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(title, info, btnRow);
        StackPane.setAlignment(content, Pos.BOTTOM_LEFT);

        banner.getChildren().addAll(bgImage, gradient, content);
        banner.setOnMouseClicked(e -> videoPlayer.playVideo(movie.getYoutubeEmbedUrl()));
        return banner;
    }
}