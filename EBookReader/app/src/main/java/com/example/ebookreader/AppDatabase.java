package com.example.ebookreader;

import android.content.Context; // Ekle

import androidx.room.Database; // Ekle
import androidx.room.Room; // Ekle
import androidx.room.RoomDatabase; // Ekle
import androidx.room.TypeConverters; // Ekle

// @Database anotasyonu ile veritabanını tanımlıyoruz
// entities: Bu veritabanında hangi tabloların (Entity sınıflarının) olacağını belirtir
// version: Veritabanı şema versiyonu. Şemayı değiştirirseniz arttırmanız gerekir.
// exportSchema = false: Şema bilgilerini dışa aktarmayı kapatır (şimdilik)
@Database(entities = {Book.class}, version = 1, exportSchema = false)
// TypeConverter'ımızı veritabanına tanıtıyoruz
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Veritabanımızın içereceği DAO'ları abstract metotlarla belirtiyoruz
    public abstract BookDao bookDao();

    // Singleton pattern: Tüm uygulama için tek bir veritabanı örneği olmasını sağlar
    private static volatile AppDatabase INSTANCE; // volatile: farklı thread'lerden erişimde tutarlılık sağlar

    // Veritabanı örneğini almak için statik metot
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) { // Eğer örnek henüz oluşturulmadıysa
            synchronized (AppDatabase.class) { // Aynı anda sadece bir thread'in girmesini sağla (thread-safe)
                if (INSTANCE == null) { // İçeride tekrar kontrol et (double-checked locking)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "ebook_reader_database") // Veritabanı adı
                            // .allowMainThreadQueries() // !! KESİNLİKLE KULLANMA !! Test dışında ana thread'de sorguya izin verir (kötü pratik)
                            .fallbackToDestructiveMigration() // Şimdilik versiyon yükseltmede eski veriyi silip yeniden oluşturur (geliştirme için kolay)
                            .build();
                }
            }
        }
        return INSTANCE; // Mevcut veya yeni oluşturulan örneği döndür
    }
}