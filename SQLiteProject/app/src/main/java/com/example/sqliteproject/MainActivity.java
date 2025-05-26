package com.example.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText editTextAd, editTextSoyad, editTextYas, editTextSehir;
    Button buttonKayitEkle, buttonKayitGoster, buttonKayitSil, buttonKayitGuncelle;
    TextView textViewBilgiler;

    private veritabani vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate çağrıldı.");

        vt = new veritabani(this);
        Log.d(TAG, "Veritabanı yardımcısı başlatıldı.");

        editTextAd = findViewById(R.id.editTextAd);
        editTextSoyad = findViewById(R.id.editTextSoyad);
        editTextYas = findViewById(R.id.editTextYas);
        editTextSehir = findViewById(R.id.editTextSehir);

        buttonKayitEkle = findViewById(R.id.buttonKayitEkle);
        buttonKayitGoster = findViewById(R.id.buttonKayitGoster);
        buttonKayitSil = findViewById(R.id.buttonKayitSil);
        buttonKayitGuncelle = findViewById(R.id.buttonKayitGuncelle);

        textViewBilgiler = findViewById(R.id.textViewBilgiler);
        Log.d(TAG, "Arayüz elemanları bulundu.");

        buttonKayitEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Kayıt Ekle butonuna basıldı.");
                kayitEkle();
            }
        });

        buttonKayitGoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Kayıtları Göster butonuna basıldı.");
                kayitGoster();
            }
        });

        buttonKayitSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Kayıt Sil butonuna basıldı.");
                kayitSil();
            }
        });

        buttonKayitGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Kayıt Güncelle butonuna basıldı.");
                kayitGuncelle();
            }
        });
    }

    private void kayitEkle() {
        String ad = editTextAd.getText().toString().trim();
        String soyad = editTextSoyad.getText().toString().trim();
        String yasStr = editTextYas.getText().toString().trim();
        String sehir = editTextSehir.getText().toString().trim();

        if (ad.isEmpty() || soyad.isEmpty() || yasStr.isEmpty() || sehir.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Kayıt ekleme başarısız: Boş alanlar var.");
            return;
        }

        int yas;
        try {
            yas = Integer.parseInt(yasStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Yaş geçerli bir sayı olmalıdır!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Kayıt ekleme başarısız: Yaş formatı hatalı.", e);
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = vt.getWritableDatabase();
            ContentValues veriler = new ContentValues();
            veriler.put(veritabani.SUTUN_AD, ad);
            veriler.put(veritabani.SUTUN_SOYAD, soyad);
            veriler.put(veritabani.SUTUN_YAS, yas);
            veriler.put(veritabani.SUTUN_SEHIR, sehir);

            long result = db.insertOrThrow(veritabani.TABLO_OGRENCI_BILGI, null, veriler);
            if (result != -1) {
                Toast.makeText(this, "Kayıt başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Kayıt başarıyla eklendi. ID: " + result);
                editTextAd.setText("");
                editTextSoyad.setText("");
                editTextYas.setText("");
                editTextSehir.setText("");
            } else {
                Toast.makeText(this, "Kayıt eklenirken hata oluştu!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Kayıt eklenirken hata oluştu. Result: " + result);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Kayıt eklenirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Kayıt eklenirken istisna: ", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Veritabanı bağlantısı kapatıldı (kayitEkle).");
            }
        }
    }

    private void kayitGoster() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        StringBuilder builder = new StringBuilder();
        try {
            db = vt.getReadableDatabase();
            String[] sutunlar = {veritabani.SUTUN_AD, veritabani.SUTUN_SOYAD, veritabani.SUTUN_YAS, veritabani.SUTUN_SEHIR};
            cursor = db.query(veritabani.TABLO_OGRENCI_BILGI, sutunlar, null, null, null, null, veritabani.SUTUN_AD + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, cursor.getCount() + " kayıt bulundu.");
                do {
                    int adIndex = cursor.getColumnIndexOrThrow(veritabani.SUTUN_AD);
                    int soyadIndex = cursor.getColumnIndexOrThrow(veritabani.SUTUN_SOYAD);
                    int yasIndex = cursor.getColumnIndexOrThrow(veritabani.SUTUN_YAS);
                    int sehirIndex = cursor.getColumnIndexOrThrow(veritabani.SUTUN_SEHIR);

                    String ad = cursor.getString(adIndex);
                    String soyad = cursor.getString(soyadIndex);
                    int yas = cursor.getInt(yasIndex);
                    String sehir = cursor.getString(sehirIndex);

                    builder.append("Ad: ").append(ad).append("\n");
                    builder.append("Soyad: ").append(soyad).append("\n");
                    builder.append("Yaş: ").append(yas).append("\n");
                    builder.append("Şehir: ").append(sehir).append("\n");
                    builder.append("--------------------\n");
                } while (cursor.moveToNext());
                textViewBilgiler.setText(builder.toString());
            } else {
                textViewBilgiler.setText("Gösterilecek kayıt bulunamadı.");
                Log.d(TAG, "Gösterilecek kayıt bulunamadı.");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Kayıtlar gösterilirken hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Kayıtlar gösterilirken istisna: ", e);
            textViewBilgiler.setText("Kayıtlar yüklenemedi.");
        } finally {
            if (cursor != null) {
                cursor.close();
                Log.d(TAG, "Cursor kapatıldı (kayitGoster).");
            }
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Veritabanı bağlantısı kapatıldı (kayitGoster).");
            }
        }
    }

    private void kayitSil() {
        String adToDelete = editTextAd.getText().toString().trim();

        if (adToDelete.isEmpty()) {
            Toast.makeText(this, "Lütfen silinecek kaydın adını girin!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Kayıt silme başarısız: Silinecek ad boş.");
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = vt.getWritableDatabase();
            String whereClause = veritabani.SUTUN_AD + " = ?";
            String[] whereArgs = {adToDelete};

            int silinenSatirSayisi = db.delete(veritabani.TABLO_OGRENCI_BILGI, whereClause, whereArgs);

            if (silinenSatirSayisi > 0) {
                Toast.makeText(this, silinenSatirSayisi + " kayıt başarıyla silindi!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, silinenSatirSayisi + " kayıt silindi. Ad: " + adToDelete);
                kayitGoster();
                editTextAd.setText("");
            } else {
                Toast.makeText(this, "'" + adToDelete + "' adına sahip kayıt bulunamadı!", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "'" + adToDelete + "' adına sahip kayıt bulunamadı.");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Kayıt silinirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Kayıt silinirken istisna: ", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Veritabanı bağlantısı kapatıldı (kayitSil).");
            }
        }
    }

    private void kayitGuncelle() {
        String adToUpdate = editTextAd.getText().toString().trim();
        String yeniSoyad = editTextSoyad.getText().toString().trim();
        String yeniYasStr = editTextYas.getText().toString().trim();
        String yeniSehir = editTextSehir.getText().toString().trim();

        if (adToUpdate.isEmpty()) {
            Toast.makeText(this, "Lütfen güncellenecek kaydın adını girin!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Kayıt güncelleme başarısız: Güncellenecek ad boş.");
            return;
        }

        if (yeniSoyad.isEmpty() && yeniYasStr.isEmpty() && yeniSehir.isEmpty()) {
            Toast.makeText(this, "Lütfen güncellemek için en az bir yeni bilgi girin!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Kayıt güncelleme başarısız: Güncellenecek yeni bilgi yok.");
            return;
        }

        SQLiteDatabase db = null;
        ContentValues guncelVeriler = new ContentValues();

        if (!yeniSoyad.isEmpty()) {
            guncelVeriler.put(veritabani.SUTUN_SOYAD, yeniSoyad);
        }
        if (!yeniYasStr.isEmpty()) {
            try {
                int yeniYas = Integer.parseInt(yeniYasStr);
                guncelVeriler.put(veritabani.SUTUN_YAS, yeniYas);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Yaş geçerli bir sayı olmalıdır!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Kayıt güncelleme başarısız: Yaş formatı hatalı.", e);
                return;
            }
        }
        if (!yeniSehir.isEmpty()) {
            guncelVeriler.put(veritabani.SUTUN_SEHIR, yeniSehir);
        }

        if (guncelVeriler.size() == 0) { // Hiçbir alan güncellenmiyorsa işlem yapma
            Toast.makeText(this, "Güncellenecek yeni bilgi girilmedi.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            db = vt.getWritableDatabase();
            String whereClause = veritabani.SUTUN_AD + " = ?";
            String[] whereArgs = {adToUpdate};

            int guncellenenSatirSayisi = db.update(veritabani.TABLO_OGRENCI_BILGI, guncelVeriler, whereClause, whereArgs);

            if (guncellenenSatirSayisi > 0) {
                Toast.makeText(this, guncellenenSatirSayisi + " kayıt başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, guncellenenSatirSayisi + " kayıt güncellendi. Ad: " + adToUpdate);
                kayitGoster();
                editTextAd.setText("");
                editTextSoyad.setText("");
                editTextYas.setText("");
                editTextSehir.setText("");
            } else {
                Toast.makeText(this, "'" + adToUpdate + "' adına sahip kayıt bulunamadı veya güncellenecek bilgi yoktu!", Toast.LENGTH_LONG).show();
                Log.w(TAG, "'" + adToUpdate + "' adına sahip kayıt bulunamadı veya güncellenecek bilgi yoktu.");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Kayıt güncellenirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Kayıt güncellenirken istisna: ", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Veritabanı bağlantısı kapatıldı (kayitGuncelle).");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vt != null) {
            vt.close();
            Log.d(TAG, "onDestroy çağrıldı, veritabanı yardımcısı kapatıldı.");
        }
    }
}
