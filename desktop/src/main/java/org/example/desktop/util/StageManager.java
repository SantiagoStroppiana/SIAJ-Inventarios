package org.example.desktop.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageManager {

    private static Stage primaryStage;
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    public static void loadScene(String fxml, double width, double height) {
        loadScene(fxml, width, height, false);
    }

    public static void loadScene(String fxml, double width, double height, boolean createNewStage) {
        try{
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxml));
            Parent originalRoot = loader.load();

            if (createNewStage) {
                Stage newStage = new Stage();
                newStage.initStyle(StageStyle.UNDECORATED);

                HBox titleBar = createCustomTitleBar("SIAJ-Inventarios", newStage);

                StackPane stackContainer = new StackPane();
                VBox contentContainer = new VBox();
                contentContainer.setSpacing(0);

                VBox.setMargin(originalRoot, new Insets(0, 0, 0, 0));
                contentContainer.getChildren().addAll(new Region() {{ setPrefHeight(40); }}, originalRoot);
                VBox.setVgrow(originalRoot, Priority.ALWAYS);

                stackContainer.getChildren().addAll(contentContainer, titleBar);

                StackPane.setAlignment(titleBar, Pos.TOP_CENTER);
                StackPane.setAlignment(contentContainer, Pos.CENTER);

                titleBar.setMinHeight(40);
                titleBar.setPrefHeight(40);
                titleBar.setMaxHeight(40);

                Scene scene = new Scene(stackContainer, width, height + 40);
                newStage.setScene(scene);


                makeDraggable(titleBar, newStage);
                newStage.centerOnScreen();
                newStage.show();

            } else {
                HBox titleBar = createCustomTitleBar("SIAJ-Inventarios", primaryStage);

                StackPane stackContainer = new StackPane();
                VBox contentContainer = new VBox();
                contentContainer.setSpacing(0);

                contentContainer.getChildren().addAll(new Region() {{ setPrefHeight(40); }}, originalRoot);
                VBox.setVgrow(originalRoot, Priority.ALWAYS);

                stackContainer.getChildren().addAll(contentContainer, titleBar);

                StackPane.setAlignment(titleBar, Pos.TOP_CENTER);
                StackPane.setAlignment(contentContainer, Pos.CENTER);

                titleBar.setMinHeight(40);
                titleBar.setPrefHeight(40);
                titleBar.setMaxHeight(40);

                Scene scene = new Scene(stackContainer, width, height + 40);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);

                makeDraggable(titleBar, primaryStage);
                primaryStage.centerOnScreen();

                if (!primaryStage.isShowing()) {
                    primaryStage.show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static HBox createCustomTitleBar(String title, Stage targetStage) {
        HBox titleBar = new HBox();
        titleBar.setPrefHeight(40);
        titleBar.setMinHeight(40);
        titleBar.setMaxHeight(40);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(0, 0, 0, 12));

        titleBar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);" +
                        "-fx-border-color: #1abc9c;" +
                        "-fx-border-width: 0 0 1px 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );

        try {
            Image appIcon = new Image(StageManager.class.getResourceAsStream("/images/logo.png"));
            ImageView iconView = new ImageView(appIcon);
            iconView.setFitWidth(24);
            iconView.setFitHeight(24);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            titleBar.getChildren().add(iconView);
        } catch (Exception e) {

        }

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                        "-fx-padding: 0 0 0 10px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 1, 0, 0, 1);"
        );
        titleBar.getChildren().add(titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().add(spacer);

        HBox controlButtons = createControlButtons(targetStage);
        titleBar.getChildren().add(controlButtons);

        return titleBar;
    }

    private static HBox createControlButtons(Stage targetStage) {
        HBox controlButtons = new HBox(0); // Sin espaciado entre botones
        controlButtons.setAlignment(Pos.CENTER_RIGHT);
        controlButtons.setPrefHeight(40);
        controlButtons.setMaxHeight(40);

        Button minimizeBtn = new Button("🗕");
        minimizeBtn.setPrefSize(40, 40);
        minimizeBtn.setMinSize(40, 40);
        minimizeBtn.setMaxSize(40, 40);
        minimizeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );
        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        ));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        ));
        minimizeBtn.setOnAction(e -> targetStage.setIconified(true));

        // Botón cerrar
        Button closeBtn = new Button("✕");
        closeBtn.setPrefSize(40, 40);
        closeBtn.setMinSize(40, 40);
        closeBtn.setMaxSize(40, 40);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        ));
        closeBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de cierre");
            alert.setHeaderText("¿Estás seguro de que deseas cerrar la aplicación?");

            alert.getDialogPane().setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50);" +
                            "-fx-border-color: #1abc9c;" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 10px;" +
                            "-fx-background-radius: 10px;"
            );

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result == ButtonType.OK) {
                targetStage.close();
            }
        });

        controlButtons.getChildren().addAll(minimizeBtn, closeBtn);
        return controlButtons;
    }

    private static void makeDraggable(HBox titleBar, Stage stage) {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            titleBar.setCursor(Cursor.MOVE);
        });

        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        titleBar.setOnMouseReleased(event -> {
            titleBar.setCursor(Cursor.DEFAULT);
        });

        titleBar.setOnMouseEntered(event -> {
            if (!event.isPrimaryButtonDown()) {
                titleBar.setCursor(Cursor.DEFAULT);
            }
        });
    }

    public static void setTitle(String newTitle) {
        if (primaryStage != null && primaryStage.getScene() != null) {
            VBox mainContainer = (VBox) primaryStage.getScene().getRoot();
            HBox titleBar = (HBox) mainContainer.getChildren().get(0);
            Label titleLabel = (Label) titleBar.getChildren().stream()
                    .filter(node -> node instanceof Label)
                    .findFirst()
                    .orElse(null);
            if (titleLabel != null) {
                titleLabel.setText(newTitle);
            }
        }
    }
}