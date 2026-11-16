package com.evc.view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.animation.PauseTransition;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.function.Consumer;
import com.evc.model.Movie;

public class VideoPlayer extends StackPane {
    private WebView webView;
    private WebEngine webEngine;
    private LoadingAnimation loadingAnimation;
    private AudioClip clickSound;
    private HttpServer server;
    private int serverPort;
    private PauseTransition loadTimeout;
    private final EventHandler<KeyEvent> keyEventHandler = this::handleKeyEvent;
    private Consumer<Movie> onMoviePlayed = movie -> { };

    // State tracking for volume
    private int currentVolume = 100;
    private boolean isMuted = false;

    public VideoPlayer() {
        this.getStyleClass().add("video-player-root");

        try {
            String soundPath = getClass().getResource("/com/evc/sounds/click.mp3").toExternalForm();
            clickSound = new AudioClip(soundPath);
        } catch (Exception e) {
            System.out.println("Click sound not found.");
        }

        startLocalServer();

        webView = new WebView();
        webView.setVisible(false);
        webView.setContextMenuEnabled(false);
        webView.setFocusTraversable(false);

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
        );

        loadingAnimation = new LoadingAnimation();
        loadTimeout = new PauseTransition(Duration.seconds(15));
        loadTimeout.setOnFinished(event -> {
            if (!webView.isVisible()) {
                loadingAnimation.showError();
            }
        });

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("Load state changed: " + oldState + " → " + newState);

            if (newState == Worker.State.SUCCEEDED) {
                loadTimeout.stop();
                loadingAnimation.setVisible(false);
                webView.setVisible(true);
                this.requestFocus();
                System.out.println("Video page loaded successfully");
            }
            if (newState == Worker.State.FAILED) {
                loadTimeout.stop();
                loadingAnimation.showError();
                System.err.println("Load failed: " + webEngine.getLoadWorker().getException());
            }
            if (newState == Worker.State.CANCELLED) {
                loadTimeout.stop();
                loadingAnimation.showError();
                System.out.println("Load was cancelled");
            }
        });

        IconButton playBtn = new IconButton("/com/evc/images/icons/play.png", "PLAY");
        playBtn.setOnMouseClicked(e -> {
            playClickSound();
            sendYouTubeCommand("playVideo");
            this.requestFocus();
        });

        IconButton pauseBtn = new IconButton("/com/evc/images/icons/pause.png", "PAUSE");
        pauseBtn.setOnMouseClicked(e -> {
            playClickSound();
            sendYouTubeCommand("pauseVideo");
            this.requestFocus();
        });

        IconButton closeBtn = new IconButton("/com/evc/images/icons/close.png", "CLOSE");
        closeBtn.setOnMouseClicked(e -> {
            playClickSound();
            stopVideo();
        });

        HBox controls = new HBox(20);
        controls.getStyleClass().add("video-controls");
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));
        controls.getChildren().addAll(playBtn, pauseBtn, closeBtn);

        VBox layout = new VBox(webView, controls);
        layout.setAlignment(Pos.CENTER);
        VBox.setVgrow(webView, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().addAll(layout, loadingAnimation);

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.removeEventFilter(KeyEvent.ANY, keyEventHandler);
            }
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.ANY, keyEventHandler);
            }
        });
    }

    private void handleKeyEvent(KeyEvent event) {
        if (!isVisible() || event.getEventType() != KeyEvent.KEY_PRESSED) {
            return;
        }

        switch (event.getCode()) {
            case SPACE:
                togglePlayPauseJS();
                playClickSound();
                event.consume();
                break;
            case K:
                sendYouTubeCommand("pauseVideo");
                playClickSound();
                event.consume();
                break;
            case LEFT:
                seekVideo(-10);
                event.consume();
                break;
            case RIGHT:
                seekVideo(10);
                event.consume();
                break;
            case UP:
                if (isMuted) {
                    isMuted = false;
                    sendYouTubeCommand("unMute");
                }
                currentVolume = Math.min(100, currentVolume + 10);
                sendYouTubeCommand("setVolume", currentVolume);
                System.out.println("🔊 Volume: " + currentVolume + "%");
                event.consume();
                break;
            case DOWN:
                if (isMuted) {
                    isMuted = false;
                    sendYouTubeCommand("unMute");
                }
                currentVolume = Math.max(0, currentVolume - 10);
                sendYouTubeCommand("setVolume", currentVolume);
                System.out.println("🔉 Volume: " + currentVolume + "%");
                event.consume();
                break;
            case M:
                if (isMuted) {
                    sendYouTubeCommand("unMute");
                    isMuted = false;
                    System.out.println("🔊 Unmuted");
                } else {
                    sendYouTubeCommand("mute");
                    isMuted = true;
                    System.out.println("🔇 Muted");
                }
                event.consume();
                break;
            case ESCAPE:
                stopVideo();
                event.consume();
                break;
            default:
                break;
        }
    }

    private void togglePlayPauseJS() {
        if (webEngine == null) {
            return;
        }
        try {
            String js =
                    "var iframe = document.getElementById('player'); "
                            + "if(iframe && iframe.contentWindow) { "
                            + "  if (window.isVideoPlaying === undefined) window.isVideoPlaying = true; "
                            + "  if (window.isVideoPlaying) { "
                            + "    iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":[]}', '*'); "
                            + "    window.isVideoPlaying = false; "
                            + "  } else { "
                            + "    iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"playVideo\",\"args\":[]}', '*'); "
                            + "    window.isVideoPlaying = true; "
                            + "  } "
                            + "}";
            webEngine.executeScript(js);
        } catch (Exception e) {
            System.out.println("Toggle failed: " + e.getMessage());
        }
    }

    private void seekVideo(double seconds) {
        if (webEngine == null) {
            return;
        }
        try {
            String js =
                    "var iframe = document.getElementById('player'); "
                            + "if(iframe && iframe.contentWindow) { "
                            + "  var listener = function(event) { "
                            + "    try { "
                            + "      var data = JSON.parse(event.data); "
                            + "      if (data.event === 'infoDelivery' && data.info && data.info.currentTime !== undefined) { "
                            + "        window.removeEventListener('message', listener); "
                            + "        var currentTime = data.info.currentTime; "
                            + "        var newTime = currentTime + (" + seconds + "); "
                            + "        if (newTime < 0) newTime = 0; "
                            + "        iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"seekTo\",\"args\":[' + newTime + ', true]}', '*'); "
                            + "      } "
                            + "    } catch(e) {} "
                            + "  }; "
                            + "  window.addEventListener('message', listener); "
                            + "  iframe.contentWindow.postMessage('{\"event\":\"listening\",\"id\":1}', '*'); "
                            + "}";
            webEngine.executeScript(js);
        } catch (Exception e) {
            System.out.println("Seek failed: " + e.getMessage());
        }
    }

    private void startLocalServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            serverPort = server.getAddress().getPort();

            server.createContext("/player", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = exchange.getRequestURI().getQuery();
                    String videoId = null;

                    if (query != null && query.contains("v=")) {
                        for (String parameter : query.split("&")) {
                            if (parameter.startsWith("v=")) {
                                videoId = parameter.substring(2);
                                break;
                            }
                        }
                    }

                    if (videoId == null || !videoId.matches("[A-Za-z0-9_-]{11}")) {
                        byte[] error = "Invalid video ID".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(400, error.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(error);
                        }
                        return;
                    }

                    String html = "<!DOCTYPE html><html><head><meta charset='utf-8'>"
                            + "<style>"
                            + "  body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }"
                            + "  .video-container { position: absolute; top: 0; left: 0; width: 100vw; height: 100vh; }"
                            + "  iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none; }"
                            + "</style>"
                            + "</head><body>"
                            + "<div class='video-container'>"
                            + "<iframe id='player' "
                            + "src='https://www.youtube.com/embed/" + videoId
                            + "?autoplay=1&enablejsapi=1&rel=0&modestbranding=1&playsinline=1&origin=http://127.0.0.1:"
                            + serverPort + "' "
                            + "frameborder='0' allow='autoplay; encrypted-media; picture-in-picture' allowfullscreen></iframe>"
                            + "</div>"
                            + "<script>"
                            + "  window.onload = function() { "
                            + "    console.log('Player iframe loaded'); "
                            + "    var iframe = document.getElementById('player'); "
                            + "    iframe.contentWindow.postMessage('{\"event\":\"listening\",\"id\":1}', '*'); "
                            + "    window.isVideoPlaying = true; "
                            + "  };"
                            + "</script>"
                            + "</body></html>";

                    byte[] response = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response);
                    }
                }
            });

            server.start();
            System.out.println("Local video server started on port: " + serverPort);
        } catch (IOException e) {
            System.err.println("Failed to start local server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playVideo(String embedUrl) {
        this.setVisible(true);
        this.toFront();
        this.requestFocus();

        loadTimeout.stop();
        currentVolume = 100;
        isMuted = false;

        loadingAnimation.setVisible(true);
        loadingAnimation.reset();
        webView.setVisible(false);

        String videoId = extractVideoId(embedUrl);
        if (videoId == null) {
            loadingAnimation.showError();
            System.err.println(
                    "Cannot play video: expected a YouTube embed URL or 11-character video ID"
            );
            return;
        }

        if (server == null) {
            loadingAnimation.showError();
            System.err.println("❌ Cannot play video: local video server is unavailable");
            return;
        }

        System.out.println("🎬 Loading video ID: " + videoId);

        String localUrl = "http://127.0.0.1:" + serverPort + "/player?v=" + videoId;
        System.out.println("Loading: " + localUrl);
        webEngine.load(localUrl);
        loadTimeout.playFromStart();
    }

    public void playVideo(Movie movie) {
        if (movie == null) {
            return;
        }
        String embedUrl = movie.getYoutubeEmbedUrl();
        if (server != null && extractVideoId(embedUrl) != null) {
            onMoviePlayed.accept(movie);
        }
        playVideo(embedUrl);
    }

    public void setOnMoviePlayed(Consumer<Movie> callback) {
        onMoviePlayed = callback == null ? movie -> { } : callback;
    }

    public void stopVideo() {
        loadTimeout.stop();
        webEngine.load("about:blank");
        this.setVisible(false);
    }

    private String extractVideoId(String embedUrl) {
        if (embedUrl == null) {
            return null;
        }

        String videoId = embedUrl.trim();
        int embedIndex = videoId.indexOf("/embed/");
        if (embedIndex >= 0) {
            videoId = videoId.substring(embedIndex + "/embed/".length());
        }

        int queryIndex = videoId.indexOf('?');
        if (queryIndex >= 0) {
            videoId = videoId.substring(0, queryIndex);
        }

        return videoId.matches("[A-Za-z0-9_-]{11}") ? videoId : null;
    }

    private void sendYouTubeCommand(String command) {
        sendYouTubeCommand(command, -1);
    }

    private void sendYouTubeCommand(String command, int arg) {
        if (webEngine == null) {
            return;
        }
        try {
            String argsStr = arg >= 0 ? "[" + arg + "]" : "[]";
            String js = "var iframe = document.getElementById('player'); if(iframe && iframe.contentWindow) { "
                    + "iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\""
                    + command + "\",\"args\":" + argsStr + "}', '*'); }";
            webEngine.executeScript(js);
        } catch (Exception e) {
            System.out.println("YouTube command failed: " + command + " — " + e.getMessage());
        }
    }

    private void playClickSound() {
        if (clickSound != null) {
            clickSound.play();
        }
    }
}