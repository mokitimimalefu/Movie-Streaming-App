package com.evc;

import com.evc.model.MovieData;
import com.evc.view.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import com.evc.model.Movie;

public class MainController {
    private BorderPane mainLayout;
    private VideoPlayer videoPlayer;
    private String currentScreen = "Categories";
    private String searchQuery = "";

    public StackPane getRootLayout() {
        StackPane absoluteRoot = new StackPane();
        absoluteRoot.setStyle("-fx-background-color: #141414;");

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #141414;");

        videoPlayer = new VideoPlayer();
        videoPlayer.setVisible(false);

        mainLayout.setTop(new Header(query -> {
            searchQuery = query == null ? "" : query.trim();
            renderCurrentScreen();
        }));

        Sidebar sidebar = new Sidebar();
        sidebar.setOnMenuItemClicked(this::showScreen);
        mainLayout.setLeft(sidebar);

        showScreen("Categories");

        absoluteRoot.getChildren().addAll(mainLayout, videoPlayer);
        return absoluteRoot;
    }

    private void showScreen(String screenName) {
        currentScreen = screenName;
        renderCurrentScreen();
    }

    private void renderCurrentScreen() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(0, 0, 50, 40));

        Map<String, List<Movie>> categories = MovieData.getCategorizedMovies();

        if (!searchQuery.isEmpty()) {
            Map<String, List<Movie>> matchingCategories = findMatchingMovies(categories, searchQuery);
            if (matchingCategories.isEmpty()) {
                Label noResults = new Label("No movies found for \"" + searchQuery + "\".");
                noResults.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 50px;");
                contentBox.getChildren().add(noResults);
            } else {
                for (Map.Entry<String, List<Movie>> entry : matchingCategories.entrySet()) {
                    contentBox.getChildren().add(
                            new CategoryBar(entry.getKey(), entry.getValue(), videoPlayer)
                    );
                }
            }
        }
        else if (currentScreen.equals("Home") || currentScreen.equals("Categories")) {
            contentBox.getChildren().add(new FeaturedMovies(videoPlayer));
            for (Map.Entry<String, List<Movie>> entry : categories.entrySet()) {
                contentBox.getChildren().add(
                    new CategoryBar(entry.getKey(), entry.getValue(), videoPlayer)
                );
            }
        }
        else if (currentScreen.equals("Trending")) {
            List<Movie> actions = categories.get("Action & Adventure");
            
            if (actions != null) {
                contentBox.getChildren().add(
                    new CategoryBar("Trending Now", actions, videoPlayer)
                );
            } else {
                Label error = new Label("Could not find category 'Action & Adventure' — check your MovieData.java key name!");
                error.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px; -fx-padding: 50px;");
                contentBox.getChildren().add(error);
            }
        }
        else if (currentScreen.equals("Coming Soon")) {
            Label title = new Label("Coming Soon");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
            Label desc = new Label("New movies and trailers will be added here soon.\n\nCheck back later for updates.");
            desc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
            contentBox.getChildren().addAll(title, desc);
        }
        else if (currentScreen.equals("History")) {
            Label title = new Label("Watch History");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
            Label desc = new Label("Movies you've watched will appear here.");
            desc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
            contentBox.getChildren().addAll(title, desc);
        }
        else if (currentScreen.equals("Saved")) {
            Label title = new Label("My List");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
            Label desc = new Label("Save movies to watch later — they will show up here.");
            desc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
            contentBox.getChildren().addAll(title, desc);
        }
        else if (currentScreen.equals("My Library")) {
            Label title = new Label("My Library");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
            Label desc = new Label("Your personal collection of favorite movies.");
            desc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
            contentBox.getChildren().addAll(title, desc);
        }
        else if (currentScreen.equals("Downloads")) {
            Label title = new Label("Downloads");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold;");
            Label desc = new Label("Downloaded movie trailers will appear here.");
            desc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
            contentBox.getChildren().addAll(title, desc);
        }
        else if (currentScreen.equals("Settings")) {
            Label title = new Label("Settings");
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 0 0 30 0;");

            VBox settingsBox = new VBox(20);
            settingsBox.setMaxWidth(600);

            Label section1 = new Label("Appearance");
            section1.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

            HBox darkModeRow = new HBox(15);
            darkModeRow.setAlignment(Pos.CENTER_LEFT);
            darkModeRow.setPadding(new Insets(12, 15, 12, 15));
            darkModeRow.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 6; -fx-cursor: hand;");

            Label darkModeLabel = new Label("Dark Mode");
            darkModeLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

            Label darkModeValue = new Label(AppSettings.isDarkMode() ? "Enabled" : "Disabled");
            darkModeValue.setStyle("-fx-text-fill: " + (AppSettings.isDarkMode() ? "#22c55e" : "#888888") + "; -fx-font-size: 14px; -fx-font-weight: bold;");

            Region darkSpacer = new Region();
            HBox.setHgrow(darkSpacer, Priority.ALWAYS);

            darkModeRow.getChildren().addAll(darkModeLabel, darkSpacer, darkModeValue);

            darkModeRow.setOnMouseClicked(e -> {
                AppSettings.setDarkMode(!AppSettings.isDarkMode());
                if (AppSettings.isDarkMode()) {
                    darkModeValue.setText("Enabled");
                    darkModeValue.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
                } else {
                    darkModeValue.setText("Disabled");
                    darkModeValue.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
            });

            HBox langRow = new HBox(15);
            langRow.setAlignment(Pos.CENTER_LEFT);
            langRow.setPadding(new Insets(12, 15, 12, 15));
            langRow.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 6;");

            Label langLabel = new Label("Language");
            langLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

            Label langValue = new Label(AppSettings.getLanguage());
            langValue.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");

            Region langSpacer = new Region();
            HBox.setHgrow(langSpacer, Priority.ALWAYS);

            langRow.getChildren().addAll(langLabel, langSpacer, langValue);

            Label section2 = new Label("Playback");
            section2.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 20 0 5 0;");

            HBox autoPlayRow = new HBox(15);
            autoPlayRow.setAlignment(Pos.CENTER_LEFT);
            autoPlayRow.setPadding(new Insets(12, 15, 12, 15));
            autoPlayRow.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 6; -fx-cursor: hand;");

            Label autoPlayLabel = new Label("Auto-play next video");
            autoPlayLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

            Label autoPlayValue = new Label(AppSettings.isAutoPlay() ? "On" : "Off");
            autoPlayValue.setStyle("-fx-text-fill: " + (AppSettings.isAutoPlay() ? "#22c55e" : "#888888") + "; -fx-font-size: 14px; -fx-font-weight: bold;");

            Region autoSpacer = new Region();
            HBox.setHgrow(autoSpacer, Priority.ALWAYS);

            autoPlayRow.getChildren().addAll(autoPlayLabel, autoSpacer, autoPlayValue);

            autoPlayRow.setOnMouseClicked(e -> {
                AppSettings.setAutoPlay(!AppSettings.isAutoPlay());
                if (AppSettings.isAutoPlay()) {
                    autoPlayValue.setText("On");
                    autoPlayValue.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
                } else {
                    autoPlayValue.setText("Off");
                    autoPlayValue.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
            });

            HBox qualityRow = new HBox(15);
            qualityRow.setAlignment(Pos.CENTER_LEFT);
            qualityRow.setPadding(new Insets(12, 15, 12, 15));
            qualityRow.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 6; -fx-cursor: hand;");

            Label qualityLabel = new Label("Video Quality");
            qualityLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

            Label qualityValue = new Label(AppSettings.getVideoQuality());
            qualityValue.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");

            Region qualitySpacer = new Region();
            HBox.setHgrow(qualitySpacer, Priority.ALWAYS);

            qualityRow.getChildren().addAll(qualityLabel, qualitySpacer, qualityValue);

            String[] qualities = {"Auto", "1080p", "720p", "480p", "360p"};
            qualityRow.setOnMouseClicked(e -> {
                String current = AppSettings.getVideoQuality();
                int idx = 0;
                for (int i = 0; i < qualities.length; i++) {
                    if (qualities[i].equals(current)) { idx = (i + 1) % qualities.length; break; }
                }
                AppSettings.setVideoQuality(qualities[idx]);
                qualityValue.setText(qualities[idx]);
            });

            settingsBox.getChildren().addAll(
                section1, darkModeRow, langRow,
                section2, autoPlayRow, qualityRow
            );
            contentBox.getChildren().addAll(title, settingsBox);
        }
        else {
            Label unknown = new Label(currentScreen + "\n\nThis page is coming soon.");
            unknown.setStyle("-fx-text-fill: #888888; -fx-font-size: 18px; -fx-padding: 50px;");
            contentBox.getChildren().add(unknown);
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainLayout.setCenter(scrollPane);
    }

    private Map<String, List<Movie>> findMatchingMovies(
            Map<String, List<Movie>> categories, String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        Map<String, List<Movie>> matches = new LinkedHashMap<>();

        for (Map.Entry<String, List<Movie>> category : categories.entrySet()) {
            List<Movie> matchingMovies = new ArrayList<>();
            for (Movie movie : category.getValue()) {
                if (movie.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || movie.getYear().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || movie.getCategory().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                    matchingMovies.add(movie);
                }
            }
            if (!matchingMovies.isEmpty()) {
                matches.put(category.getKey(), matchingMovies);
            }
        }
        return matches;
    }
}
