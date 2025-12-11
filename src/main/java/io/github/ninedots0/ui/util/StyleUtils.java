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
}
