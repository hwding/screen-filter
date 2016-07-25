package com.amastigote.darker.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.WindowManager;

public class DarkerSettings {
    float brightness;
    float alpha;
    int color;
    boolean useColor;

    private static Context context = null;
    private static SharedPreferences sharedPreferences_default = null;
    private static SharedPreferences sharedPreferences_current = null;
    private static final String BRIGHTNESS = "BRIGHTNESS";
    private static final String ALPHA = "ALPHA";
    private static final String COLOR = "COLOR";
    private static final String USE_COLOR = "USE_COLOR";
    public static final float ALPHA_MINIMUM = 0.0F;
    public static final float ALPHA_MAXIMUM = 1.0F;
    public static final float ALPHA_DEFAULT = 0.4F;
    public static final float BRIGHTNESS_AUTO = -1.0F;
    private static final boolean USE_COLOR_DEFAULT = false;

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
        int thisColor = sharedPreferences.getInt(COLOR,
                0);
        boolean thisUseColor = sharedPreferences.getBoolean(USE_COLOR,
                USE_COLOR_DEFAULT);

        thisDarkerSettings.setBrightness(thisBrightness);
        thisDarkerSettings.setAlpha(thisAlpha);
        thisDarkerSettings.setColor(thisColor);
        thisDarkerSettings.setUseColor(thisUseColor);

        return thisDarkerSettings;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setColor(int color) {
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public boolean isUseColor() {
        return useColor;
    }

    public void saveCurrentSettings() {
        SharedPreferences.Editor editor = sharedPreferences_current.edit();
        editor.putFloat(BRIGHTNESS, brightness);
        editor.putFloat(ALPHA, alpha);
        editor.putInt(COLOR, color);
        editor.putBoolean(USE_COLOR, useColor);
        editor.apply();
    }
}
