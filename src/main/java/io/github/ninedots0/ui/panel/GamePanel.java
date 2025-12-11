package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.board.*;
import io.github.ninedots0.core.game.GameController;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.ui.util.*;
import io.github.ninedots0.ui.frame.MainFrame;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class GamePanel extends Pane {

    private static final int TILE_SIZE = 60;
    private static final int PIECE_SIZE = 50;
    MainFrame mainFrame;
    private Canvas canvas;
    private Board board;
    private GameController gameController;

    private int selectedX = -1;
    private int selectedY = -1;

    private Image boardImg;

    public GamePanel(GameController gameController1, MainFrame mainFrame1) {
        board = gameController1.getBoard(); gameController = gameController1;
        mainFrame = mainFrame1;
        boardImg = ImageUtils.load("board.png");
        this.setStyle(
            "-fx-background-image: url('/images/board.png');" +
            "-fx-background-size: 100% 100%;"  + // 拉伸填满
            "-fx-background-repeat: no-repeat;"  // 不重复
        );

        canvas = new Canvas(600, 700);
        canvas.setLayoutX(200); canvas.setLayoutY(10);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);

        Button btn = new Button("存档");
        btn.setOnAction(e -> {
            if (mainFrame.getAuthService().getCurrentUser() != null) {
                try {
                    
                    String username = mainFrame.getAuthService().getCurrentUser().getUsername();
                    SaveManager.addNum(username);
                    String path = "saves/" + username + "/save" + SaveManager.getNum(username) + ".json";
                    // Sys
                    // System.out.println(path);
                    SaveManager.save(gameController, path);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backBtn = new Button("返回");
        backBtn.setOnAction(e -> {mainFrame.showMainMenu();});
        backBtn.setLayoutY(50);


        this.getChildren().addAll(canvas, btn, backBtn);

        loadResources();
        draw();
        
    }

    private void loadResources() {
        boardImg = ImageUtils.load("board.png");
        PieceType.initPieceImages();
    }
    private void handleClick(MouseEvent e) {
        

        int x = (int) e.getX() / TILE_SIZE;
        int y = (int) e.getY() / TILE_SIZE;
        if (x * TILE_SIZE + PIECE_SIZE < e.getX()) return;
        if (y * TILE_SIZE + PIECE_SIZE < e.getY()) return;
        if (!board.inBounds(x, y)) return;
        Piece clicked = board.getPiece(x, y);

        if (selectedX == -1 || (clicked != null && clicked.getColor() == gameController.getCurrentPlayer())) {
            // 没选 → 选子
            if (clicked != null &&
                clicked.getColor() == gameController.getCurrentPlayer()) {

                selectedX = x;
                selectedY = y;
            }
        } else {
            // 已选 → 尝试走子
            System.out.printf("sd");
            if (gameController.move(selectedX, selectedY, x, y)) {
                System.out.println("Move success!");
            }
            selectedX = -1;  // 重置
        }

        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, 600, 700);

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board.getPiece(x, y);
                if (p != null) {
                    gc.drawImage(
                        p.getImage(),
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        PIECE_SIZE,
                        PIECE_SIZE
                    );
                }
            }
        }

        if (selectedX != -1) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeOval(
                selectedX * TILE_SIZE,
                selectedY * TILE_SIZE,
                PIECE_SIZE + 4, PIECE_SIZE + 4
            );
        }
    }
}
/*1160 */