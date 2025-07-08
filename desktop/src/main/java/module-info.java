module org.example.desktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;
    requires java.dotenv;
    requires java.logging;

    opens org.example.desktop.controller to javafx.fxml;
    opens org.example.desktop.model to com.google.gson, javafx.base;
    opens org.example.desktop.dto to com.google.gson, javafx.fxml, javafx.base;

    exports org.example.desktop;
}
