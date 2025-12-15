package io.github.ninedots0.ui.panel;

import io.github.ninedots0.core.board.Board;
import io.github.ninedots0.core.board.Piece;
import io.github.ninedots0.core.board.PieceType;
import io.github.ninedots0.core.game.GameController;
import io.github.ninedots0.core.rule.OpeningDetector;
import io.github.ninedots0.core.save.SaveManager;
import io.github.ninedots0.ui.frame.MainFrame;
import io.github.ninedots0.ui.util.StyleUtils;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    // 困毙弹窗的标签（运行时可设置，避免字体/编码问题）
    private Label stalemateLabel;

    // 新增：计时器相关
    private Label timerLabel;
    private Timeline clockTimeline;
    private int redMainSeconds = 15 * 60;   // 15 minutes
    private int blackMainSeconds = 15 * 60; // 15 minutes
    private int perMoveSeconds = 60;        // 60 seconds per move
    // 大数字倒计时相关（步时最后5秒）
    private Label bigCountLabel;
    private javafx.scene.layout.VBox bigCountBox;
    private PauseTransition bigCountPause;
    private int lastBigShown = -1;

    private PauseTransition currentPause; // 记录当前的自动隐藏计时器
    // 新增：开局检测器
    private OpeningDetector openingDetector;

    private int lastFromX = -1, lastFromY = -1, lastToX = -1, lastToY = -1;

    public GamePanel(GameController gameController1, MainFrame mainFrame1) {
        javafx.scene.text.Font.getFamilies().forEach(System.out::println);
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
        // 新增：创建计时器显示并启动
        createTimerDisplay();
        startClock();
        
        // 新增：初始化开局检测器
        openingDetector = new OpeningDetector();
        
        Button saveBtn = new Button("存档");
        saveBtn.setLayoutX(0);saveBtn.setLayoutY(0);
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
        StyleUtils.applyGamePanelLabel(statusLabel);
        statusLabel.setLayoutX(120); statusLabel.setLayoutY(350);
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
                // 恢复步时（避免因悔棋导致步时异常）
                perMoveSeconds = 60;
                lastBigShown = -1;
                if (gameController.getCurrentPlayer() == 1) statusLabel.setText("红方行棋");
                else statusLabel.setText("黑方行棋");
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

    // 新增：显示大数字倒计时（步时最后5秒）
    private void showBigCountdown(int n) {
        if (bigCountLabel == null || bigCountBox == null) return;

        // 如果已有暂停动画，先停掉
        if (bigCountPause != null) {
            bigCountPause.stop();
            bigCountPause = null;
        }

        bigCountLabel.setText(String.valueOf(n));
        bigCountBox.setVisible(true);

        lastBigShown = n;

        bigCountPause = new PauseTransition(Duration.seconds(1));
        bigCountPause.setOnFinished(ev -> {
            bigCountBox.setVisible(false);
            bigCountPause = null;
        });
        bigCountPause.play();
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
        overlayPane.setStyle("""
        -fx-background-color: rgba(0, 0, 0, 0.18);
    """);
        overlayPane.setVisible(false);
        overlayPane.setMouseTransparent(true);
        
        // 普通信息弹窗
        VBox infoBox = new VBox(10);
        infoBox.setStyle("""
            -fx-background-color: rgba(245, 247, 244, 0.97);
            -fx-background-radius: 12;
            -fx-padding: 14 26;
            -fx-alignment: center;

            -fx-border-color: rgba(120, 140, 130, 0.6);
            -fx-border-width: 1;
            -fx-border-radius: 12;

            -fx-effect:
                dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.35, 0, 3);
        """);
        infoBox.setMaxSize(300, 150);
        
        infoLabel = new Label();
        infoLabel.setStyle("""
            -fx-font-family: "Source Han Serif SC", "Noto Serif CJK SC", "Serif";
            -fx-text-fill: #2F3E36;
            -fx-font-size: 22px;
            -fx-font-weight: normal;
        """);
        infoLabel.setFont(Font.font("STKaiti"));
        // infoLabel.setLayoutX(100);
        infoBox.getChildren().add(infoLabel);
        
        // 胜利弹窗
        VBox winBox = new VBox(10);
        winBox.setStyle("""
            -fx-background-color: rgba(28, 42, 36, 0.97);
            -fx-background-radius: 20;
            -fx-padding: 36 48;
            -fx-alignment: center;

            -fx-border-color: rgba(180, 200, 190, 0.6);
            -fx-border-width: 2;
            -fx-border-radius: 20;

            -fx-effect:
                dropshadow(gaussian, rgba(0,0,0,0.55), 26, 0.45, 0, 6);
        """);
        winBox.setMaxSize(400, 250);
        
        winLabel = new Label();
        winLabel.setStyle("""
            -fx-text-fill: #E6EFEA;
            -fx-font-family: "Source Han Serif SC", "Noto Serif CJK SC", "Serif";
            -fx-font-size: 36px;
            -fx-font-weight: bold;
            -fx-letter-spacing: 1.5px;
        """);
        winLabel.setFont(Font.font("STKaiti"));
        winBox.getChildren().add(winLabel);
        //困毙弹窗
        VBox stalemateBox = new VBox(10);
        stalemateBox.setStyle("""
            -fx-background-color: rgba(245, 247, 244, 0.97);
            -fx-background-radius: 12;
            -fx-padding: 14 26;
            -fx-alignment: center;  
            -fx-border-color: rgba(120, 140, 130, 0.6);
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-effect:
                dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.35, 0, 3);
        """);
        stalemateBox.setMaxSize(300, 150);
        stalemateLabel = new Label("困毙杀");
        stalemateLabel.setStyle("""
                    -fx-font-family: "Source Han Serif SC", "Noto Serif CJK SC", "Serif";
            -fx-text-fill: #2F3E36;
            -fx-font-size: 22px;
            -fx-font-weight: normal;
        """);
        stalemateLabel.setFont(Font.font("STKaiti"));
        stalemateBox.getChildren().add(stalemateLabel);
        stalemateBox.setVisible(false);
        // 居中显示困毙弹窗，和其他弹窗一致
        stalemateBox.setTranslateX(170);
        stalemateBox.setTranslateY(20);

        // 求和确认弹窗
        VBox drawBox = new VBox(15);
        drawBox.setStyle("""
            -fx-background-color: rgba(235, 240, 238, 0.98);
            -fx-background-radius: 16;
            -fx-padding: 26 36;
            -fx-alignment: center;

            -fx-border-color: rgba(110, 130, 120, 0.7);
            -fx-border-width: 1.5;
            -fx-border-radius: 16;

            -fx-effect:
                dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0.4, 0, 4);
        """);
        drawBox.setMaxSize(350, 200);
        Label drawLabel = new Label("双方求和？");
        drawBox.setStyle("""
                    
            -fx-background-color: rgba(235, 240, 238, 0.98);
            -fx-background-radius: 16;
            -fx-padding: 26 36;
            -fx-alignment: center;

            -fx-border-color: rgba(110, 130, 120, 0.7);
            -fx-border-width: 1.5;
            -fx-border-radius: 16;

            -fx-effect:
                dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0.4, 0, 4);
        """);
                // drawLabel.setFont(Font.font("STKaiti"));
                drawBox.getChildren().add(drawLabel);
                drawLabel.setStyle("""
            -fx-font-family: "Source Han Serif SC", "Noto Serif CJK SC", "Serif";
            -fx-text-fill: #344A42;
            -fx-font-size: 26px;
            -fx-font-weight: bold;
        """);


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
        
        // 大数字倒计时弹窗（居中，显眼）
        bigCountBox = new VBox();
        bigCountBox.setStyle("-fx-alignment:center;");
        bigCountLabel = new Label();
        bigCountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 120px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 20, 0.4, 0, 4);");
        bigCountBox.getChildren().add(bigCountLabel);
        bigCountBox.setVisible(false);

        // 按照已有方法中使用的索引顺序添加：0=infoBox,1=winBox,2=drawBox,3=stalemateBox
        overlayPane.getChildren().addAll(infoBox, winBox, drawBox, stalemateBox);
        winBox.setVisible(false);
        drawBox.setVisible(false);
        
        // 居中显示
        infoBox.setTranslateX(170);
        infoBox.setTranslateY(20);
        winBox.setTranslateX(170);
        winBox.setTranslateY(20);
        drawBox.setTranslateX(170);
        drawBox.setTranslateY(20);
        // bigCountBox 居中显示（置于 GamePanel 顶层，避免 overlay 背景遮罩）
        // 将 bigCountBox 从 overlayPane 移到 GamePanel 顶层，以免触发 overlayPane 的半透明背景
        if (bigCountBox != null) {
            bigCountBox.setVisible(false);
            // 大数字定位于棋盘中心（棋盘 canvas 在 350,69，尺寸 600x700）
            bigCountBox.setLayoutX(350 + 600/2 - 80);
            bigCountBox.setLayoutY(69 + 700/2 - 110);
            this.getChildren().add(bigCountBox);
        }
    }

    private void loadResources() {
        PieceType.initPieceImages();
    }

    // 新增：创建并显示计时器标签
    private void createTimerDisplay() {
        timerLabel = new Label();
        timerLabel.setStyle("-fx-font-size:14px; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.45); -fx-padding:6 10; -fx-background-radius:6;");
        timerLabel.setLayoutX(920);
        timerLabel.setLayoutY(10);
        timerLabel.setMinWidth(160);
        timerLabel.setMinHeight(28);
        this.getChildren().add(timerLabel);
        updateTimerLabel();
    }

    // 新增：启动时钟（每秒触发）
    private void startClock() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (gameController.isGameOver()) {
                stopClock();
                return;
            }

            // 每秒减少当前玩家的局时与步时
            if (gameController.getCurrentPlayer() == 1) {
                redMainSeconds = Math.max(0, redMainSeconds - 1);
            } else {
                blackMainSeconds = Math.max(0, blackMainSeconds - 1);
            }
            perMoveSeconds = Math.max(0, perMoveSeconds - 1);

            // 如果步时进入最后5秒，显示大数字（每秒一次）
            if (perMoveSeconds > 0 && perMoveSeconds <= 5 && perMoveSeconds != lastBigShown) {
                showBigCountdown(perMoveSeconds);
            }

            // 超时判负（步时或局时任一归零）
            if (perMoveSeconds <= 0 || redMainSeconds <= 0 || blackMainSeconds <= 0) {
                if (!gameController.isGameOver()) {
                    // 让当前方认输，从而对手获胜
                    gameController.surrender();
                    showWinPopup(gameController.getWinner() + "获胜！");
                }
                stopClock();
            }

            updateTimerLabel();
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void stopClock() {
        if (clockTimeline != null) {
            clockTimeline.stop();
            clockTimeline = null;
        }
    }

    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void updateTimerLabel() {
        String red = formatTime(redMainSeconds);
        String black = formatTime(blackMainSeconds);
        String step = perMoveSeconds + "s";
        timerLabel.setText("红 " + red + "  黑 " + black + "  步 " + step);
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
                // 重置步时（对下一手生效）
                perMoveSeconds = 60;
                lastBigShown = -1;
                if (isCapturing) {
                    showInfoPopup(targetPiece.getColor() == 1 ? "黑方吃子" : "红方吃子");
                }
                // System.out.println("Move success!");
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
                // 优先检测困毙（即使 controller 已将局判为结束，也优先展示“困毙杀”提示）
                boolean isRedToMove = gameController.getCurrentPlayer() == 1;
                String stalemateStatus = gameController.getCheckmateDetector().detectCheckmateOrStalemate(isRedToMove);
                if ("困毙杀".equals(stalemateStatus)) {
                    showStalematePopup();
                } else {
                    // 不是困毙时按原逻辑展示胜利弹窗
                    if (gameController.isGameOver()) {
                        showWinPopup(gameController.getWinner() + "获得胜利！");
                    }
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
    // 停止时钟并取消自动隐藏计时器 - 防止胜利窗口被自动隐藏
    if (currentPause != null) {
        currentPause.stop();
        currentPause = null;
    }
    stopClock();
    // 停止大数字计时器并隐藏
    if (bigCountPause != null) {
        bigCountPause.stop();
        bigCountPause = null;
    }
    if (bigCountBox != null) {
        bigCountBox.setVisible(false);
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
    //显示困毙弹窗
    private void showStalematePopup() {
        // 如果之前有计时器，取消它
        if (currentPause != null) {
            currentPause.stop();
        }   
        // 确保文本正确（运行时设置以防止字体/编码问题）
        if (stalemateLabel != null) {
            stalemateLabel.setText("困毙杀");
        }
        // 调试日志：记录弹窗触发与当前文本
        System.out.println("[DEBUG] showStalematePopup called, text=" + (stalemateLabel != null ? stalemateLabel.getText() : "<null>"));
        overlayPane.getChildren().get(0).setVisible(false);
        overlayPane.getChildren().get(1).setVisible(false);
        overlayPane.getChildren().get(2).setVisible(false);
        overlayPane.getChildren().get(3).setVisible(true);
        overlayPane.setVisible(true);
        overlayPane.setMouseTransparent(false);
        // 1.5秒后自动消失
        currentPause = new PauseTransition(Duration.seconds(10000));
        currentPause.setOnFinished(event -> {
            overlayPane.setVisible(false);
        });
        currentPause.play();
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