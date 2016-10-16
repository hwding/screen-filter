package com.amastigote.darker.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.amastigote.darker.R;
import com.amastigote.darker.model.DarkerSettings;

public class ScreenFilterService extends Service{
    @SuppressLint("StaticFieldLeak")
    static LinearLayout linearLayout;
    static WindowManager.LayoutParams layoutParams;
    static WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createScreenFilter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            windowManager.removeViewImmediate(linearLayout);
        } catch (Exception ignored) {}
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
        layoutParams.width = windowManager_tmp.getDefaultDisplay().getHeight();
        layoutParams.height = windowManager_tmp.getDefaultDisplay().getHeight();
        layoutParams.format = PixelFormat.TRANSLUCENT;

        LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
        linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.screen_filter, null);
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
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    }

    public static void removeScreenFilter() {
        windowManager.removeViewImmediate(linearLayout);
    }
}
