package com.example.ebookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent; // Ekle
import android.net.Uri; // Ekle
import android.os.Bundle;
import android.widget.TextView; // Ekle
import android.widget.Toast; // Ekle
import android.util.Log; // Loglama için eklenebilir (opsiyonel)

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity"; // Loglama için TAG (opsiyonel)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader); // activity_reader.xml layout'u kullanılır

        // Gelen Intent'i al
        Intent intent = getIntent();
        if (intent != null) {
            // Intent'ten "bookUri" anahtarıyla gönderilen String'i al
            String bookUriString = intent.getStringExtra("bookUri");
            String bookTitle = intent.getStringExtra("bookTitle"); // Başlığı da alalım

            if (bookUriString != null) {
                // String'i tekrar Uri nesnesine çevir (ileride dosyayı açmak için lazım olacak)
                Uri bookUri = Uri.parse(bookUriString);

                // Action bar başlığını ayarla
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(bookTitle != null ? bookTitle : "Kitap Okuyucu");
                    // Geri butonu eklemek istersen:
                    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    // getSupportActionBar().setDisplayShowHomeEnabled(true);
                }

                // activity_reader.xml'deki TextView'e URI'yi yazdıralım (test için)
                TextView infoTextView = findViewById(R.id.info_text_view); // Layout'taki ID ile eşleşmeli
                if (infoTextView != null) {
                    infoTextView.setText("Açılan Kitap URI:\n" + bookUri.toString());
                } else {
                    Log.e(TAG, "info_text_view bulunamadı!"); // Loglama (opsiyonel)
                }

                Log.d(TAG, "Kitap URI alındı: " + bookUri.toString()); // Loglama (opsiyonel)
                // Toast mesajı istersen kalabilir:
                // Toast.makeText(this, "Kitap URI alındı: " + bookUri.toString(), Toast.LENGTH_LONG).show();

                // TODO: Bu URI'yi kullanarak PDF/EPUB kütüphanesi ile kitabı yükle ve göster
                // loadBookContent(bookUri);

            } else {
                Log.e(TAG, "Intent'te 'bookUri' bulunamadı!"); // Loglama (opsiyonel)
                Toast.makeText(this, "Kitap URI bulunamadı!", Toast.LENGTH_SHORT).show();
                finish(); // URI yoksa activity'i kapat
            }
        } else {
            Log.e(TAG, "ReaderActivity'ye gelen Intent null!"); // Loglama (opsiyonel)
            Toast.makeText(this, "Kitap bilgisi alınamadı!", Toast.LENGTH_SHORT).show();
            finish(); // Intent yoksa activity'i kapat
        }
    }

    // Geri butonu işlevselliği (eğer action bar'a eklediysen)
    // @Override
    // public boolean onSupportNavigateUp() {
    //     onBackPressed(); // Veya finish();
    //     return true;
    // }

    // private void loadBookContent(Uri bookUri) {
    //     // PDF veya EPUB kütüphanesini kullanarak içeriği yükleme kodu buraya gelecek
    // }
}
