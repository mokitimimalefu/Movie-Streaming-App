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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import com.evc.model.Movie;

public class MainController {
    private BorderPane mainLayout;
    private StackPane rootLayout;
    private VideoPlayer videoPlayer;
    private Sidebar sidebar;
    private Header header;
    private String currentScreen = "Home";
    private String searchQuery = "";
    private final Map<String, Movie> watchHistory = new LinkedHashMap<>();
    private final Map<String, Movie> savedMovies = new LinkedHashMap<>();

    public StackPane getRootLayout() {
        StackPane absoluteRoot = new StackPane();
        rootLayout = absoluteRoot;

        mainLayout = new BorderPane();

        videoPlayer = new VideoPlayer();
        videoPlayer.setVisible(false);
        videoPlayer.setOnMoviePlayed(movie -> {
            watchHistory.remove(movie.getTitle());
            watchHistory.put(movie.getTitle(), movie);
            if (currentScreen.equals("History") || currentScreen.equals("My Library")) {
                renderCurrentScreen();
            }
        });

        header = new Header(query -> {
            searchQuery = query == null ? "" : query.trim();
            renderCurrentScreen();
        });
        mainLayout.setTop(header);

        sidebar = new Sidebar();
        sidebar.setOnMenuItemClicked(screenName -> showScreen(screenName));
        mainLayout.setLeft(sidebar);

        applyTheme();
        showScreen("Home");

        absoluteRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                updateResponsiveLayout(newWidth.doubleValue()));

        absoluteRoot.getChildren().addAll(mainLayout, videoPlayer);
        return absoluteRoot;
    }

    private void updateResponsiveLayout(double width) {
        if (sidebar != null) {
            sidebar.setCompact(width < 900);
        }
        if (header != null) {
            header.setCompact(width < 700);
        }
    }

    private void showScreen(String screenName) {
        this.currentScreen = screenName;
        this.searchQuery = ""; // Clear search when changing screens
        if (header != null) {
            header.clearSearch();
        }

        // Tell the sidebar to update its blue highlight
        if (sidebar != null) {
            sidebar.setActive(screenName);
        }

        renderCurrentScreen();
    }

    private void renderCurrentScreen() {
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(0, 24, 50, 24));

        Map<String, List<Movie>> categories = MovieData.getCategorizedMovies();

        if (currentScreen.equals("History")) {
            addCollection(contentBox, "Watch History", getWatchHistory(),
                    "Movies you've watched will appear here.");
        }
        else if (currentScreen.equals("Saved")) {
            addCollection(contentBox, "My List", new ArrayList<>(savedMovies.values()),
                    "Save movies from a movie card to watch later.");
        }
        else if (currentScreen.equals("My Library")) {
            Map<String, Movie> library = new LinkedHashMap<>(watchHistory);
            library.putAll(savedMovies);
            addCollection(contentBox, "My Library", new ArrayList<>(library.values()),
                    "Your watched and saved movies will appear here.");
        }
        else if (!searchQuery.isEmpty()) {
            Map<String, List<Movie>> matchingCategories = findMatchingMovies(categories, searchQuery);
            if (matchingCategories.isEmpty()) {
                Label noResults = new Label("No movies found for \"" + searchQuery + "\".");
                noResults.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-padding: 50px;");
                contentBox.getChildren().add(noResults);
            } else {
                for (Map.Entry<String, List<Movie>> entry : matchingCategories.entrySet()) {
                    contentBox.getChildren().add(createCategoryBar(entry.getKey(), entry.getValue()));
                }
            }
        }
        else if (currentScreen.equals("Home") || currentScreen.equals("Categories")) {
            List<Movie> trendingMovies = getTrendingMovies(categories);
            if (!trendingMovies.isEmpty()) {
                contentBox.getChildren().add(createCategoryBar("Trending Now", trendingMovies));
            }
            for (Map.Entry<String, List<Movie>> entry : categories.entrySet()) {
                contentBox.getChildren().add(createCategoryBar(entry.getKey(), entry.getValue()));
            }
        }
        else if (currentScreen.equals("Trending")) {
            List<Movie> trendingMovies = getTrendingMovies(categories);
            if (!trendingMovies.isEmpty()) {
                contentBox.getChildren().add(createCategoryBar("Trending Now", trendingMovies));
            } else {
                Label error = new Label("No trending movies are available.");
                error.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px; -fx-padding: 50px;");
                contentBox.getChildren().add(error);
            }
        }
        else if (currentScreen.equals("Coming Soon")) {
            contentBox.getChildren().add(createPlaceholder("Coming Soon", "New movies and trailers will be added here soon."));
        }
        else if (currentScreen.equals("Settings")) {
            contentBox.getChildren().add(createSettingsScreen());
        }
        else {
            contentBox.getChildren().add(createPlaceholder(currentScreen, "This page is coming soon."));
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainLayout.setCenter(scrollPane);
    }

    private List<Movie> getTrendingMovies(Map<String, List<Movie>> categories) {
        List<Movie> trendingMovies = new ArrayList<>();
        Map<String, Integer> selectedCounts = new LinkedHashMap<>();
        String[] trendingCategories = {"Action", "Comedy", "Sci-Fi", "Action", "Sci-Fi"};
        for (String categoryName : trendingCategories) {
            List<Movie> categoryMovies = categories.get(categoryName);
            int movieIndex = selectedCounts.getOrDefault(categoryName, 0);
            if (categoryMovies != null && movieIndex < categoryMovies.size()) {
                trendingMovies.add(categoryMovies.get(movieIndex));
                selectedCounts.put(categoryName, movieIndex + 1);
            }
        }
        return trendingMovies;
    }

    private void addCollection(VBox contentBox, String title, List<Movie> movies, String emptyMessage) {
        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if (searchQuery.isEmpty() || movieMatches(movie, searchQuery)) {
                filteredMovies.add(movie);
            }
        }

        if (filteredMovies.isEmpty()) {
            contentBox.getChildren().add(createPlaceholder(
                    title,
                    searchQuery.isEmpty() ? emptyMessage : "No matching movies in " + title + "."));
            return;
        }
        contentBox.getChildren().add(createCategoryBar(title, filteredMovies));
    }

    private List<Movie> getWatchHistory() {
        List<Movie> movies = new ArrayList<>(watchHistory.values());
        Collections.reverse(movies);
        return movies;
    }

    private CategoryBar createCategoryBar(String title, List<Movie> movies) {
        return new CategoryBar(title, movies, videoPlayer,
                movie -> savedMovies.containsKey(movie.getTitle()),
                movie -> {
                    if (savedMovies.containsKey(movie.getTitle())) {
                        savedMovies.remove(movie.getTitle());
                    } else {
                        savedMovies.put(movie.getTitle(), movie);
                    }
                    if (currentScreen.equals("Saved") || currentScreen.equals("My Library")) {
                        renderCurrentScreen();
                    }
                });
    }

    private boolean movieMatches(Movie movie, String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        return movie.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || movie.getYear().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || movie.getCategory().toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private VBox createPlaceholder(String titleText, String descText) {
        VBox box = new VBox(20);
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: " + (AppSettings.isDarkMode() ? "#ffffff" : "#171717")
                + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label desc = new Label(descText);
        desc.setStyle("-fx-text-fill: " + (AppSettings.isDarkMode() ? "#aaaaaa" : "#555555")
                + "; -fx-font-size: 16px; -fx-padding: 20 0 0 0;");
        box.getChildren().addAll(title, desc);
        return box;
    }

    private VBox createSettingsScreen() {
        Label title = new Label("Settings");
        title.setStyle("-fx-text-fill: " + (AppSettings.isDarkMode() ? "#ffffff" : "#171717")
                + "; -fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 0 0 30 0;");

        VBox settingsBox = new VBox(20);
        settingsBox.setMaxWidth(600);

        Label section1 = new Label("Appearance");
        section1.setStyle("-fx-text-fill: #e50914; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

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
            darkModeValue.setText(AppSettings.isDarkMode() ? "Enabled" : "Disabled");
            darkModeValue.setStyle("-fx-text-fill: " + (AppSettings.isDarkMode() ? "#22c55e" : "#888888") + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            applyTheme();
            renderCurrentScreen();
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
        section2.setStyle("-fx-text-fill: #e50914; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 20 0 5 0;");

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
            autoPlayValue.setText(AppSettings.isAutoPlay() ? "On" : "Off");
            autoPlayValue.setStyle("-fx-text-fill: " + (AppSettings.isAutoPlay() ? "#22c55e" : "#888888") + "; -fx-font-size: 14px; -fx-font-weight: bold;");
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

        settingsBox.getChildren().addAll(section1, darkModeRow, langRow, section2, autoPlayRow, qualityRow);
        VBox box = new VBox(title, settingsBox);
        return box;
    }

    private Map<String, List<Movie>> findMatchingMovies(Map<String, List<Movie>> categories, String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        Map<String, List<Movie>> matches = new LinkedHashMap<>();
        for (Map.Entry<String, List<Movie>> category : categories.entrySet()) {
            List<Movie> matchingMovies = new ArrayList<>();
            for (Movie movie : category.getValue()) {
                if (movieMatches(movie, normalizedQuery)) {
                    matchingMovies.add(movie);
                }
            }
            if (!matchingMovies.isEmpty()) {
                matches.put(category.getKey(), matchingMovies);
            }
        }
        return matches;
    }

    private void applyTheme() {
        boolean dark = AppSettings.isDarkMode();
        String background = dark ? "#141414" : "#f4f4f5";
        if (rootLayout != null) {
            rootLayout.setStyle("-fx-background-color: " + background + ";");
        }
        if (mainLayout != null) {
            mainLayout.setStyle("-fx-background-color: " + background + ";");
        }
        if (sidebar != null) {
            sidebar.setDarkMode(dark);
        }
        if (header != null) {
            header.setStyle("-fx-background-color: " + background + ";");
        }
        if (rootLayout != null) {
            if (dark) {
                rootLayout.getStyleClass().remove("light-theme");
            } else if (!rootLayout.getStyleClass().contains("light-theme")) {
                rootLayout.getStyleClass().add("light-theme");
            }
        }
    }
}