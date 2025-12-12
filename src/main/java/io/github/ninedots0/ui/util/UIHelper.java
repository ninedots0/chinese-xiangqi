package io.github.ninedots0.ui.util;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UIHelper {

    public static void showErrorOverlay(Stage stage, String message) {

        // 文本
        Label label = new Label(message);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // 背景面板
        StackPane pane = new StackPane(label);
        pane.setStyle("-fx-background-color: rgba(0,0,0,0.6);"); // 黑色透明
        pane.setAlignment(Pos.CENTER);
        pane.setPrefSize(stage.getWidth(), stage.getHeight());

        Scene scene = stage.getScene();
        StackPane root = (StackPane) scene.getRoot();
        root.getChildren().add(pane);

        // 淡出动画
        FadeTransition ft = new FadeTransition(Duration.seconds(1.8), pane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> root.getChildren().remove(pane));
        ft.play();
    }
}
