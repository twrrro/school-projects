// app/src/main/java/com/example/ebookreader/managers/SettingsManager.java
package com.example.ebookreader.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREFS_NAME = "EBookReaderPrefs";
    private static final String KEY_FONT_SIZE_MULTIPLIER = "fontSizeMultiplier"; // Yazı tipi boyutu için çarpan
    private static final String KEY_DARK_MODE = "darkModeEnabled"; // Karanlık mod için

    private static SettingsManager instance;
    private SharedPreferences sharedPreferences;

    // Varsayılan yazı tipi boyutları (örnek)
    public static final float FONT_SIZE_SMALL = 0.8f;
    public static final float FONT_SIZE_MEDIUM = 1.0f;
    public static final float FONT_SIZE_LARGE = 1.2f;

    private SettingsManager(Context context) {
        // Application context kullanmak memory leak'leri önler
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Kaydedilmiş yazı tipi boyutu çarpanını alır.
     * Varsayılan olarak orta boy (1.0f).
     * @return float yazı tipi boyutu çarpanı
     */
    public float getFontSizeMultiplier() {
        return sharedPreferences.getFloat(KEY_FONT_SIZE_MULTIPLIER, FONT_SIZE_MEDIUM);
    }

    /**
     * Yeni yazı tipi boyutu çarpanını kaydeder.
     * @param multiplier Kaydedilecek çarpan (örn: 0.8f, 1.0f, 1.2f)
     */
    public void setFontSizeMultiplier(float multiplier) {
        sharedPreferences.edit().putFloat(KEY_FONT_SIZE_MULTIPLIER, multiplier).apply();
    }

    /**
     * Karanlık modun aktif olup olmadığını kontrol eder.
     * @return true eğer karanlık mod aktifse, değilse false.
     */
    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false); // Varsayılan: Karanlık mod kapalı
    }

    /**
     * Karanlık mod tercihini kaydeder.
     * @param enabled true ise karanlık mod aktif edilir, false ise pasif edilir.
     */
    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }
}
