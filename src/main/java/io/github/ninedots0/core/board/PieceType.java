package io.github.ninedots0.core.board;
import javafx.scene.image.Image;
import io.github.ninedots0.ui.util.*;
public enum PieceType {
    KING, ADVISOR, ELEPHANT, HORSE, ROOK, CANNON, PAWN;

    private Image redImg;
    private Image blackImg;

    public static void initPieceImages() {
        for(PieceType t : values()) {
            t.redImg  = ImageUtils.load("red_" + t.name().toLowerCase() + ".jpg");
            t.blackImg = ImageUtils.load("black_"  + t.name().toLowerCase() + ".jpg");
        }
    }

    public Image getImage(boolean isRed) {
        return isRed ? redImg : blackImg;
    }
}
