package com.example.testandroid.activity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class RequestOOMTestActivity extends Activity
{

    private static final String TAG = "RequestOOMTestActivity";

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);

        Button button = new Button(this);
        button.setText("开始");
        button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                init();
            }
        });
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER;
        frameLayout.addView(button, p);
        setContentView(frameLayout);
    }

    private void init()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try
                {
                    URL url = new URL("http://test.adx.yumimobi.com/mock.php?id=123");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(false);
//                    conn.connect();
//                    Log.v(TAG, "开启链接");
                    conn.getContentLength();
                    
                    conn.disconnect();
                    Log.v(TAG, "关闭链接");
                    
//                    InputStream is = conn.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    int len = 0;
//                    char[] buffer = new char[1024];
//                    StringBuffer sb = new StringBuffer();
//                    while((len=isr.read(buffer))!=-1){
//                        sb.append(buffer, 0, len);
////                        Log.v(TAG, "len="+sb.length()+"  buf="+sb.toString());
//                    }
//                    String resp = sb.toString();
//                    Log.v(TAG, "resp="+resp);
//                    is.close();
                } catch (Exception e)
                {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }).start();
    }


}
