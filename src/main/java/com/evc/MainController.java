package com.evc;

import com.evc.model.MovieData;
import com.evc.view.*;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Map;
import com.evc.model.Movie;

public class MainController {
    public StackPane getRootLayout() {
        StackPane absoluteRoot = new StackPane();
        absoluteRoot.setStyle("-fx-background-color: #141414;");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #141414;");

        VideoPlayer videoPlayer = new VideoPlayer();
        videoPlayer.setVisible(false);

        mainLayout.setTop(new Header());
        mainLayout.setLeft(new Sidebar());

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(0, 0, 50, 0));
        contentBox.getChildren().add(new FeaturedMovies(videoPlayer));

        Map<String, List<Movie>> categories = MovieData.getCategorizedMovies();
        for (Map.Entry<String, List<Movie>> entry : categories.entrySet()) {
            contentBox.getChildren().add(new CategoryBar(entry.getKey(), entry.getValue(), videoPlayer));
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainLayout.setCenter(scrollPane);

        absoluteRoot.getChildren().addAll(mainLayout, videoPlayer);
        return absoluteRoot;
    }
}