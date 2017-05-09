package com.example.testandroid.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JavaScriptTest extends Activity
{
    private WebView webView;
    private static final String TAG = "JavaScriptTest";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init()
    {
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(false);
        webView.setHorizontalScrollbarOverlay(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(true);
        webView.setBackgroundColor(0);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        setContentView(webView);
        try
        {
            webView.addJavascriptInterface(this, "objectname");
            webView.setWebViewClient(new WVC());
            InputStream is = getAssets().open("new.html");
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            char[] buf = new char[6144];
            int read = isr.read(buf);
            String html = new String(buf,0,read);
            Log.i(TAG, "html="+html);
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public class WVC extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            Log.i(TAG, "onPageFinished");
            try
            {
                InputStream is = getAssets().open("test.js");
                byte[] buffer = new byte[1024];
                int len = is.read(buffer);
                String js = new String(buffer, 0, len, "UTF-8");
                webView.evaluateJavascript(js, new ValueCallback<String>()
                {
                    @Override
                    public void onReceiveValue(String value)
                    {
                        Log.i(TAG, "onReceiveValue:"+value);
                    }
                });
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    @JavascriptInterface
    public void yumi_ad_show(String[] url)
    {
        for (int i = 0; i < url.length; i++)
        {
            Log.i(TAG, "yumi_ad_show:"+url[i]);
        }
    }
    
    @JavascriptInterface
    public void adtest(String result)
    {
        Log.i(TAG, "adtest:" + result);
    }
    
}
