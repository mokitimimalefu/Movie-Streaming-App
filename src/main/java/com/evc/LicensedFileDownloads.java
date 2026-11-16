package com.evc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public final class LicensedFileDownloads {
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            ".avi", ".m4v", ".mkv", ".mov", ".mp4", ".mpeg", ".mpg", ".webm", ".wmv");

    private final Path libraryFile;

    public LicensedFileDownloads(Path libraryFile) {
        this.libraryFile = libraryFile.toAbsolutePath();
    }

    public Path getLibraryFile() {
        return libraryFile;
    }

    public DownloadEntry addLicensedFileReference(Path source, String movieTitle) throws IOException {
        if (source == null || !Files.isRegularFile(source)) {
            throw new IOException("The selected source is not a regular file.");
        }

        Path absoluteSource = source.toAbsolutePath();
        String extension = getExtension(absoluteSource.getFileName().toString());
        if (!VIDEO_EXTENSIONS.contains(extension)) {
            throw new IOException("Select a supported video file.");
        }

        String title = movieTitle == null || movieTitle.isBlank()
                ? absoluteSource.getFileName().toString()
                : movieTitle.trim();

        Properties entries = loadEntries();
        String entryKey = "entry." + UUID.randomUUID();
        entries.setProperty(entryKey + ".title", title);
        entries.setProperty(entryKey + ".path", absoluteSource.toString());
        saveEntries(entries);
        return new DownloadEntry(title, absoluteSource);
    }

    public List<DownloadEntry> listDownloadedFiles() throws IOException {
        Properties entries = loadEntries();
        List<DownloadEntry> results = new ArrayList<>();
        for (String key : entries.stringPropertyNames()) {
            if (!key.startsWith("entry.") || !key.endsWith(".title")) {
                continue;
            }

            String entryKey = key.substring(0, key.length() - ".title".length());
            String pathValue = entries.getProperty(entryKey + ".path");
            if (pathValue == null || pathValue.isBlank()) {
                throw new IOException("The in-app Downloads list contains an incomplete entry.");
            }
            try {
                results.add(new DownloadEntry(entries.getProperty(key), Path.of(pathValue)));
            } catch (InvalidPathException e) {
                throw new IOException("The in-app Downloads list contains an invalid file path.", e);
            }
        }

        results.sort(Comparator.comparing(entry -> entry.title().toLowerCase(Locale.ROOT)));
        return results;
    }

    private Properties loadEntries() throws IOException {
        Properties entries = new Properties();
        if (Files.isRegularFile(libraryFile)) {
            try (InputStream input = Files.newInputStream(libraryFile)) {
                entries.load(input);
            }
        }
        return entries;
    }

    private void saveEntries(Properties entries) throws IOException {
        Path parent = libraryFile.getParent();
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, "evc-downloads-", ".tmp");
        try {
            try (OutputStream output = Files.newOutputStream(temporaryFile)) {
                entries.store(output, "EVC in-app Downloads list");
            }
            try {
                Files.move(temporaryFile, libraryFile,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, libraryFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex < 0 ? "" : fileName.substring(extensionIndex).toLowerCase(Locale.ROOT);
    }

    public record DownloadEntry(String title, Path sourcePath) { }
}
