package com.evc.model;

public class Movie {
    private String title;
    private String year;
    private String category;
    private String imagePath;
    private String youtubeEmbedUrl;

    public Movie(String title, String year, String category, String imagePath, String youtubeVideoId) {
        this.title = title;
        this.year = year;
        this.category = category;
        this.imagePath = imagePath;
        // enablejsapi=1 lets our custom PLAY/PAUSE buttons control YouTube
        this.youtubeEmbedUrl = "https://www.youtube.com/embed/" + youtubeVideoId
                + "?autoplay=1&enablejsapi=1&rel=0&modestbranding=1";
    }

    public String getTitle() { return title; }
    public String getYear() { return year; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
    public String getYoutubeEmbedUrl() { return youtubeEmbedUrl; }
}