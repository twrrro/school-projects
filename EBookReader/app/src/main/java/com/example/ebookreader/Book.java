package com.example.ebookreader;

import androidx.annotation.NonNull; // Ekle
import androidx.room.ColumnInfo; // Ekle
import androidx.room.Entity; // Ekle
import androidx.room.PrimaryKey; // Ekle
import androidx.room.TypeConverters; // Ekle (TypeConverter için)

// @Entity ile bu sınıfın bir veritabanı tablosu olduğunu belirtiyoruz
// tableName ile tablo adını belirliyoruz
@Entity(tableName = "books")
// Enum tipini veritabanına yazıp okuyabilmek için TypeConverter'ı belirtiyoruz
@TypeConverters({Converters.class}) // Bu sınıfı birazdan oluşturacağız
public class Book {

    // --- Alanlar (Fields) ---

    // @PrimaryKey ile bu alanın birincil anahtar (unique identifier) olduğunu belirtiyoruz
    // Kitabın URI'si (daha önce filePath dediğimiz) eşsiz olduğu için iyi bir anahtar
    @PrimaryKey
    @NonNull // Boş olamaz
    @ColumnInfo(name = "uri_path") // Sütun adını değiştirebiliriz (isteğe bağlı)
    private String filePath; // Artık URI string'ini tutuyor

    @ColumnInfo(name = "book_title") // Sütun adı (isteğe bağlı)
    private String title;

    @ColumnInfo(name = "book_author")
    private String author;

    @ColumnInfo(name = "cover_image_uri")
    private String coverImageUri;

    @ColumnInfo(name = "last_read_page")
    private int lastReadPage;

    @ColumnInfo(name = "total_pages")
    private int totalPages;

    // BookType enum'ı TypeConverter ile String olarak saklanacak
    @ColumnInfo(name = "book_type")
    private BookType type;

    // @ColumnInfo(name = "last_read_timestamp")
    // private long lastReadTimestamp;


    // --- Yapıcı Metot (Constructor) ---
    // Room'un nesneyi oluşturabilmesi için bazen boş constructor gerekebilir,
    // veya tüm alanları alan bir constructor kullanabiliriz.
    // Şimdilik mevcut constructor kalsın, Room genellikle bunu yönetebilir.
    public Book(@NonNull String filePath, BookType type) {
        this.filePath = filePath; // @NonNull olarak işaretlendi
        this.type = type;
        // Diğer alanların ilk değer atamaları burada veya setter ile yapılabilir
        this.title = extractTitleFromPath(filePath);
        this.author = "Bilinmiyor";
        this.coverImageUri = null;
        this.lastReadPage = 0;
        this.totalPages = 0;
    }

    // --- Yardımcı Metot (extractTitleFromPath - Değişiklik yok) ---
    private String extractTitleFromPath(String path) {
        // ... (önceki kod aynı) ...
        // Bu metot URI string'leri için güncellenmeli veya daha iyi bir başlık alma yöntemi bulunmalı
        // Şimdilik basit haliyle bırakalım veya null döndürüp displayName kullanalım.
        // Constructor'da değil de, displayName alındıktan sonra set etmek daha mantıklı olabilir.
        // ŞİMDİLİK BU METODU KULLANMAYALIM, CONSTRUCTOR'da title'ı null yapalım:
        // this.title = null; // VEYA "Başlık Bekleniyor";
        // Gerçek başlığı URI'den displayName alınca set edeceğiz.
        // ---- DÜZELTME ----
        // Room'un kullanması için tüm alanları alan bir constructor yapmak daha iyi olabilir.
        // VEYA getter/setter'ları kullanır. Şimdilik mevcut haliyle bırakıp test edelim.
        // Başlık çıkarma mantığını kaldırıp, null bırakalım:
        try {
            String filename = path.substring(path.lastIndexOf('/') + 1);
            if (filename.contains(".")) {
                // return filename.substring(0, filename.lastIndexOf('.')); // Başlık çıkarmayı kapatalım
                return null;
            } else {
                // return filename; // Başlık çıkarmayı kapatalım
                return null;
            }
        } catch (Exception e) {
            return null; // Başlık çıkarmayı kapatalım
        }
    }


    // --- Getter ve Setter Metotları ---
    // Room'un alanlara erişmesi için getter ve setter'lar GEREKLİDİR!
    // (Otomatik generate edilebilir)

    @NonNull
    public String getFilePath() { // NonNull olarak işaretleyelim
        return filePath;
    }

    public void setFilePath(@NonNull String filePath) { // NonNull olarak işaretleyelim
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ... Diğer tüm alanlar için getter ve setter'lar ...
    // getAuthor, setAuthor, getCoverImageUri, setCoverImageUri, ... getType, setType

    // ÖNEMLİ: Tüm private alanlar için public getter ve setter metodları olduğundan emin ol!
    // Android Studio'da sağ tık -> Generate -> Getter and Setter ile eksikleri tamamla.


    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCoverImageUri() { return coverImageUri; }
    public void setCoverImageUri(String coverImageUri) { this.coverImageUri = coverImageUri; }
    public int getLastReadPage() { return lastReadPage; }
    public void setLastReadPage(int lastReadPage) { this.lastReadPage = lastReadPage; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public BookType getType() { return type; }
    public void setType(BookType type) { this.type = type; }

}