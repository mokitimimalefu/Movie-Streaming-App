package com.evc;

import com.evc.model.MovieData;
import com.evc.view.*;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Map;
import com.evc.model.Movie;

public class MainController {
    private BorderPane mainLayout;
    private VideoPlayer videoPlayer;

    public StackPane getRootLayout() {
        StackPane absoluteRoot = new StackPane();
        absoluteRoot.setStyle("-fx-background-color: #141414;");

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #141414;");

        videoPlayer = new VideoPlayer();
        videoPlayer.setVisible(false);

        mainLayout.setTop(new Header());

        // Create the sidebar and tell it what to do when an item is clicked
        Sidebar sidebar = new Sidebar();
        sidebar.setOnMenuItemClicked(this::showScreen);
        mainLayout.setLeft(sidebar);

        // Load the default screen (Categories)
        showScreen("Categories");

        absoluteRoot.getChildren().addAll(mainLayout, videoPlayer);
        return absoluteRoot;
    }

    private void showScreen(String screenName) {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(0, 0, 50, 0));

        Map<String, List<Movie>> categories = MovieData.getCategorizedMovies();

        if (screenName.equals("Categories") || screenName.equals("Home")) {
            // Show everything
            contentBox.getChildren().add(new FeaturedMovies(videoPlayer));
            for (Map.Entry<String, List<Movie>> entry : categories.entrySet()) {
                contentBox.getChildren().add(new CategoryBar(entry.getKey(), entry.getValue(), videoPlayer));
            }
        } else if (screenName.equals("Trending")) {
            // Show Action movies as "Trending"
            List<Movie> actions = categories.get("Action");
            if (actions != null) {
                contentBox.getChildren().add(new CategoryBar("Trending Now", actions, videoPlayer));
            }
        } else {
            // Placeholder for Coming Soon, History, Saved, etc.
            Label placeholder = new Label(screenName + " section is currently empty.");
            placeholder.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 20px; -fx-padding: 50px;");
            contentBox.getChildren().add(placeholder);
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // This replaces the center content!
        mainLayout.setCenter(scrollPane);
    }
}