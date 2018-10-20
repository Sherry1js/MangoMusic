package com.sherry.mangomusic.activity;


import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

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

        musicInfos = MusicUtils.getMusicData(this);
        musicAdapter = new MusicAdapter(this,musicInfos);
        listView.setAdapter(musicAdapter);
        //setTransluteWindow();
        //getStatusBarHeight(this);

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

    //初始化控件
    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_header);
        listView = (ListView) findViewById(R.id.list_view);
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
