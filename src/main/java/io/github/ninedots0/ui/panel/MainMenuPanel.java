package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.auth.AuthService;
import io.github.ninedots0.ui.frame.MainFrame;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import io.github.ninedots0.ui.util.StyleUtils;

public class MainMenuPanel {

    private MainFrame mainFrame;
    private AuthService authService;

    public MainMenuPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.authService = frame.getAuthService(); 
    }

    public BorderPane getContent() {
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Label title = new Label("Chinese Xiangqi");
        title.setStyle("-fx-font-size: 31px;" +
        "fx-font-family: 隶书" + 
        "-fx-font-weight: bold;");

        Button startBtn = new Button("Start");
        Button settingBtn = new Button("Setting");
        Button loginBtn = new Button("Login / Register");

        StyleUtils.applyMainMenuButton(startBtn);
        StyleUtils.applyMainMenuButton(settingBtn);
        StyleUtils.applyMainMenuButton(loginBtn);

        startBtn.setOnAction(e -> {
            mainFrame.setRootContent(new StartGamePanel(mainFrame));
        });
        settingBtn.setOnAction(e -> mainFrame.showSettings());
        loginBtn.setOnAction(e -> mainFrame.showLogin());

        Label account = new Label();
        if (authService.getCurrentUser()  == null) account.setText("guest");

        centerBox.getChildren().addAll(title, startBtn);
        if (authService.getCurrentUser() == null) 
            centerBox.getChildren().add(loginBtn);
        centerBox.getChildren().add(settingBtn);
        
        HBox bottomBox = new HBox();
        Button accountBtn = new Button("account");
        StyleUtils.applyMainMenuButton(accountBtn);
        bottomBox.getChildren().add(accountBtn);

        BorderPane root = new BorderPane();
        root.setStyle(
            "-fx-background-image: url('/images/bg.png');" +
            "-fx-background-size: 100% 100%;"  + // 拉伸填满
            "-fx-background-repeat: no-repeat;"  // 不重复
        );
        root.setCenter(centerBox);
        if (authService.getCurrentUser() != null) root.setBottom(bottomBox);

        return root;
    }
}
