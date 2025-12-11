package io.github.ninedots0.core.rule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.board.PieceType;

public class CheckmateDetector {

    private Board board;
    private MoveValidator moveValidator;

    public CheckmateDetector(Board board) {
        this.board = board;
        this.moveValidator = new MoveValidator();
    }
    
    public String detectCheckmateOrStalemate(boolean isRed) {
    boolean inCheck = isInCheck(isRed);
    boolean hasLegalMove = hasAnyLegalMove(isRed);
    if (inCheck) {
        if (!hasLegalMove) {
            return detectSpecificCheckmateType(isRed);
        }
        return "将军";  
    } else {
        if (!hasLegalMove) {
            return "困毙杀";
        }
        return null; 
    }
}
    public String detectCheckmate(boolean isRedAttacker) {
        return detectCheckmateOrStalemate(!isRedAttacker);
    }
    public boolean isCheckmate(boolean isRed) {
        return isInCheck(isRed) && !hasAnyEscapeMove(isRed);
    }
    //检查是否被将军
    public boolean isInCheck(boolean isRed) {
        int[] kingPos = findKingPosition(isRed);
        if (kingPos == null) return false;
        int attackerColor = isRed ? -1 : 1;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor() == attackerColor) {
                    if (canAttack(piece, kingPos[0], kingPos[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //检查是否有任何逃脱移动
    private boolean hasAnyEscapeMove(boolean isRed) {
        // 1. 将移动躲避
        if (canKingMoveToEscape(isRed)) {
            return true;
        }
        // 2. 吃掉攻击者
        if (canCaptureAttacker(isRed)) {
            return true;
        }
        // 3. 挡住攻击路线
        if (canBlockAttack(isRed)) {
            return true;
        }
        return false;
    }
    
    //检查将能否移动躲避
    private boolean canKingMoveToEscape(boolean isRed) {
        int[] kingPos = findKingPosition(isRed);
        if (kingPos == null) return false;
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] move : moves) {
            int nx = kingPos[0] + move[0];
            int ny = kingPos[1] + move[1];
            if (isInPalace(nx, ny, isRed)) {
                Piece target = board.getPiece(nx, ny);
                if (target == null || target.getColor() != (isRed ? 1 : -1)) {
                    if (isMoveSafeForKing(isRed, kingPos[0], kingPos[1], nx, ny)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //检查能否吃掉攻击者
    private boolean canCaptureAttacker(boolean isRed) {
        int[] kingPos = findKingPosition(isRed);
        if (kingPos == null) return false;
        
        // 找到所有攻击将的棋子
        List<int[]> attackers = findAttackers(kingPos[0], kingPos[1], isRed);
        
        for (int[] attackerPos : attackers) {
            if (canBeCaptured(attackerPos[0], attackerPos[1], isRed ? 1 : -1)) {
                return true;
            }
        }
        return false;
    }
    
    //检查能否挡住攻击
    private boolean canBlockAttack(boolean isRed) {
        int[] kingPos = findKingPosition(isRed);
        if (kingPos == null) return false;
        
        List<int[]> attackers = findAttackers(kingPos[0], kingPos[1], isRed);
        
        for (int[] attackerPos : attackers) {
            Piece attacker = board.getPiece(attackerPos[0], attackerPos[1]);
            if (attacker != null && (attacker.getType() == PieceType.ROOK || 
            attacker.getType() == PieceType.CANNON)) {
                List<int[]> attackLine = getAttackLine(attacker, attackerPos[0], attackerPos[1], kingPos[0], kingPos[1]);
                for (int[] blockPos : attackLine) {
                    if (canPlaceBlocker(blockPos[0], blockPos[1], isRed ? 1 : -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    //查找将/帅位置
    private int[] findKingPosition(boolean isRed) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getType() == PieceType.KING && 
                    piece.isRed() == isRed) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }
    
    //检查是否在九宫内
    private boolean isInPalace(int x, int y, boolean isRed) {
        return x >= 3 && x <= 5 && ((isRed && y >= 7 && y <= 9) || (!isRed && y >= 0 && y <= 2));
    }
    
    //检查棋子是否能攻击目标
    private boolean canAttack(Piece piece, int tx, int ty) {
        int[] pos = findPiecePosition(piece);
        if (pos == null) return false;
        
        return moveValidator.isValid(board, pos[0], pos[1], tx, ty);
    }
    
    //查找棋子位置
    private int[] findPiecePosition(Piece piece) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                if (board.getPiece(x, y) == piece) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }
    
    //查找所有攻击者
    private List<int[]> findAttackers(int kx, int ky, boolean isRedKing) {
        List<int[]> attackers = new ArrayList<>();
        int attackerColor = isRedKing ? -1 : 1;
        
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor() == attackerColor) {
                    if (canAttack(piece, kx, ky)) {
                        attackers.add(new int[]{x, y});
                    }
                }
            }
        }
        return attackers;
    }
    
    //检查能否被吃掉
    private boolean canBeCaptured(int tx, int ty, int playerColor) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor() == playerColor) {
                    if (moveValidator.isValid(board, x, y, tx, ty)) {
                        if (isMoveSafeForPiece(piece, x, y, tx, ty)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    //获取攻击线路
    private List<int[]> getAttackLine(Piece attacker, int ax, int ay, int kx, int ky) {
        List<int[]> line = new ArrayList<>();
        
        if (ax == kx) { // 垂直
            int step = ay < ky ? 1 : -1;
            for (int y = ay + step; y != ky; y += step) {
                line.add(new int[]{ax, y});
            }
        } else if (ay == ky) { // 水平
            int step = ax < kx ? 1 : -1;
            for (int x = ax + step; x != kx; x += step) {
                line.add(new int[]{x, ay});
            }
        }
        return line;
    }
    
    //检查能否放置挡子
    private boolean canPlaceBlocker(int bx, int by, int playerColor) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor() == playerColor) {
                    if (moveValidator.isValid(board, x, y, bx, by)) {
                        if (isMoveSafeForPiece(piece, x, y, bx, by)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
     //困毙杀类型检测
private boolean hasAnyLegalMove(boolean isRed) {
    int playerColor = isRed ? 1 : -1;
    
    // 遍历所有棋子
    for (int y = 0; y < 10; y++) {
        for (int x = 0; x < 9; x++) {
            Piece piece = board.getPiece(x, y);
            if (piece != null && piece.getColor() == playerColor) {
                // 根据棋子类型检查可能的移动方向
                List<int[]> possibleMoves = getPossibleMovesForPiece(piece, x, y);
                for (int[] move : possibleMoves) {
                    int tx = move[0];
                    int ty = move[1];
                    // 验证移动是否合法且安全
                    if (moveValidator.isValid(board, x, y, tx, ty)) {
                        Piece target = board.getPiece(tx, ty);
                        if (target == null || target.getColor() != piece.getColor()) {
                            if (isMoveSafeForPiece(piece, x, y, tx, ty)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
    return false;
}

// 获取棋子所有可能移动位置
private List<int[]> getPossibleMovesForPiece(Piece piece, int x, int y) {
    List<int[]> moves = new ArrayList<>();
    PieceType type = piece.getType();
    switch (type) {
        case KING:
            // 将/帅：上下左右一步
            moves.add(new int[]{x-1, y});
            moves.add(new int[]{x+1, y});
            moves.add(new int[]{x, y-1});
            moves.add(new int[]{x, y+1});
            break;
        case ADVISOR:
            // 士/仕：斜线一步
            moves.add(new int[]{x-1, y-1});
            moves.add(new int[]{x-1, y+1});
            moves.add(new int[]{x+1, y-1});
            moves.add(new int[]{x+1, y+1});
            break;
        case ELEPHANT:
            // 象/相：田字，有象眼
            moves.add(new int[]{x-2, y-2});
            moves.add(new int[]{x-2, y+2});
            moves.add(new int[]{x+2, y-2});
            moves.add(new int[]{x+2, y+2});
            break;
        case HORSE:
            // 马：8个方向，有马腿
            int[][] horseMoves = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
            for (int[] move : horseMoves) {
                moves.add(new int[]{x + move[0], y + move[1]});
            }
            break;
        case ROOK:
        case CANNON:
            // 车/炮：直线所有位置
            for (int i = 0; i < 9; i++) if (i != x) moves.add(new int[]{i, y});
            for (int j = 0; j < 10; j++) if (j != y) moves.add(new int[]{x, j});
            break;
        case PAWN:
            // 兵/卒：根据是否过河
            boolean isRed = piece.getColor() == 1;
            if (isRed) {
                moves.add(new int[]{x, y-1});  // 红兵前进
                if (y <= 4) {  // 过河后
                    moves.add(new int[]{x-1, y});
                    moves.add(new int[]{x+1, y});
                }
            } else {
                moves.add(new int[]{x, y+1});  // 黑卒前进
                if (y >= 5) {  // 过河后
                    moves.add(new int[]{x-1, y});
                    moves.add(new int[]{x+1, y});
                }
            }
            break;
    }
    return moves.stream()
            .filter(pos -> pos[0] >= 0 && pos[0] < 9 && pos[1] >= 0 && pos[1] < 10)
            .collect(Collectors.toList());
}
    
   // 安全移动检查
    private boolean isMoveSafeForPiece(Piece piece, int fx, int fy, int tx, int ty) {
        return isMoveSafe(piece.getColor() == 1, fx, fy, tx, ty, piece);
    }
    
    private boolean isMoveSafeForKing(boolean isRed, int fx, int fy, int tx, int ty) {
        return isMoveSafe(isRed, fx, fy, tx, ty, board.getPiece(fx, fy));
    }
   
    private boolean isMoveSafe(boolean isRed, int fx, int fy, int tx, int ty, Piece movingPiece) {
        Piece targetPiece = board.getPiece(tx, ty);
        
        // 模拟移动
        board.movePiece(fx, fy, tx, ty);
        
        // 检查将对将
        boolean facingKings = isFacingKings();
        
        // 检查是否被将军
        boolean inCheck = isInCheck(isRed);
        
        // 恢复棋盘
        board.movePiece(tx, ty, fx, fy);
        if (targetPiece != null) {
            restorePiece(tx, ty, targetPiece);
        }
        return !facingKings && !inCheck;
    }
    
    // 恢复棋子
    private void restorePiece(int x, int y, Piece piece) {
        try {
            java.lang.reflect.Field gridField = Board.class.getDeclaredField("grid");
            gridField.setAccessible(true);
            Piece[][] grid = (Piece[][]) gridField.get(board);
            grid[x][y] = piece;
        } catch (Exception e) {
            System.err.println("恢复棋子失败: " + e.getMessage());
        }
    }
    
    // 检查是否对面将军
    public boolean isFacingKings() {
        int[] redKing = findKingPosition(true);
        int[] blackKing = findKingPosition(false);
        if (redKing == null || blackKing == null || redKing[0] != blackKing[0]) {
            return false;
        }
        int startY = Math.min(redKing[1], blackKing[1]) + 1;
        int endY = Math.max(redKing[1], blackKing[1]);
        
        for (int y = startY; y < endY; y++) {
            if (board.getPiece(redKing[0], y) != null) {
                return false;
            }
        }
        return true;
    }

    // 检查移动后是否会将军
    public boolean wouldSendGeneral(Piece piece, int fx, int fy, int tx, int ty) {
        boolean isRed = piece.getColor() == 1;
        boolean isCurrentlyInCheck = isInCheck(isRed);
        
        Piece targetPiece = board.getPiece(tx, ty);
        board.movePiece(fx, fy, tx, ty);
        
        boolean facingKingsAfterMove = isFacingKings();
        boolean inCheckAfterMove = isInCheck(isRed);
        
        board.movePiece(tx, ty, fx, fy);
        if (targetPiece != null) {
            restorePiece(tx, ty, targetPiece);
        }
        if (facingKingsAfterMove) {
            return true;
        }
        return isCurrentlyInCheck ? inCheckAfterMove : inCheckAfterMove;
    }
    
   
    
    private String detectSpecificCheckmateType(boolean isRedAttacker) {
        if (isFacingKings()) {
            return "对面笑杀";
        }
        return "绝杀";
    }
    public boolean checkIfInCheck(boolean isRed) {
        return isInCheck(isRed);
    }
}