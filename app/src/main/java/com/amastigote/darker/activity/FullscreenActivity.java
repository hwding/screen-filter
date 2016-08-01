package com.amastigote.darker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import com.amastigote.darker.R;

public class FullscreenActivity extends AppCompatActivity {
    final static long DELAY_MILLISECOND = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainActivity.hasSplashScreenShown) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.screen_splash);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(FullscreenActivity.this, MainActivity.class));
                    MainActivity.hasSplashScreenShown = true;
                    finish();
                }
            }, DELAY_MILLISECOND);
        }
        else {
            startActivity(new Intent(FullscreenActivity.this, MainActivity.class));
            finish();
        }
    }
}
