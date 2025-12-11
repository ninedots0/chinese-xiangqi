package io.github.ninedots0.core.rule;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;

public class MoveValidator {

    public static boolean isValid(Board board, int fx, int fy, int tx, int ty) {
        Piece p = board.getPiece(fx, fy);
        Piece target = board.getPiece(tx, ty);
        boolean isRedPiece = p.getColor() == 1;
        if (p == null) return false;
        // 不能吃己方子
        if (target != null && target.getColor() == p.getColor())
            return false;

        switch (p.getType()) {
            case ROOK:
                return validRook(board, fx, fy, tx, ty);
            case CANNON:
                return validCannon(board, fx, fy, tx, ty);
            case HORSE:
                return validHorse(board, fx, fy, tx, ty);
            case ELEPHANT:
                return validElephant(board, fx, fy, tx, ty, isRedPiece);
            case ADVISOR:
                return validAdvisor(board, fx, fy, tx, ty, isRedPiece);
            case KING:
                return validKing(board, fx, fy, tx, ty, isRedPiece);
            case PAWN:
                return validPawn(board, fx, fy, tx, ty, isRedPiece);  
            default:
                return true;
        }
    }

    private static boolean validRook(Board board, int fx, int fy, int tx, int ty) {
        if (fx != tx && fy != ty) return false;

        if (fx == tx) { // 垂直
            int dir = ty > fy ? 1 : -1;
            for (int y = fy + dir; y != ty; y += dir)
                if (board.getPiece(fx, y) != null) return false;
        } else { // 水平
            int dir = tx > fx ? 1 : -1;
            for (int x = fx + dir; x != tx; x += dir)
                if (board.getPiece(x, fy) != null) return false;
        }
        return true;
    }
    
    private static boolean validCannon(Board board, int fx, int fy, int tx, int ty) {
        if (fx != tx && fy != ty) return false;

        int screenCount = 0; // 炮台数量

        if (fx == tx) { // 垂直
            int dir = ty > fy ? 1 : -1;
            for (int y = fy + dir; y != ty; y += dir)
                if (board.getPiece(fx, y) != null) screenCount++;
        } else { // 水平
            int dir = tx > fx ? 1 : -1;
            for (int x = fx + dir; x != tx; x += dir)
                if (board.getPiece(x, fy) != null) screenCount++;
        }

        Piece target = board.getPiece(tx, ty);
        if (target == null) {
            return screenCount == 0; 
        } else {
            return screenCount == 1; 
        }
    }
    private static boolean validHorse(Board board, int fx, int fy, int tx, int ty) {
        int dx = Math.abs(tx - fx);
        int dy = Math.abs(ty - fy);
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) return false;

        // 马脚
        if (dx == 2) {
            int blockX = (fx + tx) / 2;
            if (board.getPiece(blockX, fy) != null) return false;
        } else {
            int blockY = (fy + ty) / 2;
            if (board.getPiece(fx, blockY) != null) return false;
        }
        return true;
    }
    private static boolean validElephant(Board board, int fx, int fy, int tx, int ty, boolean isRed) {
        int dx = Math.abs(tx - fx);
        int dy = Math.abs(ty - fy);
        if (dx != 2 || dy != 2) return false;

        // 象眼
        int blockX = (fx + tx) / 2;
        int blockY = (fy + ty) / 2;
        if (board.getPiece(blockX, blockY) != null) return false;

        // 不过河
        if (isRed && ty < 5) return false;
        if (!isRed && ty > 4) return false;

        return true;
    }
    private static boolean validAdvisor(Board board, int fx, int fy, int tx, int ty, boolean isRed) {
        int dx = Math.abs(tx - fx);
        int dy = Math.abs(ty - fy);
        if (dx != 1 || dy != 1) return false;

        // 宫内
        if (tx < 3 || tx > 5) return false;
        if (isRed) {
            if (ty < 7 || ty > 9) return false;
        } else {
            if (ty < 0 || ty > 2) return false;
        }

        return true;
    }
    private static boolean validKing(Board board, int fx, int fy, int tx, int ty, boolean isRed) {
        int dx = Math.abs(tx - fx);
        int dy = Math.abs(ty - fy);
        if (!((dx == 1 && dy == 0) || (dx == 0 && dy == 1))) return false;

        // 宫
        if (tx < 3 || tx > 5) return false;
        if (isRed) {
            if (ty < 7 || ty > 9) return false;
        } else {
            if (ty < 0 || ty > 2) return false;
        }
        return true;
    }
    private static boolean validPawn(Board board, int fx, int fy, int tx, int ty, boolean isRed) {
        int dx = Math.abs(tx - fx);
        int dy = ty - fy;

        // 未过河前只能直走
        if (isRed && fy >= 5) {
            if (dx != 0 || dy != -1) return false;
        } else if (!isRed && fy <= 4) {
            if (dx != 0 || dy != 1) return false;
        } else {
            if (!((dx == 1 && dy == 0) || (dx == 0 && ((isRed && dy == -1) || (!isRed && dy == 1))))) {
                return false;
            }
        }
        return true;
    }
}
