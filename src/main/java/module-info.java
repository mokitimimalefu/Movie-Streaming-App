module com.evc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    opens com.evc to javafx.fxml;
    exports com.evc;
    exports com.evc.model;
    exports com.evc.view;
}