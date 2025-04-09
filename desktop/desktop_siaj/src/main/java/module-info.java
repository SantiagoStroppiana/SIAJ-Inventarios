module org.example.desktop_siaj {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.desktop_siaj to javafx.fxml;
    exports org.example.desktop_siaj;
}