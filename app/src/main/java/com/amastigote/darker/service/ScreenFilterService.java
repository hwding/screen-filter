package com.amastigote.darker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.amastigote.darker.R;

public class ScreenFilterService extends Service{
    LinearLayout linearLayout;
    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createScreenFilter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (linearLayout != null)
            windowManager.removeView(linearLayout);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings(value = "all")
    private void createScreenFilter() {
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
        linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.screen_filter, null);
        windowManager.addView(linearLayout, layoutParams);
    }

    public void updateScreenFilter() {}
}
