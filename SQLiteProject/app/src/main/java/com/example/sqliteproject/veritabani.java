package com.example.sqliteproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class veritabani extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "Ogrenciler.db";
    private static final int SURUM = 1;

    public static final String TABLO_OGRENCI_BILGI = "OgrenciBilgi";
    public static final String SUTUN_AD = "ad";
    public static final String SUTUN_SOYAD = "soyad";
    public static final String SUTUN_YAS = "yas";
    public static final String SUTUN_SEHIR = "sehir";

    private static final String TAG = "veritabani";

    private static final String TABLO_OLUSTUR_OGRENCI_BILGI = "CREATE TABLE " + TABLO_OGRENCI_BILGI + " (" +
            SUTUN_AD + " TEXT NOT NULL, " +
            SUTUN_SOYAD + " TEXT NOT NULL, " +
            SUTUN_YAS + " INTEGER NOT NULL, " +
            SUTUN_SEHIR + " TEXT NOT NULL);";

    public veritabani(Context context) {
        super(context, VERITABANI_ADI, null, SURUM);
        Log.d(TAG, "Veritabanı yardımcısı oluşturuldu.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "onCreate çağrıldı. OgrenciBilgi tablosu oluşturuluyor.");
            db.execSQL(TABLO_OLUSTUR_OGRENCI_BILGI);
            Log.d(TAG, "OgrenciBilgi tablosu başarıyla oluşturuldu.");
        } catch (Exception e) {
            Log.e(TAG, "Tablo oluşturulurken hata: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int eskiSurum, int yeniSurum) {
        Log.d(TAG, "onUpgrade çağrıldı. Eski sürüm: " + eskiSurum + ", Yeni sürüm: " + yeniSurum);
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_OGRENCI_BILGI);
        Log.d(TAG, TABLO_OGRENCI_BILGI + " tablosu silindi.");
        onCreate(db);
    }
}
