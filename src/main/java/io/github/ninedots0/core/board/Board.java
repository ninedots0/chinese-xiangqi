package io.github.ninedots0.core.board;

public class Board {

    private Piece[][] grid = new Piece[9][10];

    public Board() {
        initBoard();
    }
    public void clear() {
        grid = new Piece[9][10];
    }

    public void setPiece(int x, int y, Piece p) {
        grid[x][y] = p;
    }

    public void initBoard() {
        // 清空
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                grid[x][y] = null;
            }
        }

        // ---- 红方 (y = 9, 7, 6) ----
        grid[4][9] = new Piece(PieceType.KING, true);            // 帅
        grid[3][9] = new Piece(PieceType.ADVISOR, true);         // 仕
        grid[5][9] = new Piece(PieceType.ADVISOR, true);
        grid[2][9] = new Piece(PieceType.ELEPHANT, true);        // 相
        grid[6][9] = new Piece(PieceType.ELEPHANT, true);
        grid[1][9] = new Piece(PieceType.HORSE, true);           // 马
        grid[7][9] = new Piece(PieceType.HORSE, true);
        grid[0][9] = new Piece(PieceType.ROOK, true);            // 车
        grid[8][9] = new Piece(PieceType.ROOK, true);
        grid[1][7] = new Piece(PieceType.CANNON, true);          // 炮
        grid[7][7] = new Piece(PieceType.CANNON, true);

        // 红兵（卒）
        grid[0][6] = new Piece(PieceType.PAWN, true);
        grid[2][6] = new Piece(PieceType.PAWN, true);
        grid[4][6] = new Piece(PieceType.PAWN, true);
        grid[6][6] = new Piece(PieceType.PAWN, true);
        grid[8][6] = new Piece(PieceType.PAWN, true);

        // ---- 黑方 (y = 0, 2, 3) ----
        grid[4][0] = new Piece(PieceType.KING, false);           // 将
        grid[3][0] = new Piece(PieceType.ADVISOR, false);        // 士
        grid[5][0] = new Piece(PieceType.ADVISOR, false);
        grid[2][0] = new Piece(PieceType.ELEPHANT, false);       // 象
        grid[6][0] = new Piece(PieceType.ELEPHANT, false);
        grid[1][0] = new Piece(PieceType.HORSE, false);          // 马
        grid[7][0] = new Piece(PieceType.HORSE, false);
        grid[0][0] = new Piece(PieceType.ROOK, false);           // 车
        grid[8][0] = new Piece(PieceType.ROOK, false);
        grid[1][2] = new Piece(PieceType.CANNON, false);         // 炮
        grid[7][2] = new Piece(PieceType.CANNON, false);

        // 黑兵（卒）
        grid[0][3] = new Piece(PieceType.PAWN, false);
        grid[2][3] = new Piece(PieceType.PAWN, false);
        grid[4][3] = new Piece(PieceType.PAWN, false);
        grid[6][3] = new Piece(PieceType.PAWN, false);
        grid[8][3] = new Piece(PieceType.PAWN, false);
    }


    public boolean inBounds(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 10;
    }

    public Piece getPiece(int x, int y) {
        return grid[x][y];
    }

    public void movePiece(int fx, int fy, int tx, int ty) {
        grid[tx][ty] = grid[fx][fy];
        grid[fx][fy] = null;
    }


    //拷贝棋盘状态
    public Board deepCopy() {
        Board copy = new Board();
        copy.grid = new Piece[9][10];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                Piece original = this.grid[x][y];
                if (original != null) {
                    copy.grid[x][y] = new Piece(original.getType(), original.isRed());
                }
            }
        }
        return copy;
    }
    // 从另一个棋盘恢复
    public void restoreFrom(Board other) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                Piece original = other.grid[x][y];
                if (original != null) {
                    this.grid[x][y] = new Piece(original.getType(), original.isRed());
                } else {
                    this.grid[x][y] = null;
                }
            }
        }
    }
}



