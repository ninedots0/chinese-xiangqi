package io.github.ninedots0.core.board;

import javafx.scene.image.Image;

public class Piece {
    private PieceType type;
    private boolean isRed;

    public Piece(PieceType type, boolean isRed) {
        this.type = type;
        this.isRed = isRed;
    }
    public PieceType getType() {return type;}
    public boolean isRed() {return isRed;}

    public Image getImage() {return type.getImage(isRed);}
    public int getColor() {return isRed? 1 : -1;}

}
