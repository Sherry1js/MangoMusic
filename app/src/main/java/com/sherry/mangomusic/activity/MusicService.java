package com.sherry.mangomusic.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.sherry.mangomusic.utils.MusicUtils;


public class MusicService extends Service{

    // 播放控制命令，标识操作
    public static final int COMMAND_UNKNOWN = -1;
    public static final int COMMAND_PLAY = 0;
    public static final int COMMAND_PAUSE = 1;
    public static final int COMMAND_STOP = 2;
    public static final int COMMAND_RESUME = 3;
    public static final int COMMAND_PREVIOUS = 4;
    public static final int COMMAND_NEXT = 5;
    public static final int COMMAND_CHECK_IS_PLAYING = 6;
    public static final int COMMAND_SEEK_TO = 7;
    public static final int COMMAND_RANDOM = 8;
    // 播放器状态̬
    public static final int STATUS_PLAYING = 0;
    public static final int STATUS_PAUSED = 1;
    public static final int STATUS_STOPPED = 2;
    public static final int STATUS_COMPLETED = 3;
    // 广播标志
    public static final String BROADCAST_MUSICSERVICE_CONTROL = "MusicService.ACTION_CONTROL";
    public static final String BROADCAST_MUSICSERVICE_UPDATE_STATUS = "MusicService.ACTION_UPDATE";

    //歌曲序号，从0开始
    private int number = 0;
    private int status;
    // 媒体播放类
    private MediaPlayer player = new MediaPlayer();

    // 广播接收器
    private CommandReceiver receiver;

    @Override
    public void onCreate(){
        super.onCreate();

        //绑定广播接收器
        bindCommandReceiver();
        //开启服务时状态默认是停止的
        status = MusicService.STATUS_STOPPED;
    }

    @Override
    public void onDestroy(){
        if(player != null) {
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        // TODO: Return the communication channel to the service.
        return null;
    }

    /** 绑定广播接收器 */
    private void bindCommandReceiver() {
        receiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter(BROADCAST_MUSICSERVICE_CONTROL);
        registerReceiver(receiver, filter);
    }

    /** 内部类，接收广播命令，并执行操作 */
    class CommandReceiver extends BroadcastReceiver{

        public void onReceive(Context context, Intent intent) {
            // 获取命令
            int command = intent.getIntExtra("command", COMMAND_UNKNOWN);
            // 执行命令
            switch (command) {
                case COMMAND_PLAY:
                    number = intent.getIntExtra("number", 0);
                    play(number);
                    break;
                case COMMAND_PREVIOUS:
                    previous();
                    break;
                case COMMAND_NEXT:
                    next();
                    break;
                case COMMAND_PAUSE:
                    pause();
                    break;
                case COMMAND_STOP:
                    stop();
                    break;
                case COMMAND_RESUME:
                    resume();
                    break;
                case COMMAND_CHECK_IS_PLAYING:
                    if (player != null && player.isPlaying()) {
                        sendBroadcastOnStatusChanged(MusicService.STATUS_PLAYING);
                    }
                    break;
                case COMMAND_UNKNOWN:
                default:
                    break;
            }
        }
    }

    /** 发送广播，提醒状态改变了 */
    private void sendBroadcastOnStatusChanged(int status){
        Intent intent = new Intent(BROADCAST_MUSICSERVICE_UPDATE_STATUS);
        intent.putExtra("status", status);
        sendBroadcast(intent);
    }

    /** 播放结束监听器 */
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer player) {
            if (player.isLooping()) {
                replay();
            } else {
                sendBroadcastOnStatusChanged(MusicService.STATUS_COMPLETED);
            }
        }
    };

    /** 读取音乐文件 */
    private void load(int number) {
        try {
            player.reset();
            player.setDataSource(MusicUtils.getMusicData(this).get(number).path);
            player.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 注册监听器
        player.setOnCompletionListener(completionListener);
    }

    /** 播放下一首 */
    private void previous(){
        if(number == 0){
            Toast.makeText(MusicService.this,"已经到达列表顶部",Toast.LENGTH_SHORT).show();
        } else {
            --number;
            play(number);
        }
    }

    /** 播放上一首 */
    private void next(){
        if(number == MusicUtils.getMusicData(this).size() - 1){
            Toast.makeText(MusicService.this,"已经到达列表底部",Toast.LENGTH_SHORT).show();
        } else {
            ++number;
            play(number);
        }
    }

    private void play(int number){
        if(player != null && player.isPlaying()){
            player.stop();
        }
        load(number);
        player.start();
        status = MusicService.STATUS_PLAYING;
        sendBroadcastOnStatusChanged(MusicService.STATUS_PLAYING);
    }

    private void pause(){
        if (player.isPlaying()) {
            player.pause();
            status = MusicService.STATUS_PAUSED;
            sendBroadcastOnStatusChanged(MusicService.STATUS_PAUSED);
        }
    }

    private void stop(){
        if (status != MusicService.STATUS_STOPPED) {
            player.stop();
            status = MusicService.STATUS_STOPPED;
            sendBroadcastOnStatusChanged(MusicService.STATUS_STOPPED);
        }
    }

    private void resume(){
        player.start();
        status = MusicService.STATUS_PLAYING;
        //sendBroadcastOnStatusChanged(MusicService.STATUS_PLAYING);
    }

    private void replay(){
        player.start();
        status = MusicService.STATUS_PLAYING;
        //sendBroadcastOnStatusChanged(MusicService.STATUS_PLAYING);
    }
}
