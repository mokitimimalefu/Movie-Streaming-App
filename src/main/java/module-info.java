module com.evc {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    // REQUIRED for the built-in Java HTTP server
    requires jdk.httpserver;

    opens com.evc to javafx.fxml;
    exports com.evc;
    exports com.evc.model;
    exports com.evc.view;
}