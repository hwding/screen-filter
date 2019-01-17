package com.amastigote.darker.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.amastigote.darker.R;
import com.amastigote.darker.model.DarkerSettings;

//import android.support.v7.app.AppCompatActivity;
//import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompatApi21.getWindow;

public class ScreenFilterService extends Service {
    @SuppressLint("StaticFieldLeak")
    static LinearLayout linearLayout;
    static WindowManager.LayoutParams layoutParams;
    static WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createScreenFilter();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            windowManager.removeViewImmediate(linearLayout);
        } catch (Exception ignored) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings(value = "all")
    private void createScreenFilter() {
        WindowManager windowManager_tmp = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        int temp = getNavigationBarHeight(getApplicationContext());
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager_tmp.getDefaultDisplay().getRealMetrics(metrics);
        layoutParams.width = metrics.widthPixels;
        layoutParams.height = metrics.heightPixels + temp;

        layoutParams.format = PixelFormat.TRANSLUCENT;

        LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
        linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.screen_filter, null);
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    public static void updateScreenFilter(DarkerSettings darkerSettings) {
        updateThisSettings(darkerSettings);
        windowManager.updateViewLayout(linearLayout, layoutParams);
    }

    public static void activateScreenFilter(DarkerSettings darkerSettings) {
        updateThisSettings(darkerSettings);
        windowManager.addView(linearLayout, layoutParams);
    }

    private static void updateThisSettings(DarkerSettings darkerSettings) {
        if (darkerSettings.isUseBrightness())
            layoutParams.screenBrightness = darkerSettings.getBrightness();
        else
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

        layoutParams.alpha = darkerSettings.getAlpha();

        if (darkerSettings.isUseColor())
            linearLayout.setBackgroundColor(darkerSettings.getColor());
        else
            linearLayout.setBackgroundColor(Color.BLACK);

        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        //        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    }

    public static void removeScreenFilter() {
        windowManager.removeViewImmediate(linearLayout);
    }
/*
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }*/
}
