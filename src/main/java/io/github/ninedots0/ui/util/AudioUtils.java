package io.github.ninedots0.ui.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioUtils {

    private static MediaPlayer bgmPlayer;
    private static double volume = 0.5; // 默认音量 50%

    // 初始化并播放 BGM
    public static void playBGM(String resourcePath) {
        if (bgmPlayer == null) {
            Media media = new Media(AudioUtils.class.getResource(resourcePath).toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 无限循环播放
            bgmPlayer.setVolume(volume);
        }
        bgmPlayer.play();
    }

    // 暂停
    public static void pauseBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }

    // 调节音量
    public static void setVolume(double v) {
        volume = v;
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(v);
        }
    }

    public static double getVolume() {
        return volume;
    }
}
