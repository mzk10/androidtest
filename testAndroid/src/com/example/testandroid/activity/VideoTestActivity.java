package com.example.testandroid.activity;

import com.example.testandroid.activity.view.VedioPlayerView;
import com.example.testandroid.activity.view.VedioPlayerView.VideoListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

public class VideoTestActivity extends Activity
{

    private VedioPlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FrameLayout fl = new FrameLayout(this);
        setContentView(fl);
        
        
        player = new VedioPlayerView(this, new VideoListener()
        {
            @Override
            public void onVideoCompletion()
            {
                Toast.makeText(VideoTestActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
                player.release();
            }

            @Override
            public void onVideoPrepared(int width, int height, int duration)
            {
                player.getLayoutParams().width = width/2;
                player.getLayoutParams().height = height/2;
                player.start();
            }
        });
        VedioPlayerView.LayoutParams params = new VedioPlayerView.LayoutParams(VedioPlayerView.LayoutParams.MATCH_PARENT,VedioPlayerView.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        fl.addView(player, params);                               
        
        player.setData("/mnt/sdcard/movies/test.mp4");
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
