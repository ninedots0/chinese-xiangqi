package io.github.ninedots0.ui.panel;

import io.github.ninedots0.ui.frame.MainFrame;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import io.github.ninedots0.ui.util.AudioUtils;
import io.github.ninedots0.ui.util.StyleUtils;

public class SettingPanel {

    private MainFrame mainFrame;

    public SettingPanel(MainFrame frame) {
        this.mainFrame = frame;
    }

    public VBox getContent() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);

        Slider volumeSlider = new Slider(0, 1, AudioUtils.getVolume());
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setBlockIncrement(0.1);

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            AudioUtils.setVolume(newVal.doubleValue());
        });

        Button backBtn = new Button("返回主菜单");

        StyleUtils.applyMainMenuButton(backBtn);

        backBtn.setOnAction(e -> mainFrame.showMainMenu());

        root.getChildren().addAll(backBtn, volumeSlider);
        return root;
    }
}
