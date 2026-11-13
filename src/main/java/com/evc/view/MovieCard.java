package com.evc.view;

import com.evc.model.Movie;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

public class MovieCard extends VBox {
    private static Map<String, AudioClip> categorySounds = new HashMap<>();
    private static AudioClip clickSound;

    public MovieCard(Movie movie, VideoPlayer videoPlayer) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5);
        this.setPrefWidth(180);
        this.setCursor(Cursor.HAND);

        if (categorySounds.isEmpty()) {
            loadSound("Action", "/com/evc/sounds/action.mp3");
            loadSound("Comedy", "/com/evc/sounds/comedy.mp3");
            loadSound("Sci-Fi", "/com/evc/sounds/scifi.mp3");
            try {
                String clickPath = getClass().getResource("/com/evc/sounds/click.mp3").toExternalForm();
                clickSound = new AudioClip(clickPath);
            } catch (Exception e) { System.out.println("Click sound not found."); }
        }

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(260);
        imageView.setPreserveRatio(false);
        try {
            Image img = new Image(getClass().getResourceAsStream(movie.getImagePath()));
            imageView.setImage(img);
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 260);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: #333;");
        }

        Label title = new Label(movie.getTitle());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);
        title.getStyleClass().add("movie-card-title");

        this.getChildren().addAll(imageView, title);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), this);
        scaleUp.setToX(1.1); scaleUp.setToY(1.1);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), this);
        scaleDown.setToX(1.0); scaleDown.setToY(1.0);

        this.setOnMouseEntered(e -> {
            scaleUp.playFromStart();
            AudioClip s = categorySounds.get(movie.getCategory());
            if (s != null) s.play();
        });
        this.setOnMouseExited(e -> scaleDown.playFromStart());
        this.setOnMouseClicked(e -> {
            if (clickSound != null) clickSound.play();
            videoPlayer.playVideo(movie.getYoutubeEmbedUrl());
        });
    }

    private void loadSound(String name, String path) {
        try {
            String p = getClass().getResource(path).toExternalForm();
            categorySounds.put(name, new AudioClip(p));
        } catch (Exception e) { System.out.println("Sound not found: " + path); }
    }
}