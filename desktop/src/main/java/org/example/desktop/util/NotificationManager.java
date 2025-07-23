package org.example.desktop.util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationManager {

    public NotificationManager() {};

    private static Node createModernGraphic(String titulo, String mensaje, boolean resultado) {
        Label titleLabel = new Label(titulo);
        Label messageLabel = new Label(mensaje);

        // Estilos
        if (resultado) {
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #065f46;");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #065f46;");
        } else {
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #5f0906;");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #5f0d06;");
        }

        // Texto adaptable
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(320);  // Tamaño máximo del texto antes de hacer wrap

        VBox textBox = new VBox(titleLabel, messageLabel);
        textBox.setSpacing(5);

        Label icon = new Label(resultado ? "✔" : "✖");
        icon.setStyle("-fx-font-size: 26px; -fx-text-fill: " + (resultado ? "#10b981" : "#dc2626") + ";");
        icon.setMinWidth(30); // Asegura espacio para el ícono

        HBox content = new HBox(icon, textBox);
        content.setSpacing(12);
        content.setPrefWidth(400); // 👈 más ancho
        content.setMaxWidth(450);  // por si se agranda

        if (resultado) {
            content.setStyle(
                    "-fx-background-color: rgba(89,236,114,0.78);" +
                            "-fx-padding: 14;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-color: #34d399;" +
                            "-fx-border-radius: 10px;"
            );
        } else {
            content.setStyle(
                    "-fx-background-color: rgba(213,87,85,0.78);" +
                            "-fx-padding: 14;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-border-color: #d33434;" +
                            "-fx-border-radius: 10px;"
            );
        }

        return content;
    }


    public static void notificar(String titulo, String mensaje, boolean resultado) {
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .position(Pos.TOP_CENTER)
                    .hideAfter(Duration.seconds(4))
                    .graphic(createModernGraphic(titulo, mensaje, resultado))
                    .hideCloseButton();

            notification.show();
        });
    }


    public static void notificarExito(String mensaje) {
        notificar("Exito", mensaje, true);
    }

    public static void notificarError(String mensaje) {
        notificar("Error", mensaje, false);
    }


}
