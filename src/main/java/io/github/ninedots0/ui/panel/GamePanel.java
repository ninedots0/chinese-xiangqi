package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.board.PieceType;
import io.github.ninedots0.core.game.GameController;
import io.github.ninedots0.core.rule.OpeningDetector;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.ui.util.*;
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
    private Label infoLabel, winLabel, statusLabel;

    private PauseTransition currentPause; // 记录当前的自动隐藏计时器
    // 新增：开局检测器
    private OpeningDetector openingDetector;

    private int lastFromX = -1, lastFromY = -1, lastToX = -1, lastToY = -1;

    public GamePanel(GameController gameController1, MainFrame mainFrame1) {
        board = gameController1.getBoard(); gameController = gameController1;
        mainFrame = mainFrame1;
        this.setStyle(
            "-fx-background-image: url('/images/board.jpg');" +
            "-fx-background-size: 100% 100%;"  + // 拉伸填满
            "-fx-background-repeat: no-repeat;"  // 不重复
        );

        canvas = new Canvas(600, 700);
        canvas.setLayoutX(350); canvas.setLayoutY(69);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);

        // 新增：创建弹窗
        createPopupOverlay();
        
        // 新增：初始化开局检测器
        openingDetector = new OpeningDetector();
        
        Button saveBtn = new Button("存档");
        StyleUtils.applyGamePanelButton(saveBtn);
        saveBtn.setOnAction(e -> {
            if (mainFrame.getAuthService().getCurrentUser() != null) {
                try {
                    
                    String username = mainFrame.getAuthService().getCurrentUser().getUsername();
                    SaveManager.addNum(username);
                    String path = "saves/" + username + "/save" + SaveManager.getNum(username) + ".json";
                    SaveManager.save(gameController, path);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        statusLabel = new Label("红方先行");
        statusLabel.setStyle("""
            -fx-font-size: 16px;
            -fx-padding: 8px;
            -fx-background-color: #f5f5f5;
            -fx-border-color: #cccccc;
        """);
        statusLabel.setLayoutX(100);
        statusLabel.setLayoutY(20);
        // 添加悔棋按钮
        Button undoBtn = new Button("悔棋");
        StyleUtils.applyGamePanelButton(undoBtn);
        undoBtn.setLayoutX(980);undoBtn.setLayoutY(700);
        undoBtn.setOnAction(e -> {
            if (gameController.canUndo()) {
                gameController.undo();
                lastFromX = lastFromY = lastToX = lastToY = -1; // ⭐
                draw();
                openingDetector.reset();
            }
        });

        Button drawBtn = new Button("求和");
        StyleUtils.applyGamePanelButton(drawBtn);
        drawBtn.setLayoutX(980); drawBtn.setLayoutY(660);
        drawBtn.setOnAction(e -> {
            // 显示求和确认对话框
            showDrawConfirmDialog();
        });

        Button giveUpBtn = new Button("投降");
        StyleUtils.applyGamePanelButton(giveUpBtn);
        giveUpBtn.setLayoutX(980); giveUpBtn.setLayoutY(620);
        giveUpBtn.setOnAction(e -> {
            gameController.surrender();
            showWinPopup(gameController.getWinner() + "获得胜利！");
        });

        // 添加重新开始按钮
        Button restartBtn = new Button("重新开始");
        StyleUtils.applyGamePanelButton(restartBtn);
        restartBtn.setLayoutX(980); restartBtn.setLayoutY(580);
        restartBtn.setOnAction(e -> {
            // 重新开始游戏（创建新的GameController）
            // io.github.ninedots0.core.rule.MoveValidator validator = new io.github.ninedots0.core.rule.MoveValidator();
            GameController newGc = new GameController(new Board(), 1);
            mainFrame.showGamePanel(newGc);
        });

        Button backBtn = new Button("返回主菜单");
        StyleUtils.applyGamePanelButton(backBtn);
        backBtn.setLayoutX(0); backBtn.setLayoutY(700);
        backBtn.setOnAction(e -> {mainFrame.showMainMenu();});

        this.getChildren().add(canvas);
        if (mainFrame.getAuthService().getCurrentUser() != null)
            this.getChildren().add(saveBtn);
        this.getChildren().addAll(undoBtn, restartBtn, backBtn, giveUpBtn, drawBtn, overlayPane, statusLabel);

        loadResources();
        draw();
        
    }
    private void showDrawConfirmDialog() {
        // 隐藏其他弹窗
        overlayPane.getChildren().get(0).setVisible(false);
        overlayPane.getChildren().get(1).setVisible(false);
        overlayPane.getChildren().get(2).setVisible(true);
        overlayPane.setVisible(true);
        overlayPane.setMouseTransparent(false);
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

        
        // 求和确认弹窗
        VBox drawBox = new VBox(15);
        drawBox.setStyle(
            "-fx-background-color: rgba(255, 192, 203, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 30;" +
            "-fx-alignment: center;" +
            "-fx-border-color: blue;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;"
        );
        drawBox.setMaxSize(350, 200);
        Label drawLabel = new Label("双方求和？");
        drawLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 28px; -fx-font-weight: bold;");
        drawLabel.setFont(Font.font("STKaiti"));
        drawBox.getChildren().add(drawLabel);
        
        // 按钮容器
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setStyle("-fx-alignment: center;");
        Button agreeBtn = new Button("同意");
        agreeBtn.setPrefWidth(80);
        agreeBtn.setStyle("-fx-font-size: 14px;");
        agreeBtn.setOnAction(e -> {
            gameController.draw();
            overlayPane.getChildren().get(0).setVisible(false);
            overlayPane.getChildren().get(1).setVisible(false);
            overlayPane.getChildren().get(2).setVisible(false);
            showWinPopup("双方和棋");
        });
        
        Button disagreeBtn = new Button("不同意");
        disagreeBtn.setPrefWidth(80);
        disagreeBtn.setStyle("-fx-font-size: 14px;");
        disagreeBtn.setOnAction(e -> {
            overlayPane.getChildren().get(0).setVisible(false);
            overlayPane.getChildren().get(1).setVisible(false);
            overlayPane.getChildren().get(2).setVisible(false);
            overlayPane.setVisible(false);
            overlayPane.setMouseTransparent(true);
        });
        
        buttonBox.getChildren().addAll(agreeBtn, disagreeBtn);
        drawBox.getChildren().add(buttonBox);
        
        overlayPane.getChildren().addAll(infoBox, winBox, drawBox);
        winBox.setVisible(false);
        drawBox.setVisible(false);
        
        // 居中显示
        infoBox.setTranslateX(300);
        infoBox.setTranslateY(250);
        winBox.setTranslateX(250);
        winBox.setTranslateY(200);
        drawBox.setTranslateX(275);
        drawBox.setTranslateY(250);
    }

    private void loadResources() {
        PieceType.initPieceImages();
    }
    private void handleClick(MouseEvent e) {
        // 游戏结束后棋盘点击无效，但按钮仍然可以点击
        if (gameController.isGameOver()) {
            return;
        }
        
        // System.out.printf("%f %f\n", e.getX(), e.getY());
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
             Piece targetPiece = board.getPiece(x, y);
            boolean isCapturing = targetPiece != null && targetPiece.getColor() != gameController.getCurrentPlayer();

            boolean moveSuccess = gameController.move(selectedX, selectedY, x, y);
            if (gameController.getCurrentPlayer() == 1) statusLabel.setText("红方行棋");
            else statusLabel.setText("黑方行棋");
            if (moveSuccess) {
                lastFromX= selectedX;
                lastFromY = selectedY;
                lastToX = x;
                lastToY = y;
                if (isCapturing) {
                    showInfoPopup(targetPiece.getColor() == 1 ? "黑方吃子" : "红方吃子");
                }
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

        drawLastMoveHighlight(gc);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board.getPiece(x, y);
                if (p != null) {

                    double px = x * TILE_SIZE;
                    double py = y * TILE_SIZE;

                    double cx = px + PIECE_SIZE / 2.0;  // 圆心
                    double cy = py + PIECE_SIZE / 2.0;
                    double r = PIECE_SIZE / 2.0;        // 半径

                    gc.save();           // 保存状态，不影响其他绘制
                    gc.beginPath();
                    gc.arc(cx, cy, r, r, 0, 360);  // 画圆
                    gc.closePath();
                    gc.clip();          // 裁切成圆形

                    gc.drawImage(
                        p.getImage(),
                        px,
                        py,
                        PIECE_SIZE,
                        PIECE_SIZE
                    );

                    gc.restore();        // 恢复裁切
                }
            }
        }

        // 选中框依旧使用圆形框
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

    private void drawLastMoveHighlight(GraphicsContext gc) {
    if (lastFromX == -1) return;

    gc.setStroke(Color.rgb(255, 180, 0, 0.8));
    gc.setLineWidth(4);

    drawHighlightCircle(gc, lastFromX, lastFromY);
    drawHighlightCircle(gc, lastToX, lastToY);

    gc.setLineWidth(1);
}
private void drawHighlightCircle(GraphicsContext gc, int x, int y) {
    double px = x * TILE_SIZE + PIECE_SIZE / 2.0;
    double py = y * TILE_SIZE + PIECE_SIZE / 2.0;
    double r = PIECE_SIZE / 2.0 + 4;

    gc.strokeOval(
        px - r,
        py - r,
        r * 2,
        r * 2
    );
}
}