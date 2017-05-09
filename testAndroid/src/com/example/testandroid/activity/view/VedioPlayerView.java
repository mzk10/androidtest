package com.example.testandroid.activity.view;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class VedioPlayerView extends FrameLayout
{

    private static final String TAG = "";
    private Context context;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private boolean isVedioPrepared;
    private VideoListener listener;

    public VedioPlayerView(Context context, VideoListener listener)
    {
        super(context);
        this.context = context;
        this.listener = listener;
        isVedioPrepared = false;
        init();
    }

    @SuppressWarnings("deprecation")
    private void init()
    {
        final SurfaceView surfaceView = new SurfaceView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.addView(surfaceView, params);
        {
            // 配置holder对象
            holder = surfaceView.getHolder();
            holder.addCallback(new Callback()
            {
                @Override
                public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
                {
                    // 当Surface尺寸等参数改变时触发
                    Log.v(TAG, "surfaceChanged called");
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder)
                {
                    // 当SurfaceView中的Surface被创建的时候被调用
                    // 在这里我们指定MediaPlayer在当前的Surface中进行播放
                    player.setDisplay(holder);
                    // 在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
                    // player.prepareAsync();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {
                    Log.v(TAG, "surfaceDestroyed called");
                }
            });
            // 为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        {
            // 下面开始实例化MediaPlayer对象
            player = new MediaPlayer();
            player.setOnCompletionListener(new OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer player)
                {
                    // 当MediaPlayer播放完成后触发
                    if (listener != null)
                    {
                        listener.onVideoCompletion();
                    }
                }
            });
            player.setOnErrorListener(new OnErrorListener()
            {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra)
                {
                    Log.v(TAG, "onError called");
                    switch (what)
                    {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Log.v(TAG, "MEDIA_ERROR_SERVER_DIED");
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.v(TAG, "MEDIA_ERROR_UNKNOWN");
                        break;
                    default:
                        break;
                    }
                    return false;
                }
            });
            player.setOnInfoListener(new OnInfoListener()
            {

                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra)
                {
                    // 当一些特定信息出现或者警告时触发
                    switch (what)
                    {
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        break;
                    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        break;
                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        break;
                    }
                    return false;
                }
            });
            player.setOnPreparedListener(new OnPreparedListener()
            {

                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    // 当prepare完成后，该方法触发
                    isVedioPrepared = true;
                    if (listener != null)
                    {
                        int width = mp.getVideoWidth();
                        int height = mp.getVideoHeight();
                        int duration = mp.getDuration();
                        listener.onVideoPrepared(width, height, duration);
                    }
                }
            });
            player.setOnSeekCompleteListener(new OnSeekCompleteListener()
            {

                @Override
                public void onSeekComplete(MediaPlayer mp)
                {
                    Log.v(TAG, "onSeekComplete called");
                }
            });
            player.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener()
            {

                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
                {
                    // 这个方法在设置player的source后至少触发一次
                    Log.v(TAG, "onVideoSizeChanged called");
                }
            });
        }

    }

    public void setData(String path)
    {
        if (player != null)
        {
            try
            {
                isVedioPrepared = false;
                if (player.isPlaying())
                {
                    player.stop();
                }
                player.setDataSource(path);
                player.prepareAsync();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } catch (SecurityException e)
            {
                e.printStackTrace();
            } catch (IllegalStateException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 开始播放视频
     */
    public void start()
    {
        if (player != null && !player.isPlaying())
        {
            if (isVedioPrepared)
            {
                player.start();
            }else{
                Log.v(TAG, "视频没有准备好");
            }
        }
    }

    /**
     * 停止播放视频
     */
    public void stop()
    {
        if (player != null && player.isPlaying())
        {
            player.stop();
        }
    }

    /**
     * 释放播放器资源
     */
    public void release()
    {
        if (player != null)
        {
            player.release();
        }
    }
    
    public static class LayoutParams extends FrameLayout.LayoutParams
    {

        public LayoutParams(MarginLayoutParams source)
        {
            super(source);
        }

        public LayoutParams(android.widget.FrameLayout.LayoutParams source)
        {
            super(source);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source)
        {
            super(source);
        }

        public LayoutParams(int width, int height, int gravity)
        {
            super(width, height, gravity);
        }

        public LayoutParams(int width, int height)
        {
            super(width, height);
        }

        public LayoutParams(Context c, AttributeSet attrs)
        {
            super(c, attrs);
        }

    }
    
    public interface VideoListener
    {
        public void onVideoCompletion();
        public void onVideoPrepared(int width, int height, int duration);
        
    }

}
