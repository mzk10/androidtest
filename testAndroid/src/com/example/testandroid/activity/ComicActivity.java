package com.example.testandroid.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.testandroid.jstest.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ComicActivity extends Activity implements OnClickListener
{

	private static final String LOCAL_OBJ = "LOCAL_OBJ";
	private static final String TAG = "WebImgList";

	private static final int DOWNLOAD_FAILED = 0x00001;
	private static final int DOWNLOAD_INC = 0x00002;
	private static final int DOWNLOAD_COMPLATE = 0x00003;

	private WebView wv;
//	private String js;
	private String title;
	private TextView tv;
	private CharSequence clipurl;

	private int max_size;
	private int loc_size;

	private int pool_size;
	private SharedPreferences sp;
	private String savedir;
	
	
	private static final int ID_TIQU = 0x20002;
	private static final int ID_TIQU_99 = 0x20003;
	private static final int ID_CHANGE = 0x20001;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == DOWNLOAD_FAILED)
			{
				Toast.makeText(getApplicationContext(), "创建目录失败", Toast.LENGTH_SHORT).show();
			} else if (msg.what == DOWNLOAD_INC)
			{
				tv.setText(title + "\n" + loc_size + "/" + max_size);
			} else if (msg.what == DOWNLOAD_COMPLATE)
			{
				tv.setText("");
				tv.setVisibility(View.GONE);
			}
		};
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		pool_size = sp.getInt("pool_size", 10);

		if (Build.VERSION.SDK_INT >= 11)
		{
			ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboardManager.hasPrimaryClip())
			{
				clipurl = clipboardManager.getPrimaryClip().getItemAt(0).getText();
			}
		} else
		{
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(
			        Context.CLIPBOARD_SERVICE);
			clipboardManager.setText("内容");
			if (clipboardManager.hasText())
			{
				clipurl = clipboardManager.getText();
			}
		}

//		js = getStringByAssets("getWebImages.js");
		
		{
			FrameLayout.LayoutParams params_wv = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
			        FrameLayout.LayoutParams.MATCH_PARENT);
			params_wv.gravity = Gravity.CENTER;
			wv = new WebView(this);
//			wv.getSettings().setUserAgentString("");
			wv.setBackgroundColor(0x00000000);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.addJavascriptInterface(this, LOCAL_OBJ);
			addContentView(wv, params_wv);
			wv.setWebViewClient(new WebViewClient()
			{

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageFinished(WebView view, String url)
				{
					super.onPageFinished(view, url);
				}
			});
		}

		{
			FrameLayout.LayoutParams params_btn = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
			        FrameLayout.LayoutParams.WRAP_CONTENT);
			params_btn.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
			MyButton btn = new MyButton(this);
			btn.setId(ID_CHANGE);
			btn.setText("切换");
			btn.setOnClickListener(this);
			addContentView(btn, params_btn);
		}
		{
			FrameLayout.LayoutParams params_bottom = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
			        FrameLayout.LayoutParams.WRAP_CONTENT);
			params_bottom.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			addContentView(ll, params_bottom);
			
			{
				LinearLayout.LayoutParams params_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				MyButton btn = new MyButton(this);
				btn.setId(ID_TIQU);
				btn.setText("提取");
				btn.setOnClickListener(this);
				ll.addView(btn, params_btn);
			}
			{
				LinearLayout.LayoutParams params_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				MyButton btn = new MyButton(this);
				btn.setId(ID_TIQU_99);
				btn.setText("提取99");
				btn.setOnClickListener(this);
				ll.addView(btn, params_btn);
			}
		}
		{
			tv = new TextView(this);
			tv.setBackgroundColor(0xaa000000);
			tv.setTextColor(0xffffffff);
			tv.setPadding(20, 10, 20, 10);
			tv.setGravity(Gravity.CENTER);
			FrameLayout.LayoutParams params_tv = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
			        FrameLayout.LayoutParams.WRAP_CONTENT);
			params_tv.gravity = Gravity.CENTER;
			addContentView(tv, params_tv);
			tv.setVisibility(View.GONE);
		}
		if (clipurl != null && !"".equals(clipurl))
		{
			showChangeDialog(clipurl);
		}
	}

	
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case ID_TIQU:
			title = wv.getTitle();
			getList(getStringByAssets("getWebImages.js"));
			break;
		case ID_TIQU_99:
			title = wv.getTitle();
			getList(getStringByAssets("getImgList99.js"));
			break;
		case ID_CHANGE:
			showChangeDialog(null);
			break;
		default:
			break;
		}

	}
	
	public String getStringByAssets(String name)
	{
		try
		{
			InputStream is = getAssets().open(name);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			char[] buffer = new char[2048];
			int length = isr.read(buffer);
			String str = String.copyValueOf(buffer, 0, length);
			return str;
		} catch (IOException e)
		{
			return "";
		}
	}

	public void showChangeDialog(CharSequence u)
	{
		final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setCancelable(true);
		FrameLayout bg = new FrameLayout(this);
		bg.setBackgroundColor(0x88000000);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setBackgroundColor(0xffffffff);
		ll.setPadding(10, 10, 10, 10);
		FrameLayout.LayoutParams params_ll = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
		        FrameLayout.LayoutParams.WRAP_CONTENT);
		params_ll.gravity = Gravity.CENTER;
		bg.addView(ll, params_ll);

		final EditText et = new EditText(this);
		String url = wv.getUrl();
		if (url != null && !"".equals(url))
		{
			et.setText(url);
		} else if (u != null && !"".equals(u))
		{
			et.setText(u);
		} else
		{
			et.setText("http://");
		}
		LinearLayout.LayoutParams params_et = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		        LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.addView(et, params_et);

		MyButton btn = new MyButton(this);
		LinearLayout.LayoutParams params_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		        LinearLayout.LayoutParams.WRAP_CONTENT);
		params_btn.gravity = Gravity.CENTER_HORIZONTAL;
		btn.setText("确认");
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String url = et.getText().toString();
				wv.loadUrl(url);
				dialog.dismiss();
			}
		});
		ll.addView(btn, params_btn);
		dialog.setContentView(bg);
		dialog.show();
	}

	public void getList(String js)
	{
		if (Build.VERSION.SDK_INT >= 19)
		{
			wv.evaluateJavascript(js, new ValueCallback<String>()
			{
				@Override
				public void onReceiveValue(String value)
				{
					getWebImages(value);
				}
			});
		} else
		{
			wv.loadUrl("javascript:window." + LOCAL_OBJ + ".getWebImages(" + js + ");");
		}
	}

	@JavascriptInterface
	private void getWebImages(String value)
	{
		System.out.println("value="+value);
		value = value.replaceAll("\"", "");
		String[] imgList = value.split(";");
		for (int i = 0; i < imgList.length; i++)
		{
			Log.i(TAG, "imgurl="+imgList[i]);
		}
		tv.setVisibility(View.VISIBLE);
		save(imgList);
	}

	private void save(final String[] list)
	{
		if (title == null || "".equals(title))
		{
			title = String.valueOf(System.currentTimeMillis());
		}

		ExecutorService pool = Executors.newFixedThreadPool(pool_size);
		max_size = list.length;
		loc_size = 0;
		Log.v(TAG, "title=" + title);
		savedir = "/mnt/sdcard/image/comic/" + title;
		File file_dir = new File(savedir);
		if (file_dir.exists() || file_dir.mkdirs())
		{
			for (int i = 0; i < list.length; i++)
			{
				final String url = list[i];
				Runnable run = new Runnable()
				{
					@Override
					public void run()
					{
						if (url != null && !"".equals(url))
						{
							String name = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));
							String ext = url.substring(url.lastIndexOf("."), url.length());
							Log.v(TAG, "name=" + name + ext);
							FileOutputStream fos = null;
							try
							{
								URL u = new URL(url);
								URLConnection conn = u.openConnection();
								InputStream is = conn.getInputStream();
								fos = new FileOutputStream(new File(savedir + name + ext));
								byte[] buffer = new byte[2048];
								int len = 0;
								while ((len = is.read(buffer)) != -1)
								{
									fos.write(buffer, 0, len);
								}
								fos.flush();
								Message msg = new Message();
								loc_size++;
								msg.what = DOWNLOAD_INC;
								handler.sendMessage(msg);
								if (loc_size >= max_size)
								{
									Message msg_com = new Message();
									msg_com.what = DOWNLOAD_COMPLATE;
									handler.sendMessage(msg_com);
								}
							} catch (MalformedURLException e)
							{
								e.printStackTrace();
							} catch (IOException e)
							{
								e.printStackTrace();
							} finally
							{
								if (fos != null)
								{
									try
									{
										fos.close();
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}
						}
					}
				};
				pool.execute(run);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == 4)
		{
			if (wv != null && wv.canGoBack())
			{
				wv.goBack();
				return true;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			showInputDialog("同时下载几张图片", new String[]
				{
				        String.valueOf(pool_size)
				}, new String[]
				{
				        ""
				}, new OnInputCallBack()
				{
					@Override
					public void onCallBack(String[] params)
					{
						String size = params[0];
						try
						{
							int parseInt = Integer.parseInt(size);
							pool_size = parseInt;
							sp.edit().putInt("pool_size", pool_size).commit();
						} catch (Exception e)
						{
						}
					}
				});
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public static boolean isFullScreen(Activity activity)
	{
		return ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0);
	}

	public void showInputDialog(String title, String[] defs, String[] Hints, final OnInputCallBack callBack)
	{
		boolean isfullScreen = isFullScreen(this);
		Dialog tmpdialog = null;
		if (isfullScreen)
		{
			tmpdialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		} else
		{
			tmpdialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		}
		tmpdialog.setCanceledOnTouchOutside(true);
		final Dialog dialog = tmpdialog;
		LinearLayout view_dialog = new LinearLayout(this);
		view_dialog.setBackgroundColor(0x99000000);
		view_dialog.setPadding(10, 10, 10, 10);
		view_dialog.setGravity(Gravity.CENTER);
		view_dialog.setOrientation(LinearLayout.VERTICAL);

		// Title
		TextView tv_title = new TextView(this);
		tv_title.setText(title);
		tv_title.setTextSize(20);
		tv_title.setBackgroundColor(0xff000000);
		tv_title.setPadding(10, 10, 10, 10);
		tv_title.setGravity(Gravity.CENTER);
		view_dialog.addView(tv_title);

		final List<EditText> tvlist = new ArrayList<EditText>();
		if (defs != null && Hints != null && (defs.length == Hints.length))
		{
			for (int i = 0; i < Hints.length; i++)
			{
				EditText et_height = new EditText(this);
				// et_height.setBackgroundResource(R.drawable.edit_bg);
				et_height.setTextColor(0xff000000);
				et_height.setHint(Hints[i]);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				        LayoutParams.WRAP_CONTENT);
				et_height.setLayoutParams(params);
				et_height.setText(defs[i]);
				view_dialog.addView(et_height);
				tvlist.add(et_height);
			}
		}

		Button btn = new Button(this);
		btn.setText("确定");
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (callBack != null)
				{
					String params[] = new String[tvlist.size()];
					for (int i = 0; i < tvlist.size(); i++)
					{
						EditText et = tvlist.get(i);
						String param = et.getText().toString();
						params[i] = param;
					}
					callBack.onCallBack(params);
					dialog.cancel();
				}
			}
		});
		view_dialog.addView(btn);
		dialog.setContentView(view_dialog);
		dialog.show();
	}

	public interface OnInputCallBack
	{
		public void onCallBack(String[] params);
	}
	
	public class MyButton extends Button
	{
		public MyButton(Context context)
		{
			super(context);
			setBackgroundResource(R.drawable.btn_btn);
			setPadding(20, 10, 20, 10);
			setTextSize(20);
			setTextColor(0xffffffff);
		}
	}
	
	
	

}
