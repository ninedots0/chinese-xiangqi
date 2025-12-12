package io.github.ninedots0.core.save;

import io.github.ninedots0.core.board.*;
import io.github.ninedots0.core.game.GameController;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveManager {

    /** 将当前游戏写入 JSON 文件 */
    public static void save(GameController gc, String path) throws Exception {
        SaveData data = toSaveData(gc);
        String json = toJson(data);
        Files.write(new File(path).toPath(), json.getBytes(StandardCharsets.UTF_8));
    }

    public static int getNum(String name) throws Exception {
        Path path = Paths.get("saves/" + name + "/num.txt");
        return Integer.parseInt(Files.readString(path));
    }
    public static void addNum(String name) throws Exception {
        Path path = Paths.get("saves/" + name + "/num.txt");
        int k = Integer.parseInt(Files.readString(path)) + 1;

        Files.write(path, String.valueOf(k).getBytes());
    }
    /** 从 JSON 文件读取存档 */
    public static SaveData load(String path) throws Exception {
        String json = Files.readString(new File(path).toPath());

        return fromJson(json);
    }

    /** 从游戏对象生成 SaveData */
    private static SaveData toSaveData(GameController gc) {
        SaveData data = new SaveData();

        Board board = gc.getBoard();
        List<SaveData.PieceData> pieces = new ArrayList<>();

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                Piece p = board.getPiece(x, y);
                if (p != null) {
                    SaveData.PieceData pd = new SaveData.PieceData();
                    pd.x = x;
                    pd.y = y;
                    pd.isRed = p.isRed();
                    pd.type = p.getType().name();
                    pieces.add(pd);
                }
            }
        }

        data.pieces = pieces.toArray(new SaveData.PieceData[0]);
        data.currentPlayer = gc.getCurrentPlayer();
        return data;
    }

    /** 将 SaveData 恢复到 GameController */
    public static void applySave(GameController gc, SaveData data) {
        Board board = new Board();
        board.clear();

        for (SaveData.PieceData pd : data.pieces) {
            Piece piece = new Piece(PieceType.valueOf(pd.type), pd.isRed);
            board.setPiece(pd.x, pd.y, piece);
        }

        gc.setBoard(board);
        gc.setCurrentPlayer(data.currentPlayer);
    }

    // ---------------- JSON 序列化 -----------------

    private static String toJson(SaveData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append("\"redTurn\":").append(data.currentPlayer).append(",");
        sb.append("\"pieces\":[");

        for (int i = 0; i < data.pieces.length; i++) {
            SaveData.PieceData p = data.pieces[i];

            sb.append("{")
              .append("\"type\":\"").append(p.type).append("\",")
              .append("\"isRed\":").append(p.isRed).append(",")
              .append("\"x\":").append(p.x).append(",")
              .append("\"y\":").append(p.y)
              .append("}");

            if (i < data.pieces.length - 1) sb.append(",");
        }

        sb.append("]}");
        return sb.toString();
    }

    private static SaveData fromJson(String json) {
        try {
            SaveData data = new SaveData();

            if (!json.contains("\"pieces\":[")) return null;
            if (!json.contains("\"redTurn\"")) return null;

            String piecesStr = json.split("\"pieces\":\\[")[1].split("]")[0];
            String[] pieceObjs = piecesStr.split("\\},\\{");

            List<SaveData.PieceData> list = new ArrayList<>();

            for (String p : pieceObjs) {
                String obj = p.replace("{", "").replace("}", "");

                SaveData.PieceData pd = new SaveData.PieceData();
                pd.type = getJsonString(obj, "type");
                pd.isRed = Boolean.parseBoolean(getJsonString(obj, "isRed"));
                pd.x = Integer.parseInt(getJsonString(obj, "x"));
                pd.y = Integer.parseInt(getJsonString(obj, "y"));
                list.add(pd);
            }

            data.pieces = list.toArray(new SaveData.PieceData[0]);
            data.currentPlayer = Integer.parseInt(json.split("\"redTurn\":")[1].split(",")[0]);

            return data;

        } catch (Exception e) {
            // JSON 损坏 → 返回 null
            return null;
        }
    }


    private static String getJsonString(String source, String key) {
        try {
            String[] parts = source.split("\"" + key + "\":");
            if (parts.length < 2) return null;
            String raw = parts[1].split(",")[0];
            return raw.replace("\"", "");
        } catch (Exception e) {
            return null;
        }
    }

}
