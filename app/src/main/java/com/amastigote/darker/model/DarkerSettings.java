package com.amastigote.darker.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.WindowManager;

public class DarkerSettings {
    float brightness;
    float alpha;
    boolean useColor;
    float colorBarPosition;
    int color;
    boolean keepScreenOn;

    private static Context context = null;
    private static SharedPreferences sharedPreferences_default = null;
    private static SharedPreferences sharedPreferences_current = null;
    private static final String BRIGHTNESS = "BRIGHTNESS";
    private static final String ALPHA = "ALPHA";
    private static final String COLOR_BAR_POSITION = "COLOR_BAR_POSITION";
    private static final String USE_COLOR = "USE_COLOR";
    private static final String KEEP_SCREEN_ON = "KEEP_SCREEN_ON";
    private static final float ALPHA_DEFAULT = 0.4F;
    private static final float COLOR_BAR_POSITION_DEFAULT = 0.0F;
    private static final boolean USE_COLOR_DEFAULT = false;
    private static final boolean KEEP_SCREEN_ON_DEFAULT = false;

    public static void initializeContext(Context context) {
        DarkerSettings.context = context;
        initializeSharedPreferences();
    }

    private static void initializeSharedPreferences() {
        sharedPreferences_default = DarkerSettings.context
                .getSharedPreferences("user_settings_default",
                        Context.MODE_PRIVATE);
        sharedPreferences_current = DarkerSettings.context
                .getSharedPreferences("user_settings_current",
                        Context.MODE_PRIVATE);
    }

    public DarkerSettings() {}

    public static DarkerSettings getDefaultSettings() {
        return getSettings(sharedPreferences_default);
    }

    public static DarkerSettings getCurrentSettings() {
        return getSettings(sharedPreferences_current);
    }

    private static DarkerSettings getSettings(SharedPreferences sharedPreferences) {
        DarkerSettings thisDarkerSettings = new DarkerSettings();

        float thisBrightness = sharedPreferences.getFloat(BRIGHTNESS,
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF);
        float thisAlpha = sharedPreferences.getFloat(ALPHA,
                ALPHA_DEFAULT);
        float thisColorBarPosition = sharedPreferences.getFloat(COLOR_BAR_POSITION,
                COLOR_BAR_POSITION_DEFAULT);
        boolean thisUseColor = sharedPreferences.getBoolean(USE_COLOR,
                USE_COLOR_DEFAULT);
        boolean thisKeepScreenOn = sharedPreferences.getBoolean(KEEP_SCREEN_ON,
                KEEP_SCREEN_ON_DEFAULT);

        thisDarkerSettings.setBrightness(thisBrightness);
        thisDarkerSettings.setAlpha(thisAlpha);
        thisDarkerSettings.setColorBarPosition(thisColorBarPosition);
        thisDarkerSettings.setUseColor(thisUseColor);
        thisDarkerSettings.setKeepScreenOn(thisKeepScreenOn);

        return thisDarkerSettings;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getColorBarPosition() {
        return colorBarPosition;
    }

    public void setColorBarPosition(float colorBarPosition) {
        this.colorBarPosition = colorBarPosition;
    }

    public void setUseColor(boolean useColor) {
        this.useColor = useColor;
    }

    public float getBrightness() {
        return brightness;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isUseColor() {
        return useColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public void saveCurrentSettings() {
        SharedPreferences.Editor editor = sharedPreferences_current.edit();
        editor.putFloat(BRIGHTNESS, brightness);
        editor.putFloat(ALPHA, alpha);
        editor.putFloat(COLOR_BAR_POSITION, colorBarPosition);
        editor.putBoolean(USE_COLOR, useColor);
        editor.putBoolean(KEEP_SCREEN_ON, keepScreenOn);
        editor.apply();
    }
}
