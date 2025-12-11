package io.github.ninedots0.ui.panel;

import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.core.save.SaveData;
import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.game.GameController;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartGamePanel extends VBox {

    private MainFrame mainFrame;
    private VBox saveListBox = new VBox(10);

    public StartGamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setSpacing(20);
        setPadding(new Insets(20));

        Label title = new Label("选择存档");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ScrollPane scrollPane = new ScrollPane(saveListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Button newGameBtn = new Button("新建游戏");
        newGameBtn.setStyle("-fx-font-size: 18px;");
        newGameBtn.setOnAction(e -> startNewGame());

        Button backBtn = new Button("返回主菜单");
        backBtn.setOnAction(e -> mainFrame.showMainMenu());
        // backBtn.setLayoutY(30);

        getChildren().addAll(title, scrollPane, newGameBtn, backBtn);

        loadSaveFiles();
    }

    /** 载入当前用户的所有存档 */
    private void loadSaveFiles() {
        if (mainFrame.getAuthService().getCurrentUser() == null) return;
        saveListBox.getChildren().clear();

        String username = mainFrame.getAuthService().getCurrentUser().getUsername();
        File saveDir = new File("saves/" + username);

        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File[] saves = saveDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (saves == null || saves.length == 0) {
            saveListBox.getChildren().add(new Label("没有存档"));
            return;
        }

        for (File f : saves) {
            Button btn = new Button("存档：" + f.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> loadSave(f.getPath()));
            saveListBox.getChildren().add(btn);
        }
    }

    /** 点击已有存档 → 载入 → 进入游戏界面 */
    private void loadSave(String path) {
        try {
            SaveData data = SaveManager.load(path);

            GameController gc = new GameController(new Board(), 1);
            gc.applySaveData(data);

            mainFrame.showGamePanel(gc);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "读取存档失败").show();
        }
    }

    /** 新建一个游戏 */
    private void startNewGame() {
        GameController gc = new GameController(new Board(), 1); // 自动创建新棋盘
        mainFrame.showGamePanel(gc);
    }
}
