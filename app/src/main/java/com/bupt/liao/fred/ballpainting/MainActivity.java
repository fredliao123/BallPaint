package com.bupt.liao.fred.ballpainting;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bupt.liao.fred.ballpainting.view.WorldSurfaceView;

public class MainActivity extends AppCompatActivity {
    View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐去标题栏（程序的名字）
        setContentView(new WorldSurfaceView(this));
        mContentView = getContentView();
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private View getContentView() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        LinearLayout content = (LinearLayout) view.getChildAt(0);
        return content.getChildAt(1);//此处定位到surfaceview
    }
}
