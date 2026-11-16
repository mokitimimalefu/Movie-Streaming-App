package com.evc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

public final class LicensedFileDownloads {
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            ".avi", ".m4v", ".mkv", ".mov", ".mp4", ".mpeg", ".mpg", ".webm", ".wmv");

    private final Path downloadDirectory;

    public LicensedFileDownloads(Path downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public Path getDownloadDirectory() {
        return downloadDirectory;
    }

    public Path importLicensedFile(Path source, String movieTitle) throws IOException {
        if (source == null || !Files.isRegularFile(source)) {
            throw new IOException("The selected source is not a regular file.");
        }

        String sourceName = source.getFileName().toString();
        String extension = getExtension(sourceName);
        if (!VIDEO_EXTENSIONS.contains(extension)) {
            throw new IOException("Select a supported video file.");
        }

        Files.createDirectories(downloadDirectory);
        String safeTitle = movieTitle == null
                ? ""
                : movieTitle.replaceAll("[^A-Za-z0-9._-]+", "_").replaceAll("^_+|_+$", "");
        if (safeTitle.isEmpty()) {
            safeTitle = "video";
        }

        Path destination = uniquePath(safeTitle, extension);
        return Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES);
    }

    public List<Path> listDownloadedFiles() throws IOException {
        if (!Files.isDirectory(downloadDirectory)) {
            return List.of();
        }

        try (Stream<Path> files = Files.list(downloadDirectory)) {
            List<Path> results = new ArrayList<>();
            files.filter(Files::isRegularFile)
                    .filter(path -> VIDEO_EXTENSIONS.contains(getExtension(path.getFileName().toString())))
                    .forEach(results::add);
            results.sort(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)));
            return results;
        }
    }

    private Path uniquePath(String title, String extension) {
        Path candidate = downloadDirectory.resolve(title + extension);
        int copyNumber = 2;
        while (Files.exists(candidate)) {
            candidate = downloadDirectory.resolve(title + " (" + copyNumber++ + ")" + extension);
        }
        return candidate;
    }

    private static String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex < 0 ? "" : fileName.substring(extensionIndex).toLowerCase(Locale.ROOT);
    }
}
