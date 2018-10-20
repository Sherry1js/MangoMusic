package com.sherry.mangomusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sherry.mangomusic.R;
import com.sherry.mangomusic.bean.MusicInfo;
import com.sherry.mangomusic.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Author:Sherry
 * Date:2018/10/19
 * 作用：音乐列表的适配器
 */

public class MusicAdapter extends BaseAdapter{

    private Context mContext;
    private List<MusicInfo> musicInfos;

    public MusicAdapter(Context context, List<MusicInfo> musicInfos) {
        this.mContext = context;
        this.musicInfos = musicInfos;
    }

    @Override
    public int getCount() {
        return musicInfos == null ? 0 : musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return musicInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null) {
            holder = new ViewHolder();

            //引入布局
            view = View.inflate(mContext, R.layout.item_music_list, null);
            //实例化对象
            holder.name = (TextView) view.findViewById(R.id.music_name);
            holder.artist = (TextView) view.findViewById(R.id.music_artist);
            holder.duration = (TextView) view.findViewById(R.id.music_duration);
            holder.position = (TextView) view.findViewById(R.id.music_position);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //给控件赋值
        holder.position.setText(i+1+"");
        holder.name.setText(musicInfos.get(i).title);
        holder.artist.setText(musicInfos.get(i).artist.toString()+" - "+musicInfos.get(i).album);

        //时间需要转换一下
        int duration = musicInfos.get(i).duration;
        String time = MusicUtils.formatTime(duration);
        holder.duration.setText(time);

        return view;
    }

    class ViewHolder{

        TextView name;//歌曲名
        TextView artist;//歌手
        TextView duration;//时长
        TextView position;//序号

    }

}
