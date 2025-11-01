## 架构

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

### 资源文件

src/main/resources/
├── images/
│   ├── pieces/
│   │   ├── red_general.png
│   │   ├── black_general.png
│   │   └── ...其他棋子图片
│   └── board/
│       ├── chessboard.png
│       └── background.jpg
├── sounds/
│   ├── move.wav
│   ├── capture.wav
│   └── check.wav
└── config/
    └── game.properties
