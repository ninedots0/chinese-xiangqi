// OpeningDetector.java - 修复坐标判断
package io.github.ninedots0.core.rule;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.board.PieceType;

public class OpeningDetector {
    private int moveCount = 0;
    private boolean openingDetected = false;
    
    public String detectOpening(Piece piece, int fx, int fy, int tx, int ty, boolean isRedTurn) {
        if (moveCount >= 2 || openingDetected) {
            return null;
        }
        moveCount++;
        // 红方开局检测（第一步）
        if (isRedTurn && moveCount == 1) {
            return detectRedOpening(piece, fx, fy, tx, ty);
        }
        return null;
    }
    private String detectRedOpening(Piece piece, int fx, int fy, int tx, int ty) {
        PieceType type = piece.getType();
        // 当头炮 
        if (type == PieceType.CANNON) {
            // 红方炮初始位置在(1,7)和(7,7)
            if ((fx == 1 && fy == 7 && tx == 4 && ty == 7) ||(fx == 7 && fy == 7 && tx == 4 && ty == 7)) { // 左炮平中
                openingDetected = true;
                return "当头炮";
            }
        }
        if (type == PieceType.PAWN) {
            // 红兵初始在y=6
            if ((fy == 6 && ty == 5 && fx == 2||tx==2)||(fy==6&&ty==5&&fx==6&&tx==6)) { // 直进一格
                openingDetected = true;
                return "仙人指路";
            }
        }
        
        // 飞相局 - 相飞到中路
        if (type == PieceType.ELEPHANT) {
            // 红相初始在(2,9)和(6,9)
            if ((fx == 2 && fy == 9 && tx == 4 && ty == 7) || // 右相飞中
                (fx == 6 && fy == 9 && tx == 4 && ty == 7)) { // 左相飞中
                openingDetected = true;
                return "飞相局";
            } 
           if((fx == 2 && fy == 9 && tx == 0 && ty == 7) || // 右相飞边
                (fx == 6 && fy == 9 && tx == 8 && ty == 7)) { // 左相飞边
                openingDetected = true;
                return "边相局";
            }
        }
        
        // 起马局 - 马跳到河口
        if (type == PieceType.HORSE) {
            // 红马初始在(1,9)和(7,9)
            if ((fx == 1 && fy == 9 && tx == 2 && ty == 7) || // 右马跳到河口
                (fx == 7 && fy == 9 && tx == 6 && ty == 7)) { // 左马跳到河口
                openingDetected = true;
                return "起马局";
            }
        }
        
        // 过宫炮 - 炮过宫
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==5&&ty==7)||(fx==7&&fy==7&&tx==3&&ty==7)){
            openingDetected = true;
            return "过宫炮";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==3&&ty==7)||(fx==7&&fy==7&&tx==5&&ty==7)){
            openingDetected = true;
            return "仕角炮";
            }
        }
        if (type == PieceType.ADVISOR ) {
            if((fx==3&&fy==9&&tx==4&&ty==8)||(fx==5&&fy==9&&tx==4&&ty==8)){
            openingDetected = true;
            return "上仕局";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==2&&ty==7)||(fx==7&&fy==7&&tx==6&&ty==7)){
            openingDetected = true;
            return "兵底炮";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==6&&ty==7)||(fx==7&&fy==7&&tx==2&&ty==7)){
            openingDetected = true;
            return "金钩炮";
            }
        }
        if (type == PieceType.HORSE ) {
            if((fx==1&&fy==9&&tx==0&&ty==7)||(fx==7&&fy==9&&tx==8&&ty==7)){
            openingDetected = true;
            return "边马局";
            }
        }
        if (type == PieceType.PAWN ) {
            if((fx==0&&fy==6&&tx==0&&ty==5)||(fx==8&&fy==6&&tx==8&&ty==5)){
            openingDetected = true;
            return "九尾龟";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==1&&ty==5)||(fx==7&&fy==7&&tx==7&&ty==5)){
            openingDetected = true;
            return "巡河炮";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==1&&ty==3)||(fx==7&&fy==7&&tx==7&&ty==3)){
            openingDetected = true;
            return "过河炮";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==0&&ty==7)||(fx==7&&fy==7&&tx==8&&ty==7)){
            openingDetected = true;
            return "边炮局";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==1&&ty==8)||(fx==7&&fy==7&&tx==7&&ty==8)){
            openingDetected = true;
            return "龟背炮";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==5&&ty==7)||(fx==7&&fy==7&&tx==3&&ty==7)){
            openingDetected = true;
            return "过宫炮";
            }
        }
        if (type == PieceType.PAWN ) {
            if((fx==4&&fy==6&&tx==4&&ty==5)){
            openingDetected = true;
            return "中兵局";
            }
        }
        if (type == PieceType.KING ) {
            if(fx==4&&fy==9&&tx==4&&ty==8){
            openingDetected = true;
            return "创新走法";
            }
        }
        if (type == PieceType.ROOK ) {
            if((fx==0&&fy==9&&tx==0&&ty==8)||(fx==8&&fy==9&&tx==8&&ty==8)
            ||(fx==0&&fy==9&&tx==0&&ty==7)||(fx==8&&fy==9&&tx==8&&ty==7)){
            openingDetected = true;
            return "创新走法";
            }
        }
        if (type == PieceType.CANNON ) {
            if((fx==1&&fy==7&&tx==1&&ty==6)||(fx==7&&fy==7&&tx==7&&ty==6)||
            (fx==1&&fy==7&&tx==1&&ty==4)||(fx==7&&fy==7&&tx==7&&ty==4)){
            openingDetected = true;
            return "创新走法";
            }
        }
        return null;
    }
    public void reset() {
        moveCount = 0;
        openingDetected = false;
    }
    public int getMoveCount() {
        return moveCount;
    }
    public boolean isOpeningDetected() {
        return openingDetected;
    }
}