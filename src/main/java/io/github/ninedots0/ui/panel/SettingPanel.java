package io.github.ninedots0.ui.panel;

import io.github.ninedots0.ui.frame.MainFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import io.github.ninedots0.ui.util.AudioUtils;
import io.github.ninedots0.ui.util.StyleUtils;

public class SettingPanel {

    private MainFrame mainFrame;

    public SettingPanel(MainFrame frame) {
        this.mainFrame = frame;
    }

    public StackPane getContent() {

        /* ===== 根布局 ===== */
        StackPane root = new StackPane();
        root.setPadding(new Insets(30));

        /* ===== 中央设置区域 ===== */
        VBox centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);

        Label volumeLabel = new Label("音量");
        volumeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Slider volumeSlider = new Slider(0, 1, AudioUtils.getVolume());
        volumeSlider.setPrefWidth(300);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setBlockIncrement(0.1);

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                AudioUtils.setVolume(newVal.doubleValue())
        );

        centerBox.getChildren().addAll(volumeLabel, volumeSlider);

        /* ===== 左下角返回按钮 ===== */
        Button backBtn = new Button("返回主菜单");
        StyleUtils.applyMainMenuButton(backBtn);
        backBtn.setOnAction(e -> mainFrame.showMainMenu());

        StackPane.setAlignment(backBtn, Pos.BOTTOM_LEFT);
        StackPane.setMargin(backBtn, new Insets(0, 0, 10, 10));

        /* ===== 组合 ===== */
        root.getChildren().addAll(centerBox, backBtn);
        return root;
    }
}
