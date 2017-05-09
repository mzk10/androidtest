package com.example.testandroid.activity;

import com.example.testandroid.jstest.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class Png9 extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        ImageView iv = new ImageView(this);
        iv.setBackgroundResource(R.drawable.zplayad_btn_interwideoplay);
        setContentView(iv);
    }
}
