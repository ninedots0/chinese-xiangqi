package io.github.ninedots0.ui.util;

import javafx.stage.Stage;

public class WindowUtils {

    public static void initStageProperties(Stage stage, String title, boolean resizable, boolean maximized) {
        stage.setTitle(title);
        stage.setResizable(resizable);
        stage.setMaximized(maximized);
    }
}
