package io.github.ninedots0.ui.util;

import javafx.scene.image.Image;
import java.io.InputStream;

public class ImageUtils {

    public static Image load(String name) {
        InputStream is = ImageUtils.class.getResourceAsStream("/images/" + name);
        return new Image(is);
    }
}
