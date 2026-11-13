package com.evc.view;

import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class VideoPlayer extends StackPane {
    private WebView webView;
    private WebEngine webEngine;
    private LoadingAnimation loadingAnimation;
    private AudioClip clickSound;
    private String currentVideoId = "";

    public VideoPlayer() {
        this.getStyleClass().add("video-player-root");

        try {
            String soundPath = getClass().getResource("/com/evc/sounds/click.mp3").toExternalForm();
            clickSound = new AudioClip(soundPath);
        } catch (Exception e) {
            System.out.println("Click sound not found.");
        }

        webView = new WebView();
        webView.setVisible(false);
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        // Pretend to be modern Chrome - this fixes Error 153
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

    public void playVideo(String embedUrl) {
        this.setVisible(true);
        this.toFront();
        loadingAnimation.setVisible(true);
        loadingAnimation.reset();
        webView.setVisible(false);

        // Extract just the ID: https://www.youtube.com/embed/XXXX -> XXXX
        String videoId = embedUrl;
        if (videoId.contains("/embed/")) {
            videoId = videoId.substring(videoId.indexOf("/embed/") + 7);
        }
        if (videoId.contains("?")) {
            videoId = videoId.substring(0, videoId.indexOf("?"));
        }
        currentVideoId = videoId;

        // Load OUR OWN HTML page with YouTube IFrame API inside
        // This is what stops Error 153
        String html = "<!DOCTYPE html><html><head><meta charset='utf-8'>"
            + "<style>body{margin:0;background:black;} iframe{width:100%;height:100%;}</style></head>"
            + "<body>"
            + "<div id='player'></div>"
            + "<script>"
            + "var tag=document.createElement('script');"
            + "tag.src='https://www.youtube.com/iframe_api';"
            + "document.head.appendChild(tag);"
            + "var player;"
            + "function onYouTubeIframeAPIReady(){"
            + " player=new YT.Player('player',{height:'100%',width:'100%',videoId:'" + videoId + "',"
            + " playerVars:{'autoplay':1,'enablejsapi':1,'rel':0,'modestbranding':1},"
            + " events:{'onReady':function(e){e.target.playVideo();}}});}"
            + "function playVideo(){if(player) player.playVideo();}"
            + "function pauseVideo(){if(player) player.pauseVideo();}"
            + "</script></body></html>";

        webEngine.loadContent(html);
    }

    public void stopVideo() {
        sendYouTubeCommand("pauseVideo");
        webEngine.loadContent("<html><body style='background:black'></body></html>");
        this.setVisible(false);
    }

    private void sendYouTubeCommand(String command) {
        if (webEngine == null) return;
        try {
            // Call the function we defined in the HTML above
            webEngine.executeScript(command + "();");
        } catch (Exception e) {
            System.out.println("YouTube command failed: " + command);
        }
    }

    private void playClickSound() {
        if (clickSound != null) clickSound.play();
    }
}