package com.evc;

public class AppSettings {
    private static boolean darkMode = true;
    private static boolean autoPlay = true;
    private static String language = "English";
    private static String videoQuality = "Auto";

    public static boolean isDarkMode() { return darkMode; }
    public static void setDarkMode(boolean value) { darkMode = value; }

    public static boolean isAutoPlay() { return autoPlay; }
    public static void setAutoPlay(boolean value) { autoPlay = value; }

    public static String getLanguage() { return language; }
    public static void setLanguage(String value) { language = value; }

    public static String getVideoQuality() { return videoQuality; }
    public static void setVideoQuality(String value) { videoQuality = value; }
}
