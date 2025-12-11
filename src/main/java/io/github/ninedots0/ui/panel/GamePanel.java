package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.board.PieceType;
import io.github.ninedots0.core.game.GameController;
import io.github.ninedots0.core.rule.OpeningDetector;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.ui.util.ImageUtils;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GamePanel extends Pane {

    private static final int TILE_SIZE = 60;
    private static final int PIECE_SIZE = 50;
    MainFrame mainFrame;
    private Canvas canvas;
    private Board board;
    private GameController gameController;

    private int selectedX = -1;
    private int selectedY = -1;
    
    // 新增：弹窗相关
    private StackPane overlayPane;
    private Label infoLabel;
    private Label winLabel;
    private PauseTransition currentPause; // 记录当前的自动隐藏计时器
    
    // 新增：开局检测器
    private OpeningDetector openingDetector;

    public GamePanel(GameController gameController1, MainFrame mainFrame1) {
        board = gameController1.getBoard(); gameController = gameController1;
        mainFrame = mainFrame1;
        this.setStyle(
            "-fx-background-image: url('/images/board.png');" +
            "-fx-background-size: 100% 100%;"  + // 拉伸填满
            "-fx-background-repeat: no-repeat;"  // 不重复
        );

        canvas = new Canvas(600, 700);
        canvas.setLayoutX(200); canvas.setLayoutY(10);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);

        // 新增：创建弹窗
        createPopupOverlay();
        
        // 新增：初始化开局检测器
        openingDetector = new OpeningDetector();
        
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

        // 添加悔棋按钮
        Button undoBtn = new Button("悔棋");
        undoBtn.setLayoutY(30);
        undoBtn.setOnAction(e -> {
            if (gameController.canUndo()) {
                gameController.undo();
                draw();
                // 新增：悔棋时重置开局检测
                openingDetector.reset();
            }
        });

        // 添加重新开始按钮
        Button restartBtn = new Button("重新开始");
        restartBtn.setLayoutY(60);
        restartBtn.setOnAction(e -> {
            // 重新开始游戏（创建新的GameController）
            // io.github.ninedots0.core.rule.MoveValidator validator = new io.github.ninedots0.core.rule.MoveValidator();
            GameController newGc = new GameController(new Board(), 1);
            mainFrame.showGamePanel(newGc);
        });

        Button backBtn = new Button("返回");
        backBtn.setOnAction(e -> {mainFrame.showMainMenu();});
        backBtn.setLayoutY(90);


        this.getChildren().addAll(canvas, btn, undoBtn, restartBtn, backBtn, overlayPane);

        loadResources();
        draw();
        
    }

    // 新增：创建弹窗覆盖层
    private void createPopupOverlay() {
        overlayPane = new StackPane();
        overlayPane.setPrefSize(900, 700);
        overlayPane.setStyle("-fx-background-color: transparent;");
        overlayPane.setVisible(false);
        overlayPane.setMouseTransparent(true);
        
        // 普通信息弹窗
        VBox infoBox = new VBox(10);
        infoBox.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;" +
            "-fx-alignment: center;"
        );
        infoBox.setMaxSize(300, 150);
        
        infoLabel = new Label();
        infoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        infoLabel.setFont(Font.font("STKaiti"));
        infoBox.getChildren().add(infoLabel);
        
        // 胜利弹窗
        VBox winBox = new VBox(10);
        winBox.setStyle(
            "-fx-background-color: rgba(255, 215, 0, 0.9);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 40;" +
            "-fx-alignment: center;" +
            "-fx-border-color: red;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;"
        );
        winBox.setMaxSize(400, 250);
        
        winLabel = new Label();
        winLabel.setStyle("-fx-text-fill: red; -fx-font-size: 36px; -fx-font-weight: bold;");
        winLabel.setFont(Font.font("STKaiti"));
        winBox.getChildren().add(winLabel);
        
        overlayPane.getChildren().addAll(infoBox, winBox);
        winBox.setVisible(false);
        
        // 居中显示
        infoBox.setTranslateX(300);
        infoBox.setTranslateY(250);
        winBox.setTranslateX(250);
        winBox.setTranslateY(200);
    }

    private void loadResources() {
        PieceType.initPieceImages();
    }
    
    private void handleClick(MouseEvent e) {
        // 游戏结束后棋盘点击无效，但按钮仍然可以点击
        if (gameController.isGameOver()) {
            return;
        }
        
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
            boolean moveSuccess = gameController.move(selectedX, selectedY, x, y);
            if (moveSuccess) {
                System.out.println("Move success!");
                
                // 新增：检测开局类型（只在前两步）
                if (!openingDetector.isOpeningDetected() && openingDetector.getMoveCount() < 2) {
                    String openingType = openingDetector.detectOpening(
                        board.getPiece(x, y), // 移动后的棋子
                        selectedX, selectedY, // 起点
                        x, y,                // 终点
                        gameController.getCurrentPlayer() == -1 // 红方回合
                    );
                    if (openingType != null) {
                        showInfoPopup("开局：" + openingType);
                    }
                }
                
                // 检查将军状态
                String checkStatus = gameController.getCheckStatus();
                if (checkStatus != null) {
                    showInfoPopup(checkStatus);
                }
                // 检查游戏是否结束
                if (gameController.isGameOver()) {
                    showWinPopup(gameController.getWinner() + "获得胜利！");
                }
            } else {
                // 移动失败 - 检查是否是送将
                Piece movingPiece = board.getPiece(selectedX, selectedY);
                if (movingPiece != null) {
                    boolean wouldSendGeneral = gameController.getCheckmateDetector().wouldSendGeneral(
                        movingPiece, selectedX, selectedY, x, y
                    );
                    if (wouldSendGeneral) {
                        showErrorPopup("不能送将");
                    } else {
                        showErrorPopup("非法移动");
                    }
                } else {
                    showErrorPopup("非法移动");
                }
            }
            selectedX = -1;  // 重置
        }

        draw();
    }

    // 新增：显示信息弹窗（1.5秒后消失）
    private void showInfoPopup(String message) {
        // 如果之前有计时器，取消它
        if (currentPause != null) {
            currentPause.stop();
        }
        
        infoLabel.setText(message);
        overlayPane.getChildren().get(0).setVisible(true);
        overlayPane.getChildren().get(1).setVisible(false);
        overlayPane.setVisible(true);
        
        // 1.5秒后自动消失
        currentPause = new PauseTransition(Duration.seconds(1.5));
        currentPause.setOnFinished(event -> {
            overlayPane.setVisible(false);
        });
        currentPause.play();
    }

    // 新增：显示胜利弹窗（不消失）
    private void showWinPopup(String message) {
    // 如果之前有计时器，取消它 - 防止胜利窗口被自动隐藏
    if (currentPause != null) {
        currentPause
.stop();
        currentPause 
= null;
    }
    
    winLabel.setText(message);
    overlayPane.getChildren().get(0).setVisible(false);
    overlayPane.getChildren().get(1).setVisible(true);
    overlayPane.setVisible(true);
    // 新增：胜利弹窗设置为非鼠标穿透，但内容区域小不影响按钮
    overlayPane.getChildren().get(1).setMouseTransparent(false);
    // 但整个overlayPane保持鼠标穿透，这样按钮可以点击
    overlayPane.setMouseTransparent(true);
}
    // 新增：显示错误弹窗
    private void showErrorPopup(String message) {
        showInfoPopup(message);
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