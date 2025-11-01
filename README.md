## 项目结构

```
src/main/java/com/ninedots0/
├── App.java                      # 程序入口
├── core/                         # 核心游戏逻辑
│   ├── model/                    # 数据模型
│   │   ├── Board.java               # 棋盘
│   │   ├── Position.java            # 位置坐标
│   │   ├── Piece.java               # 棋子基类
│   │   ├── PieceType.java           # 棋子类型枚举
│   │   ├── PlayerColor.java         # 玩家颜色枚举
│   │   └── pieces/               # 具体棋子实现
│   │       ├── General.java
│   │       ├── Advisor.java
│   │       ├── Elephant.java
│   │       ├── Horse.java
│   │       ├── Chariot.java
│   │       ├── Cannon.java
│   │       └── Soldier.java
│   ├── rules/                    # 规则引擎
│   │   ├── MoveValidator.java       # 走法验证器
│   │   ├── RuleEngine.java          # 规则引擎
│   │   └── SpecialRules.java        # 特殊规则（将军、将死等）
│   └── Game.java                 # 游戏主控制器
├── ai/                           # AI模块（可选）
│   ├── ChessAI.java
│   ├── MoveEvaluator.java
│   └── algorithms/               # AI算法
│       ├── Minimax.java
│       └── AlphaBetaPruning.java
├── ui/                           # 用户界面
│   ├── frame/                    # 窗口框架
│   │   ├── MainFrame.java           # 主窗口
│   │   └── GameFrame.java           # 游戏窗口
│   ├── panel/                    # 面板组件
│   │   ├── ChessBoardPanel.java     # 棋盘面板
│   │   ├── ControlPanel.java        # 控制面板
│   │   └── StatusPanel.java         # 状态面板
│   ├── component/                # UI组件
│   │   ├── ChessPieceRenderer.java  # 棋子渲染器
│   │   └── BoardRenderer.java       # 棋盘渲染器
│   └── listener/                 # 事件监听
│       ├── BoardMouseListener.java
│       └── GameActionListener.java
├── service/                      # 服务层
│   ├── GameService.java            # 游戏服务
│   ├── MoveService.java            # 走法服务
│   └── SaveLoadService.java        # 存档服务
├── util/                         # 工具类
│   ├── Constants.java              # 常量定义
│   ├── SoundManager.java           # 音效管理
│   └── ImageLoader.java            # 图片加载
└── config/                       # 配置
    └── GameConfig.java             # 游戏配置
```

## 资源文件结构

```
src/main/resources/
├── images/
│   ├── pieces/
│   │   ├── red_general.png
│   │   ├── black_general.png
│   │   ├── red_advisor.png
│   │   ├── black_advisor.png
│   │   ├── red_elephant.png
│   │   ├── black_elephant.png
│   │   ├── red_horse.png
│   │   ├── black_horse.png
│   │   ├── red_chariot.png
│   │   ├── black_chariot.png
│   │   ├── red_cannon.png
│   │   ├── black_cannon.png
│   │   ├── red_soldier.png
│   │   └── black_soldier.png
│   └── board/
│       ├── chessboard.png
│       └── background.jpg
├── sounds/
│   ├── move.wav
│   ├── capture.wav
│   ├── check.wav
│   └── victory.wav
└── config/
    └── game.properties
```

## 核心类职责说明

### 1. 入口类 (App.java)
```java
/**
 * 程序主入口
 * 职责：启动应用程序，初始化主窗口
 */
public class App {
    public static void main(String[] args) {
        // 启动游戏
    }
}
```

### 2. 游戏控制器 (Game.java)
```java
/**
 * 游戏主控制器
 * 职责：管理游戏状态、回合切换、走法验证
 */
public class Game {
    private Board board;
    private PlayerColor currentPlayer;
    private GameState gameState;
    private List<Move> moveHistory;
    
    public void startNewGame() { ... }
    public boolean makeMove(Position from, Position to) { ... }
    public boolean undoMove() { ... }
    public GameState checkGameState() { ... }
}
```

### 3. 棋盘模型 (Board.java)
```java
/**
 * 棋盘数据模型
 * 职责：维护棋子位置状态，提供棋盘操作接口
 */
public class Board {
    private Piece[][] grid;
    private Map<PlayerColor, Position> generalPositions;
    
    public Board() { initializeBoard(); }
    public Piece getPiece(Position pos) { ... }
    public void setPiece(Position pos, Piece piece) { ... }
    public boolean isValidPosition(Position pos) { ... }
}
```

### 4. 棋子基类 (Piece.java)
```java
/**
 * 棋子抽象基类
 * 职责：定义棋子通用行为和属性
 */
public abstract class Piece {
    protected PieceType type;
    protected PlayerColor color;
    protected Position position;
    
    public abstract boolean isValidMove(Board board, Position from, Position to);
    public abstract List<Position> getPossibleMoves(Board board, Position position);
}
```

## 模块功能描述

### core/ - 核心游戏逻辑
- **model**: 数据模型层，包含棋盘、棋子等核心数据结构
- **rules**: 规则引擎，负责走法验证和游戏规则判断
- **Game.java**: 游戏主控制器，协调各个模块

### ui/ - 用户界面
- **frame**: 窗口容器，管理应用程序窗口
- **panel**: 功能面板，实现不同区域的UI组件
- **component**: UI组件，负责具体元素的渲染
- **listener**: 事件监听，处理用户交互

### ai/ - 人工智能
- **ChessAI**: AI主控制器
- **MoveEvaluator**: 走法评估器
- **algorithms**: AI算法实现（Minimax、Alpha-Beta剪枝）

### service/ - 服务层
- **GameService**: 游戏业务流程封装
- **MoveService**: 走法相关服务
- **SaveLoadService**: 存档读档功能

### util/ - 工具类
- **Constants**: 常量定义
- **SoundManager**: 音效管理
- **ImageLoader**: 图片资源加载

## 开发阶段规划

### 第一阶段：核心模型 (1-2周)
- [ ] 完成所有数据模型类（Piece, Board, Position等）
- [ ] 实现基本的移动规则
- [ ] 编写单元测试

### 第二阶段：游戏逻辑 (1-2周)
- [ ] 完成规则引擎
- [ ] 实现游戏状态管理
- [ ] 添加走法历史和悔棋功能

### 第三阶段：用户界面 (2-3周)
- [ ] 实现棋盘UI和棋子渲染
- [ ] 完成鼠标交互
- [ ] 添加控制面板

### 第四阶段：增强功能 (可选)
- [ ] AI对战
- [ ] 音效和动画
- [ ] 存档功能
- [ ] 网络对战

## Maven依赖配置

```xml
<dependencies>
    <!-- 单元测试 -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- 日志 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.6</version>
    </dependency>
    
    <!-- JSON处理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
```
