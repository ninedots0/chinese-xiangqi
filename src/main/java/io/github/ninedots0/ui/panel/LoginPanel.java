package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.auth.AuthService;
import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.ui.util.StyleUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class LoginPanel {

    private MainFrame mainFrame;
    private AuthService authService;

    public LoginPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.authService = frame.getAuthService(); 
    }

    public BorderPane getContent() {

    // ===== 最外层 BorderPane，用来将返回按钮放左下角 =====
        BorderPane root = new BorderPane();
        root.setStyle(
            "-fx-background-image: url('/images/setting.jpg');" +
            "-fx-background-size: 100% 100%;"  + // 拉伸填满
            "-fx-background-repeat: no-repeat;"  // 不重复
        );
        // ===== 中间区域：登录内容 =====
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Label title = new Label("用户登录");
        title.setStyle("-fx-font-family: \"Source Han Serif SC\";-fx-text-fill: #E6EFEA;-fx-font-size: 26px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);
        usernameField.setMaxWidth(300);
        usernameField.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);
        passwordField.setMaxWidth(300);
        passwordField.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        StyleUtils.applyLogin_LoginButton(loginBtn);
        StyleUtils.applyLogin_LoginButton(registerBtn);
        
        Label msg = new Label();

        loginBtn.setOnAction(e -> {
            boolean ok = authService.login(usernameField.getText(), passwordField.getText());
            if (ok) {msg.setText("登录成功！");mainFrame.showMainMenu();}
            else msg.setText("用户名或密码错误！");
        });

        registerBtn.setOnAction(e -> {
            int ok = authService.register(usernameField.getText(),passwordField.getText());
            if (ok == 0) msg.setText("用户名密码不能为空");
            else if (ok == 1) msg.setText("用户已存在");
            else msg.setText("注册成功");
        });

        HBox loginAndRegister = new HBox(15, loginBtn, registerBtn);
        centerBox.getChildren().addAll(title, usernameField, passwordField, loginAndRegister, msg);
        loginAndRegister.setAlignment(Pos.CENTER);

        // ===== 左下角按钮 =====
        Button backBtn = new Button("返回主菜单");
        StyleUtils.applyMainMenuButton(backBtn);
        backBtn.setOnAction(e -> mainFrame.showMainMenu());

        HBox bottomLeft = new HBox(backBtn);
        StyleUtils.applyLogin_BottomLeft(bottomLeft);
        
        // 放入 BorderPane
        root.setCenter(centerBox); root.setBottom(bottomLeft);

        return root;
    }

}
