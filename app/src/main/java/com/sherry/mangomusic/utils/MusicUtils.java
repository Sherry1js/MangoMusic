package com.sherry.mangomusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.sherry.mangomusic.bean.MusicInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:Sherry
 * Date:2018/10/20
 * 作用：扫描本地音乐库
 */

public class MusicUtils{
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<MusicInfo> getMusicData(Context context) {

        List<MusicInfo> musicInfos = new ArrayList<>();

        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                MusicInfo musicInfo = new MusicInfo();

                musicInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                musicInfo.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                musicInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                musicInfo.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                musicInfo.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                musicInfo.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                //添加进音乐列表
                musicInfos.add(musicInfo);
            }
            // 释放资源
            cursor.close();
        }
        return musicInfos;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        int minute = time / 1000 / 60;
        int second = time / 1000 % 60;
        String minuteString;
        String secondString;
        if(minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }
        if(second < 10) {
            secondString = "0" + second;
        } else {
            secondString = "" + second;
        }
        return minuteString + ":" + secondString;
    }
}

