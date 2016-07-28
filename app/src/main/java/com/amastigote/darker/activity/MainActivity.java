package com.amastigote.darker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
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
    Intent intent;
    View view;
    boolean isServiceRunning = false;

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
        toolbar.setTitle("屏幕滤镜");
        setSupportActionBar(toolbar);
        DarkerSettings.initializeContext(getApplicationContext());

        checkPermissions();

        view = findViewById(R.id.cm);
        circleSeekBar_brightness = (CircleSeekBar) findViewById(R.id.cp_brightness_circleSeekBar);
        circleSeekBar_alpha = (CircleSeekBar) findViewById(R.id.cp_alpha_circleSeekBar);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.cp_colorSeekBar);
        aSwitch = (Switch) findViewById(R.id.cp_useColor_switch);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.cm_toggle_button);
        Button restore_settings_button = (Button) findViewById(R.id.cm_restore_settings_button);

        restoreLatestSettings();

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isServiceRunning = b;
                if (b)
                    collectCurrentDarkerSettings();
                else
                    ScreenFilterService.removeScreenFilter();
            }
        });

        restore_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDarkerSettings = DarkerSettings.getDefaultSettings();
                if (isServiceRunning) {
                    ScreenFilterService.removeScreenFilter();
                    isServiceRunning = false;
                    doRestore();
                    isServiceRunning = true;
                    collectCurrentDarkerSettings();
                }
                else
                    doRestore();
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
                    collectCurrentDarkerSettings();
            }
        });

        circleSeekBar_brightness.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i, int i1) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
            }
        });

        circleSeekBar_alpha.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i, int i1) {
                if (isServiceRunning)
                    collectCurrentDarkerSettings();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/hwding/make-it-darker")));
        }

        else if (id == R.id.action_licenses) {
            startActivity(new Intent(MainActivity.this, LicenseActivity.class));
        }

        else if (id == R.id.keep_screen_on_toggle) {
            if(item.isChecked())
                currentDarkerSettings.setKeepScreenOn(false);
            else
                currentDarkerSettings.setKeepScreenOn(true);
            if (isServiceRunning)
                collectCurrentDarkerSettings();
            else
                currentDarkerSettings.saveCurrentSettings();
            Snackbar.make(view, "偏好配置已保存", Snackbar.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void collectCurrentDarkerSettings() {
        currentDarkerSettings.setBrightness(((float) circleSeekBar_brightness.getCurProcess()) / 100);
        currentDarkerSettings.setAlpha(((float) circleSeekBar_alpha.getCurProcess()) / 100);
        currentDarkerSettings.setUseColor(aSwitch.isChecked());
        currentDarkerSettings.setColorBarPosition(colorSeekBar.getColorPosition());
        currentDarkerSettings.setColor(colorSeekBar.getColor());
        currentDarkerSettings.saveCurrentSettings();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.keep_screen_on_toggle).setChecked(currentDarkerSettings.isKeepScreenOn());
        return true;
    }
}
