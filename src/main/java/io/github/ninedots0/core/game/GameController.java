package io.github.ninedots0.core.game;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.rule.MoveValidator;
import io.github.ninedots0.core.save.SaveData;
import io.github.ninedots0.core.save.SaveManager;

public class GameController {

    private Board board;
    private int currentPlayer; // 红先走

    public GameController(Board board, int currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
    }
    public void applySaveData(SaveData data) {
        SaveManager.applySave(this, data);
    }


    public int getCurrentPlayer() {return currentPlayer;}
    public void setCurrentPlayer(int currentPlayer) {this.currentPlayer = currentPlayer;}
    public Board getBoard() {return board;}
    public void setBoard(Board board) {this.board = board;}

    public boolean move(int fx, int fy, int tx, int ty) {
        if (!MoveValidator.isValid(board, fx, fy, tx, ty)) {
            return false;
        }

        board.movePiece(fx, fy, tx, ty);
        currentPlayer = -currentPlayer; // 切换回合
        return true;
    }
}
