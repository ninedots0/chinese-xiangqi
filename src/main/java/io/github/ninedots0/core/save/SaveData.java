package io.github.ninedots0.core.save;

public class SaveData {

    public static class PieceData {
        public String type;   // KING / ROOK / CANNON …
        public boolean isRed; // 红黑
        public int x;         // 0~8
        public int y;         // 0~9
    }

    public PieceData[] pieces;
    public int currentPlayer;   // 是否轮到红方
}
