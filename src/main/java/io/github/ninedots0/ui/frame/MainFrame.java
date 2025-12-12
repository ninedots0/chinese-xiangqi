package io.github.ninedots0.ui.frame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import io.github.ninedots0.ui.panel.*;
import io.github.ninedots0.ui.util.*;
import io.github.ninedots0.core.auth.AuthService;
import io.github.ninedots0.core.board.*;
import io.github.ninedots0.core.game.GameController;
public class MainFrame extends Application {

    private Stage primaryStage;
    private Scene scene;
    private StackPane root;

    // Panels
    private MainMenuPanel mainMenuPanel;
    private SettingPanel settingPanel;
    private LoginPanel loginPanel;
    private StartGamePanel startGamePanel;
    private AuthService authService = new AuthService();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        // root 容器
        root = new StackPane();
        scene = new Scene(root, 900, 700);

        // 初始化窗口
        WindowUtils.initStageProperties(stage, "中国象棋", true, true);
        stage.setScene(scene);

        // 初始化 UI 类
        mainMenuPanel = new MainMenuPanel(this);
        settingPanel = new SettingPanel(this);
        loginPanel = new LoginPanel(this);
        startGamePanel = new StartGamePanel(this);
        // 默认显示主菜单
        
        showMainMenu();
        stage.show();
        System.out.println(stage.getWidth());
        System.out.println(stage.getHeight());
    }

    public Stage getStage() {
        return primaryStage;
    }
    
    // 切换界面（核心）
    public void setRootContent(javafx.scene.Node node) {
        root.getChildren().setAll(node);
    }

    public void showMainMenu() {
        setRootContent(mainMenuPanel.getContent());
    }
    public void showGamePanel(GameController gc) {
        AudioUtils.playBGM("/sounds/Blue light.mp3");
        setRootContent(new GamePanel(gc, this));
    }
    
    public void showSettings() {
        setRootContent(settingPanel.getContent());
    }
    public void showLogin() {
        setRootContent(loginPanel.getContent());
    }
    public AuthService getAuthService() {
        return authService;
    }
}
/*
1160 * 780
 */