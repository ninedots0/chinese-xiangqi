package io.github.ninedots0.ui.util;

import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class StyleUtils {

    public static void applyMainMenuButton(Button btn) { // 为主界面的button设置style
        btn.setStyle("""
            -fx-padding: 10 20;
            -fx-font-size: 20px;
            -fx-background-color: transparent;
            -fx-text-fill: white;

            /* 去掉默认按钮边框与聚焦边框 */
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """);

        // Hover 效果 —— JavaFX 需要用 pseudoClassState 来写
        btn.setOnMouseEntered(e ->
            btn.setStyle("""
                -fx-padding: 10 20;
                -fx-font-size: 20px;
                -fx-background-color: rgba(255,255,255,0.15);
                -fx-text-fill: white;

                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
            """)
        );

        btn.setOnMouseExited(e ->
            btn.setStyle("""
                -fx-padding: 10 20;
                -fx-font-size: 20px;
                -fx-background-color: transparent;
                -fx-text-fill: white;

                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
            """)
        );
    }
    public static void applyLogin_BottomLeft(HBox bottomLeft) { // Login界面的左下角pane样式
        bottomLeft.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeft.setPadding(new javafx.geometry.Insets(10));
    }
    public static void applyLogin_LoginButton(Button btn) { // Login界面的 登录 和 注册 按钮样式
        btn.setStyle(
            "-fx-background-color: #adb5bd; -fx-text-fill: white;" +
            "-fx-padding: 10 24;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 12;"
        );
    }
    public static void applyGamePanelButton(Button btn) {

    // ===== 默认：暗绿背景 · 米白文字 =====
    String BASE_STYLE = """
        -fx-background-color: rgba(20, 35, 28, 0.65);
        -fx-border-color: rgba(94, 124, 106, 0.75);
        -fx-border-width: 1;
        -fx-border-radius: 0;
        -fx-background-radius: 0;

        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-font-family: "Microsoft YaHei", "PingFang SC", "Serif";
        -fx-text-fill: #E6E8E3;

        -fx-padding: 9 40;
        -fx-min-width: 170;
        -fx-cursor: hand;
    """;

    // ===== 悬浮：青绿高光（不发白） =====
    String HOVER_STYLE = """
        -fx-background-color: rgba(32, 58, 46, 0.85);
        -fx-border-color: rgba(138, 178, 155, 0.95);
        -fx-border-width: 1.2;
        -fx-border-radius: 0;
        -fx-background-radius: 0;

        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-font-family: "Microsoft YaHei", "PingFang SC", "Serif";
        -fx-text-fill: #F2F4EE;

        -fx-padding: 9 40;
        -fx-min-width: 170;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(120,180,150,0.35), 10, 0.3, 0, 0);
    """;

    // ===== 按下：实心落子感 =====
    String PRESSED_STYLE = """
        -fx-background-color: rgba(18, 45, 34, 0.95);
        -fx-border-color: rgba(170, 210, 185, 1.0);
        -fx-border-width: 1.4;
        -fx-border-radius: 0;
        -fx-background-radius: 0;

        -fx-font-size: 15px;
        -fx-font-weight: bold;
        -fx-font-family: "Microsoft YaHei", "PingFang SC", "Serif";
        -fx-text-fill: #FFFFFF;

        -fx-padding: 10 40 8 40;
        -fx-min-width: 170;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0.35, 0, 1);
    """;

    // 初始
    btn.setStyle(BASE_STYLE);
    btn.setFocusTraversable(false);

    // Hover
    btn.setOnMouseEntered(e -> btn.setStyle(HOVER_STYLE));
    btn.setOnMouseExited(e -> btn.setStyle(BASE_STYLE));

    // Press
    btn.setOnMousePressed(e -> btn.setStyle(PRESSED_STYLE));
    btn.setOnMouseReleased(e -> btn.setStyle(HOVER_STYLE));
}




}
