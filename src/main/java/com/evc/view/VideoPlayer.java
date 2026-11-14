package com.evc.view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class VideoPlayer extends StackPane {
    private WebView webView;
    private WebEngine webEngine;
    private LoadingAnimation loadingAnimation;
    private AudioClip clickSound;
    private String currentVideoId = "";
    private HttpServer server;
    private int serverPort;

    public VideoPlayer() {
        this.getStyleClass().add("video-player-root");

        try {
            String soundPath = getClass().getResource("/com/evc/sounds/click.mp3").toExternalForm();
            clickSound = new AudioClip(soundPath);
        } catch (Exception e) {
            System.out.println("Click sound not found.");
        }

        // Start the built-in Java HTTP server
        startLocalServer();

        webView = new WebView();
        webView.setVisible(false);
        webView.setContextMenuEnabled(false);
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");

        loadingAnimation = new LoadingAnimation();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadingAnimation.setVisible(false);
                webView.setVisible(true);
            }
            if (newState == Worker.State.FAILED) {
                loadingAnimation.showError();
            }
        });

        IconButton playBtn = new IconButton("/com/evc/images/icons/play.png", "PLAY");
        playBtn.setOnMouseClicked(e -> {
            playClickSound();
            sendYouTubeCommand("playVideo");
        });

        IconButton pauseBtn = new IconButton("/com/evc/images/icons/pause.png", "PAUSE");
        pauseBtn.setOnMouseClicked(e -> {
            playClickSound();
            sendYouTubeCommand("pauseVideo");
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
    }

    private void startLocalServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            serverPort = server.getAddress().getPort();
            
            server.createContext("/player", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = exchange.getRequestURI().getQuery();
                    String videoId = "";
                    if (query != null && query.contains("v=")) {
                        videoId = query.split("v=")[1].split("&")[0];
                    }

                    // FULL SCREEN FIX: Using 100vw and 100vh with absolute positioning
                    String html = "<!DOCTYPE html><html><head><meta charset='utf-8'>"
                        + "<style>"
                        + "  body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: black; overflow: hidden; }"
                        + "  .video-container { position: absolute; top: 0; left: 0; width: 100vw; height: 100vh; }"
                        + "  iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none; }"
                        + "</style>"
                        + "</head><body>"
                        + "<div class='video-container'>"
                        + "<iframe id='player' "
                        + "src='https://www.youtube.com/embed/" + videoId + "?autoplay=1&enablejsapi=1&rel=0&modestbranding=1&playsinline=1' "
                        + "frameborder='0' allow='autoplay; encrypted-media' allowfullscreen></iframe>"
                        + "</div>"
                        + "</body></html>";

                    byte[] response = html.getBytes("UTF-8");
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                }
            });
            
            server.start();
            System.out.println("Local video server started on port: " + serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playVideo(String embedUrl) {
        this.setVisible(true);
        this.toFront();
        loadingAnimation.setVisible(true);
        loadingAnimation.reset();
        webView.setVisible(false);

        String videoId = embedUrl;
        if (videoId.contains("/embed/")) {
            videoId = videoId.substring(videoId.indexOf("/embed/") + 7);
        }
        if (videoId.contains("?")) {
            videoId = videoId.substring(0, videoId.indexOf("?"));
        }
        currentVideoId = videoId;

        String localUrl = "http://127.0.0.1:" + serverPort + "/player?v=" + videoId;
        webEngine.load(localUrl);
    }

    public void stopVideo() {
        webEngine.load("about:blank");
        this.setVisible(false);
    }

    private void sendYouTubeCommand(String command) {
        if (webEngine == null) return;
        try {
            String js = "var iframe = document.getElementById('player'); if(iframe && iframe.contentWindow) { "
                      + "iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"" 
                      + command + "\",\"args\":[]}', '*'); }";
            webEngine.executeScript(js);
        } catch (Exception e) {
            System.out.println("YouTube command failed: " + command);
        }
    }

    private void playClickSound() {
        if (clickSound != null) clickSound.play();
    }
}