package io.github.ninedots0.ui.panel;

import io.github.ninedots0.ui.util.*;
import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.core.save.SaveData;
import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.game.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.io.File;

public class StartGamePanel extends BorderPane {

    private MainFrame mainFrame;
    private VBox saveListBox = new VBox(15);  // Increased spacing between buttons

    public StartGamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Main layout settings
        setPadding(new Insets(30, 20, 20, 20));
        setStyle("-fx-background-color: #f4f4f9; -fx-border-radius: 10px;");

        // Title styling
        Label title = new Label("选择存档");
        title.setFont(Font.font("Arial", 28)); // Increased font size
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ScrollPane for saved games list
        ScrollPane scrollPane = new ScrollPane(saveListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px;");

        // New Game button styling with rounded edges
        Button newGameBtn = new Button("新建游戏");

        newGameBtn.setStyle("-fx-font-size: 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 15px;");
        newGameBtn.setOnAction(e -> startNewGame());
        newGameBtn.setMaxWidth(Double.MAX_VALUE);
        newGameBtn.setPadding(new Insets(12));

        // Back button styling with rounded edges
        Button backBtn = new Button("返回主菜单");
        backBtn.setStyle("-fx-font-size: 20px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 15px;");
        backBtn.setOnAction(e -> mainFrame.showMainMenu());
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPadding(new Insets(12));

        // Create a VBox to hold the buttons and align them in the center
        VBox buttonVBox = new VBox(15, newGameBtn, backBtn);  // Increased spacing
        buttonVBox.setAlignment(Pos.CENTER);  // Center-align buttons in the VBox

        // Set VBox on the left side of the scene, and make sure it fills the height
        buttonVBox.setMaxHeight(Double.MAX_VALUE); // Make VBox occupy full height
        setLeft(buttonVBox);

        // Add title and scroll pane to the center of the BorderPane
        VBox centerLayout = new VBox(25, title, scrollPane);  // Increased spacing
        centerLayout.setAlignment(Pos.TOP_CENTER);
        setCenter(centerLayout);

        // Load save files into the list
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
        // Style buttons for each save file with rounded edges
        for (File f : saves) {
            Button btn = new Button("存档：" + f.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-border-radius: 15px;"); // Rounded button
            btn.setOnAction(e -> loadSave(f.getPath()));
            btn.setPadding(new Insets(12));
            saveListBox.getChildren().add(btn);
        }
    }

    /** 点击已有存档 → 载入 → 进入游戏界面 */
    private void loadSave(String path) {
        try {
            SaveData data = SaveManager.load(path);
            if (data == null) {
                UIHelper.showErrorOverlay(mainFrame.getStage(), "加载存档失败");
            }
            else {
                GameController gc = new GameController(new Board(), 1);
                gc.applySaveData(data);
                mainFrame.showGamePanel(gc);
            }
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
