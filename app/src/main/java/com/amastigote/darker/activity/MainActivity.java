package com.amastigote.darker.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.amastigote.darker.R;
import com.amastigote.darker.model.DarkerNotification;
import com.amastigote.darker.model.DarkerSettings;
import com.amastigote.darker.service.ScreenFilterService;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainActivity extends AppCompatActivity {
    DarkerSettings currentDarkerSettings = new DarkerSettings();
    DarkerNotification darkerNotification;
    TextView brightness_indicator;
    TextView alpha_indicator;
    CircularSeekBar circleSeekBar_brightness;
    CircularSeekBar circleSeekBar_alpha;
    ColorSeekBar colorSeekBar;
    Switch aSwitch;
    Switch bSwitch;
    AppCompatButton appCompatButton;
    Intent intent;
    View view;
    BroadcastReceiver broadcastReceiver;
    boolean isServiceRunning = false;

    @Override
    protected void onDestroy() {
        doCleanBeforeExit();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleMarginStart(100);
        toolbar.setLogo(R.mipmap.night_128);
        setSupportActionBar(toolbar);

        DarkerSettings.initializeContext(getApplicationContext());

        checkPermissions();

        darkerNotification = new DarkerNotification(MainActivity.this);
        darkerNotification.updateStatus(isServiceRunning);

        view = findViewById(R.id.cm);

        circleSeekBar_brightness = (CircularSeekBar) findViewById(R.id.cp_brightness_circleSeekBar);
        circleSeekBar_alpha = (CircularSeekBar) findViewById(R.id.cp_alpha_circleSeekBar);

        brightness_indicator = (TextView) findViewById(R.id.cp_brightness_indicator);
        alpha_indicator = (TextView) findViewById(R.id.cp_alpha_indicator);

        colorSeekBar = (ColorSeekBar) findViewById(R.id.cp_colorSeekBar);

        aSwitch = (Switch) findViewById(R.id.cp_useColor_switch);
        bSwitch = (Switch) findViewById(R.id.cp_useBrightness_switch);

        appCompatButton = (AppCompatButton) findViewById(R.id.cm_toggle_button);

        restoreLatestSettings();

        setButtonState(false);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isServiceRunning) {
                    collectCurrentDarkerSettings();
                    setButtonState(true);
                    isServiceRunning = true;
                } else {
                    ScreenFilterService.removeScreenFilter();
                    setButtonState(false);
                    isServiceRunning = false;
                }
                darkerNotification.updateStatus(isServiceRunning);
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DarkerNotification.PRESS_BUTTON.equals(intent.getAction())) {
                    if (!isServiceRunning) {
                        collectCurrentDarkerSettings();
                        setButtonState(true);
                        isServiceRunning = true;
                    } else {
                        ScreenFilterService.removeScreenFilter();
                        setButtonState(false);
                        isServiceRunning = false;
                    }
                    darkerNotification.updateStatus(isServiceRunning);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DarkerNotification.PRESS_BUTTON);
        registerReceiver(broadcastReceiver, intentFilter);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlphaAnimation alphaAnimation_0 = new AlphaAnimation(0, 1);
                    alphaAnimation_0.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_0);
                    colorSeekBar.setVisibility(View.VISIBLE);
                    currentDarkerSettings.setUseColor(true);
                } else {
                    AlphaAnimation alphaAnimation_1 = new AlphaAnimation(1, 0);
                    alphaAnimation_1.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_1);
                    colorSeekBar.setVisibility(View.INVISIBLE);
                    currentDarkerSettings.setUseColor(false);
                }
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlphaAnimation alphaAnimation_0 = new AlphaAnimation(0, 1);
                    alphaAnimation_0.setDuration(300);
                    circleSeekBar_brightness.startAnimation(alphaAnimation_0);
                    circleSeekBar_brightness.setVisibility(View.VISIBLE);
                    brightness_indicator.setText(String.valueOf((int) circleSeekBar_brightness.getProgress()));
                    currentDarkerSettings.setUseBrightness(true);
                } else {
                    AlphaAnimation alphaAnimation_1 = new AlphaAnimation(1, 0);
                    alphaAnimation_1.setDuration(300);
                    circleSeekBar_brightness.startAnimation(alphaAnimation_1);
                    circleSeekBar_brightness.setVisibility(View.INVISIBLE);
                    brightness_indicator.setText(R.string.auto_brightness);
                    currentDarkerSettings.setUseBrightness(true);
                }
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });

        circleSeekBar_brightness.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                brightness_indicator.setText(String.valueOf((int) progress));
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        circleSeekBar_alpha.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                alpha_indicator.setText(String.valueOf((int) progress));
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about)
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        else if (id == R.id.action_restoreDefaultSettings) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("重置配置面板")
                    .setMessage("将覆盖您的偏好配置并恢复为推荐配置")
                    .setPositiveButton("确认重置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentDarkerSettings = DarkerSettings.getDefaultSettings();
                            if (isServiceRunning) {
                                doRestore();
                                collectCurrentDarkerSettings();
                            } else
                                doRestore();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    private void setButtonState(boolean isChecked) {
        if (isChecked) {
            final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    ContextCompat.getColor(this, R.color.toggle_button_off),
                    ContextCompat.getColor(this, R.color.toggle_button_on));
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int trans_color = (int) valueAnimator.getAnimatedValue();
                    appCompatButton.setSupportBackgroundTintList(
                            ColorStateList.valueOf(trans_color));
                }
            });
            valueAnimator.start();
            appCompatButton.setText(getResources().getString(R.string.is_on));
        } else {
            final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    ContextCompat.getColor(this, R.color.toggle_button_on),
                    ContextCompat.getColor(this, R.color.toggle_button_off));
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int trans_color = (int) valueAnimator.getAnimatedValue();
                    appCompatButton.setSupportBackgroundTintList(
                            ColorStateList.valueOf(trans_color));
                }
            });
            valueAnimator.start();
            appCompatButton.setText(getResources().getString(R.string.is_off));
        }
    }

    private void doRestore() {
        {
            boolean isCurrentServiceRunning = false;
            if (isServiceRunning) {
                isCurrentServiceRunning = true;
                isServiceRunning = false;
            }
            circleSeekBar_brightness.setProgress(currentDarkerSettings.getBrightness() * 100);
            circleSeekBar_alpha.setProgress(currentDarkerSettings.getAlpha() * 100);
            if (isCurrentServiceRunning)
                isServiceRunning = true;
        }

        if (aSwitch.isChecked()) {
            aSwitch.setChecked(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            colorSeekBar.startAnimation(alphaAnimation);
            colorSeekBar.setVisibility(View.INVISIBLE);
        }

        if (bSwitch.isChecked()) {
            bSwitch.setChecked(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            circleSeekBar_brightness.startAnimation(alphaAnimation);
            circleSeekBar_brightness.setVisibility(View.INVISIBLE);
            brightness_indicator.setText(R.string.auto_brightness);
        }
        invalidateOptionsMenu();
    }

    private void collectCurrentDarkerSettings() {
        currentDarkerSettings.setBrightness(circleSeekBar_brightness.getProgress() / 100);
        currentDarkerSettings.setAlpha(circleSeekBar_alpha.getProgress() / 100);
        currentDarkerSettings.setUseColor(aSwitch.isChecked());
        currentDarkerSettings.setUseBrightness(bSwitch.isChecked());
        currentDarkerSettings.setColorBarPosition(colorSeekBar.getColorPosition());
        currentDarkerSettings.setColor(colorSeekBar.getColor());
        currentDarkerSettings.saveCurrentSettings();

        if (isServiceRunning)
            ScreenFilterService.updateScreenFilter(currentDarkerSettings);
        else {
            try {
                ScreenFilterService.activateScreenFilter(currentDarkerSettings);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    private void restoreLatestSettings() {
        currentDarkerSettings = DarkerSettings.getCurrentSettings();
        circleSeekBar_brightness.setProgress(currentDarkerSettings.getBrightness() * 100);
        circleSeekBar_alpha.setProgress(currentDarkerSettings.getAlpha() * 100);
        brightness_indicator.setText(String.valueOf((int) circleSeekBar_brightness.getProgress()));
        alpha_indicator.setText(String.valueOf((int) circleSeekBar_alpha.getProgress()));
        if (currentDarkerSettings.isUseColor()) {
            aSwitch.setChecked(true);
            colorSeekBar.setVisibility(View.VISIBLE);
        } else {
            aSwitch.setChecked(false);
            colorSeekBar.setVisibility(View.INVISIBLE);
        }
        if (currentDarkerSettings.isUseBrightness()) {
            bSwitch.setChecked(true);
            circleSeekBar_brightness.setVisibility(View.VISIBLE);
        } else {
            bSwitch.setChecked(false);
            circleSeekBar_brightness.setVisibility(View.INVISIBLE);
            brightness_indicator.setText(R.string.auto_brightness);
        }
        colorSeekBar.setColorBarValue((int) currentDarkerSettings.getColorBarPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this))
                    prepareForService();
                else {
                    doCleanBeforeExit();
                    finish();
                    System.exit(0);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            } else
                prepareForService();
        } else
            prepareForService();
    }

    private void prepareForService() {
        intent = new Intent(getApplicationContext(), ScreenFilterService.class);
        /*Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        intent.putExtra("height",point.y);*/
        startService(intent);
    }

    private void doCleanBeforeExit() {
        try {
            darkerNotification.removeNotification();
            unregisterReceiver(broadcastReceiver);
            stopService(intent);
        } catch (Exception ignored) {
        }
    }
}
