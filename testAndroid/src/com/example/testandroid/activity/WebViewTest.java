package com.example.testandroid.activity;

import java.io.IOException;
import java.io.InputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.OnFinished;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewTest extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		Log.e("WebViewTest", "sdcard = " + sdcard);
		WebView webView = new WebView(this);
		WebSettings webSettings = webView.getSettings();
		// webSettings.setJavaScriptEnabled(true);
		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// webSettings.setSupportZoom(false);
		// webSettings.setBuiltInZoomControls(false);
		// webSettings.setUseWideViewPort(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		if (Build.VERSION.SDK_INT >= 21) {
		     webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		
		webView.setHorizontalScrollbarOverlay(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setHorizontalScrollbarOverlay(true);
		webView.setBackgroundColor(0);

		webView.setWebViewClient(new MyWebViewClient());

		try {
			InputStream is = getAssets().open("new.html");
			byte[] buffer = new byte[2400];
			int read = is.read(buffer);
			String html = new String(buffer, 0, read);
			System.out.println(html);
			is.close();
			webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.i("TAGTAG", "完成");
		}
		
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
//			final SslErrorHandler sslHadnler = handler;
//			final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewTest.this);
//			String message = "SSL Certificate error.";
//			switch (error.getPrimaryError()) {
//			case SslError.SSL_UNTRUSTED:
//				message = "The certificate authority is not trusted.";
//				break;
//			case SslError.SSL_EXPIRED:
//				message = "The certificate has expired.";
//				break;
//			case SslError.SSL_IDMISMATCH:
//				message = "The certificate Hostname mismatch.";
//				break;
//			case SslError.SSL_NOTYETVALID:
//				message = "The certificate is not yet valid.";
//				break;
//			}
//			message += " Do you want to continue anyway?";
//
//			builder.setTitle("SSL Certificate Error");
//			builder.setMessage(message);
//			builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					sslHadnler.proceed();
//				}
//			});
//			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					sslHadnler.cancel();
//				}
//			});
//			final AlertDialog dialog = builder.create();
//			dialog.show();
		}

	}

}
