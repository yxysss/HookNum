package com.example.yxy.hooknum;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Y.X.Y on 2017/5/31 0031.
 */
public class MusicPlay {


    // 音效播放功能

    private static SoundPool soundPool;
    private static int soundId;

    public static void Build(Context context) {


        SoundPool.Builder builder = new SoundPool.Builder();
        //传入音频数量
        builder.setMaxStreams(1);
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        //设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        //加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build());
        if (soundPool == null) {
            soundPool = builder.build();
            soundId = soundPool.load(context, R.raw.music, 1);
        }

    }

    public static void playmusic() {
        if (soundPool != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }
}
