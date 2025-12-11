package io.github.ninedots0.core.game;

import java.util.Stack;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.rule.CheckmateDetector;
import io.github.ninedots0.core.rule.MoveValidator;
import io.github.ninedots0.core.save.SaveData;
import io.github.ninedots0.core.save.SaveManager;


public class GameController {
    private Board board;
    private int currentPlayer ;
    private Stack<GameState> history = new Stack<>();
    private CheckmateDetector checkmateDetector;
    private boolean gameOver = false;
    private String winner = null;
    public GameController(Board board, int currentPlayer) {
        this.board = board;
        this.checkmateDetector = new CheckmateDetector(board);
        this.history.clear();
        this.currentPlayer=currentPlayer;
        saveState();
    }
    public void applySaveData(SaveData data) {
        SaveManager.applySave(this, data);
        // 加载存档后需要重新初始化历史记录
        history.clear();
        saveState();
    }
    public int getCurrentPlayer() {return currentPlayer;}
    public void setCurrentPlayer(int currentPlayer) {this.currentPlayer = currentPlayer;}
    public Board getBoard() {return board;}
    public void setBoard(Board board) {this.board = board;}

    
    public boolean move(int fx, int fy, int tx, int ty) {
    if (gameOver){ 
    return false;}
    Piece movingPiece = board.getPiece(fx, fy);
    if (!validateMove(movingPiece, fx, fy, tx, ty)) {
        return false;
    }
    // 执行移动
    executeMove(fx, fy, tx, ty);
    // 切换玩家
    currentPlayer = -currentPlayer;
    // 在切换玩家后保存状态
    saveState();
    checkForGameEnd();
    return true;
}

    // 验证移动是否合法
    private boolean validateMove(Piece movingPiece, int fx, int fy, int tx, int ty) {
        if (movingPiece == null || movingPiece.getColor() != currentPlayer) {
            return false;
        }
        if (!MoveValidator.isValid(board, fx, fy, tx, ty)) {
            return false;
        }
        Piece targetPiece = board.getPiece(tx, ty);
        if (targetPiece != null && targetPiece.getColor() == movingPiece.getColor()) {
            return false;
        }
        return !checkmateDetector.wouldSendGeneral(movingPiece, fx, fy, tx, ty);
    }
    // 执行移动
    private void executeMove(int fx, int fy, int tx, int ty) {
        board.movePiece(fx, fy, tx, ty);
    }
    // 检查游戏是否结束
    private void checkForGameEnd() {
        // 检查红方
        String redStatus = checkmateDetector.detectCheckmateOrStalemate(true);
        if (redStatus != null && redStatus.contains("杀")) {
            gameOver = true;
            winner = "黑方";
            return;
        }
        // 检查黑方
        String blackStatus = checkmateDetector.detectCheckmateOrStalemate(false);
        if (blackStatus != null && blackStatus.contains("杀")) {
            gameOver = true;
            winner = "红方";
        }
    }
    public String getCheckmateType() {
        boolean isCurrentPlayerRed = currentPlayer == 1;
        return checkmateDetector.detectCheckmateOrStalemate(!isCurrentPlayerRed);
    }
    
    public boolean isInCheck(boolean isRed) {
        return checkmateDetector.isInCheck(isRed);
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public String getWinner() {
        return winner;
    }
    public String getCheckStatus() {
        boolean isRed = currentPlayer == 1;
        if (checkmateDetector.isInCheck(isRed)) {
            String checkmateType = getCheckmateType();
            if (checkmateType != null && checkmateType.contains("杀")) {
                return checkmateType;
            }
            return "将军";
        }
        return null;
    }
    
    public CheckmateDetector getCheckmateDetector() {
        return checkmateDetector;
    }
    
    public boolean canUndo() {
        return history.size() > 1;
    }
   //悔棋
   public boolean undo() {
    if (history.size() <= 1){return false;}
    // 弹出当前状态
    history.pop();
    // 获取上一步状态
    GameState previousState = history.peek();
    restoreState(previousState);  
    gameOver = false;
    winner = null;
    return true;
}
    
    private void saveState() {
        history.push(new GameState(board.deepCopy(), currentPlayer));
    }
    
    private void restoreState(GameState state) {
        this.board.restoreFrom(state.board);
        this.currentPlayer = state.player;
        this.checkmateDetector = new CheckmateDetector(board);
    }
    private static class GameState {
        Board board;
        int player;
        GameState(Board board, int player) {
            this.board = board;
            this.player = player;
        }
    }
}


    
    
    
    


    
    