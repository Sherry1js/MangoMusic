package com.sherry.mangomusic.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sherry.mangomusic.R;
import com.sherry.mangomusic.adapter.MusicAdapter;
import com.sherry.mangomusic.bean.MusicInfo;
import com.sherry.mangomusic.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private MusicAdapter musicAdapter;
    private ListView listView;

    private List<MusicInfo> musicInfos = new ArrayList<>();

    ImageView iv_bottom_album;
    TextView tv_bottom_title;
    ImageView iv_bottom_pre;
    ImageView iv_bottom_play;
    ImageView iv_bottom_next;

    // 当前歌曲的序号，下标从0开始
    private int number = 0;
    // 播放状态
    private int status;
    // 广播接收器
    private StatusChangedReceiver receiver;

    //获取专辑封面的Uri
    //private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumArt");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        //初始化各控件
        initView();

        //获取ActionBar的实例，更改默认的导航图标为菜单图标
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.actionbar_menu);

        initDrawerLayoutListener();

        initListener();

        musicInfos = MusicUtils.getMusicData(this);
        musicAdapter = new MusicAdapter(this,musicInfos);
        listView.setAdapter(musicAdapter);
        //setTransluteWindow();
        //getStatusBarHeight(this);

        bindStatusChangedReceiver();

        startService(new Intent(this, MusicService.class));
        status = MusicService.COMMAND_STOP;

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sendBroadcastOnCommand(MusicService.COMMAND_CHECK_IS_PLAYING);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (status == MusicService.STATUS_STOPPED) {
            stopService(new Intent(this, MusicService.class));
        }
        super.onDestroy();
    }

    //初始化控件
    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_header);
        listView = (ListView) findViewById(R.id.list_view);
        iv_bottom_album = (ImageView) findViewById(R.id.iv_bottom_album);
        tv_bottom_title = (TextView) findViewById(R.id.tv_bottom_title);
        iv_bottom_pre = (ImageView) findViewById(R.id.iv_bottom_pre);
        iv_bottom_play = (ImageView) findViewById(R.id.iv_bottom_play);
        iv_bottom_next = (ImageView) findViewById(R.id.iv_bottom_next);
    }

    private void initListener(){
        //歌曲条目的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                number = position;
                sendBroadcastOnCommand(MusicService.COMMAND_PLAY);
            }
        });

        iv_bottom_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch (status) {
                    case MusicService.STATUS_PLAYING:
                        sendBroadcastOnCommand(MusicService.COMMAND_PAUSE);
                        break;
                    case MusicService.STATUS_PAUSED:
                        sendBroadcastOnCommand(MusicService.COMMAND_RESUME);
                        break;
                    case MusicService.COMMAND_STOP:
                        sendBroadcastOnCommand(MusicService.COMMAND_PLAY);
                    default:
                        break;
                }
            }
        });

        iv_bottom_pre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sendBroadcastOnCommand(MusicService.COMMAND_PREVIOUS);
            }
        });

        iv_bottom_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sendBroadcastOnCommand(MusicService.COMMAND_NEXT);
            }
        });
    }
    /** 绑定广播接收器 */
    private void bindStatusChangedReceiver() {
        receiver = new StatusChangedReceiver();
        IntentFilter filter = new IntentFilter(MusicService.BROADCAST_MUSICSERVICE_UPDATE_STATUS);
        registerReceiver(receiver, filter);
    }

    /** 发送命令，控制音乐播放。参数定义在MusicService类中 */
    private void sendBroadcastOnCommand(int command) {

        Intent intent = new Intent(MusicService.BROADCAST_MUSICSERVICE_CONTROL);
        intent.putExtra("command", command);
        // 根据不同命令，封装不同的数据
        switch (command) {
            case MusicService.COMMAND_PLAY:
                intent.putExtra("number", number);
                break;
            case MusicService.COMMAND_PREVIOUS:
            case MusicService.COMMAND_NEXT:
            case MusicService.COMMAND_PAUSE:
            case MusicService.COMMAND_STOP:
            case MusicService.COMMAND_RESUME:
            default:
                break;
        }
        sendBroadcast(intent);
    }

    /** 内部类，用于播放器状态更新的接收广播 */
    class StatusChangedReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            // 获取播放器状态
            status = intent.getIntExtra("status", -1);
            switch (status) {
                case MusicService.STATUS_PLAYING:
                    tv_bottom_title.setText(MusicUtils.getMusicData(context).get(number).artist+" - "+MusicUtils.getMusicData(context).get(number).title);
                    iv_bottom_play.setBackgroundResource(R.drawable.play_btn_pause);
                    break;
                case MusicService.STATUS_PAUSED:
                    iv_bottom_play.setBackgroundResource(R.drawable.play_btn_play);
                    break;
                case MusicService.STATUS_STOPPED:
                    iv_bottom_play.setBackgroundResource(R.drawable.play_btn_pause);
                    break;
                case MusicService.STATUS_COMPLETED:
                    sendBroadcastOnCommand(MusicService.COMMAND_NEXT);
                    break;
                default:
                    break;
            }
        }
    }

    /**
    public void setTransluteWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
     //隐藏状态栏的两个方法
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen","android");
        if(resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }*/

    //侧滑菜单的点击事件
    private void initDrawerLayoutListener(){
        //默认选中个人中心
        //navView.setCheckedItem(R.id.menu_user);
        //设置侧滑菜单的点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    //打开侧滑菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
