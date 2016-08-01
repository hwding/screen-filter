package com.amastigote.darker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.amastigote.darker.R;
import com.amastigote.darker.model.DarkerSettings;
import com.amastigote.darker.service.ScreenFilterService;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import io.feeeei.circleseekbar.CircleSeekBar;

public class MainActivity extends AppCompatActivity {
    DarkerSettings currentDarkerSettings = new DarkerSettings();
    CircleSeekBar circleSeekBar_brightness;
    CircleSeekBar circleSeekBar_alpha;
    ColorSeekBar colorSeekBar;
    Switch aSwitch;
    AppCompatButton appCompatButton;
    Intent intent;
    View view;
    boolean isServiceRunning = false;
    static boolean hasSplashScreenShown = false;

    @Override
    protected void onDestroy() {
        if (intent != null)
            stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("  " + AboutActivity.getVersionCode(MainActivity.this));
        toolbar.setLogo(R.mipmap.night_128);
        setSupportActionBar(toolbar);
        DarkerSettings.initializeContext(getApplicationContext());

        checkPermissions();

        view = findViewById(R.id.cm);
        circleSeekBar_brightness = (CircleSeekBar) findViewById(R.id.cp_brightness_circleSeekBar);
        circleSeekBar_alpha = (CircleSeekBar) findViewById(R.id.cp_alpha_circleSeekBar);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.cp_colorSeekBar);
        aSwitch = (Switch) findViewById(R.id.cp_useColor_switch);
        appCompatButton = (AppCompatButton) findViewById(R.id.cm_toggle_button);

        restoreLatestSettings();

        setButtonState(false);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isServiceRunning) {
                    collectCurrentDarkerSettings(true);
                    setButtonState(true);
                    isServiceRunning = true;
                }
                else {
                    ScreenFilterService.removeScreenFilter();
                    setButtonState(false);
                    isServiceRunning = false;
                }
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AlphaAnimation alphaAnimation_0 = new AlphaAnimation(0, 1);
                    alphaAnimation_0.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_0);
                    colorSeekBar.setVisibility(View.VISIBLE);
                    currentDarkerSettings.setUseColor(true);
                }
                else {
                    AlphaAnimation alphaAnimation_1 = new AlphaAnimation(1, 0);
                    alphaAnimation_1.setDuration(300);
                    colorSeekBar.startAnimation(alphaAnimation_1);
                    colorSeekBar.setVisibility(View.INVISIBLE);
                    currentDarkerSettings.setUseColor(false);
                }
                if (isServiceRunning)
                    collectCurrentDarkerSettings(true);
            }
        });

        circleSeekBar_brightness.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i, int i1) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings(true);
            }
        });

        circleSeekBar_alpha.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i, int i1) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings(true);
            }
        });

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i, int i1, int i2) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings(true);
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
        else if (id == R.id.action_changeKeepScreenOnState) {
            if (currentDarkerSettings.isKeepScreenOn()) {
                currentDarkerSettings.setKeepScreenOn(false);
                invalidateOptionsMenu();
                Snackbar.make(view, "屏幕常亮关", Snackbar.LENGTH_LONG).show();
            }
            else {
                currentDarkerSettings.setKeepScreenOn(true);
                invalidateOptionsMenu();
                Snackbar.make(view, "屏幕常亮开", Snackbar.LENGTH_LONG).show();
            }
            if (isServiceRunning)
                collectCurrentDarkerSettings(false);
            else
                currentDarkerSettings.saveCurrentSettings();
        }
        else if (id == R.id.action_restoreDefaultSettings) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("恢复推荐配置")
                    .setMessage("此项操作将会覆盖您最新的偏好配置")
                    .setPositiveButton("恢复", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentDarkerSettings = DarkerSettings.getDefaultSettings();
                            if (isServiceRunning) {
                                ScreenFilterService.removeScreenFilter();
                                isServiceRunning = false;
                                doRestore();
                                isServiceRunning = true;
                                collectCurrentDarkerSettings(true);
                            }
                            else
                                doRestore();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_changeKeepScreenOnState);
        if (currentDarkerSettings.isKeepScreenOn())
            menuItem.setIcon(MainActivity.this.getResources().getDrawable(R.mipmap.ic_lock_outline_white_24dp));
        else
            menuItem.setIcon(MainActivity.this.getResources().getDrawable(R.mipmap.ic_lock_open_white_24dp));
        return super.onPrepareOptionsMenu(menu);
    }

    private void setButtonState(boolean isChecked) {
        if (isChecked) {
            appCompatButton.setSupportBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.toggle_button_on)));
            appCompatButton.setText(getResources().getString(R.string.is_on));
        }
        else {
            appCompatButton.setSupportBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.toggle_button_off)));
            appCompatButton.setText(getResources().getString(R.string.is_off));
        }
    }

    private void doRestore() {
        if (aSwitch.isChecked()) {
            aSwitch.setChecked(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            colorSeekBar.startAnimation(alphaAnimation);
            colorSeekBar.setVisibility(View.INVISIBLE);
        }
        circleSeekBar_brightness.setCurProcess((int) (currentDarkerSettings.getBrightness() * 100));
        circleSeekBar_alpha.setCurProcess((int) (currentDarkerSettings.getAlpha() * 100));
        invalidateOptionsMenu();
    }

    private void collectCurrentDarkerSettings(boolean showHint) {
        currentDarkerSettings.setBrightness(((float) circleSeekBar_brightness.getCurProcess()) / 100);
        currentDarkerSettings.setAlpha(((float) circleSeekBar_alpha.getCurProcess()) / 100);
        currentDarkerSettings.setUseColor(aSwitch.isChecked());
        currentDarkerSettings.setColorBarPosition(colorSeekBar.getColorPosition());
        currentDarkerSettings.setColor(colorSeekBar.getColor());
        currentDarkerSettings.saveCurrentSettings();
        if (showHint)
            Snackbar.make(view, "偏好配置已保存", Snackbar.LENGTH_LONG).show();
        ScreenFilterService.updateScreenFilter(currentDarkerSettings);
    }

    private void restoreLatestSettings() {
        currentDarkerSettings =  DarkerSettings.getCurrentSettings();
        circleSeekBar_alpha.setCurProcess((int) (currentDarkerSettings.getAlpha() * 100));
        circleSeekBar_brightness.setCurProcess((int) (currentDarkerSettings.getBrightness() * 100));
        if (currentDarkerSettings.isUseColor()) {
            aSwitch.setChecked(true);
            colorSeekBar.setVisibility(View.VISIBLE);
        }
        colorSeekBar.setColorBarValue((int) currentDarkerSettings.getColorBarPosition());
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
            else
                prepareForService();
        }
        else
            prepareForService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this))
                    prepareForService();
                else {
                    Toast.makeText(getApplicationContext(), "权限请求被拒绝 无法正常工作 :(", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void prepareForService() {
        intent = new Intent(getApplicationContext(), ScreenFilterService.class);
        startService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("完全退出")
                    .setMessage("此项操作将会关闭滤镜并完全退出应用")
                    .setPositiveButton("完全退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("后台运行", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            moveTaskToBack(true);
                        }
                    })
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
