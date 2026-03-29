module com.ornek {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;
    requires javafx.web;
    requires javafx.media;
    requires java.desktop;

    opens com.ornek to javafx.fxml;

    exports com.ornek;
}
