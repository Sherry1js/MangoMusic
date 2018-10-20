package com.sherry.mangomusic.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author:Sherry
 * Date:2018/10/19
 * 作用：音乐数据的bean类
 */

public class MusicInfo{

    public String artist; //歌手

    public String title; //音乐名字

    public String path; //歌曲的地址

    public int duration; //歌曲长度

    public long size; //歌曲的大小

    public String album; //专辑
}