package com.example.ebookreader;

import androidx.room.Dao; // Ekle
import androidx.room.Delete; // Ekle
import androidx.room.Insert; // Ekle
import androidx.room.OnConflictStrategy; // Ekle
import androidx.room.Query; // Ekle
import androidx.room.Update; // Ekle

import java.util.List; // Ekle

@Dao // Bu arayüzün bir Data Access Object olduğunu belirtir
public interface BookDao {

    // Kitap ekleme metodu
    // OnConflict = OnConflictStrategy.IGNORE: Eğer aynı Primary Key (bizim için URI)
    // ile bir kitap zaten varsa, yeni eklemeyi görmezden gel (hata verme).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Book book); // Arka planda çalıştırılmalı!

    // Tüm kitapları başlığa göre sıralı getirme metodu
    @Query("SELECT * FROM books ORDER BY book_title ASC")
    List<Book> getAllBooks(); // Arka planda çalıştırılmalı!

    // Belirli bir kitabı URI'sine göre getirme (Opsiyonel)
    @Query("SELECT * FROM books WHERE uri_path = :uri LIMIT 1")
    Book findBookByUri(String uri); // Arka planda çalıştırılmalı!

    // Kitap güncelleme metodu (örn: son okunan sayfayı kaydetmek için)
    @Update
    void update(Book book); // Arka planda çalıştırılmalı!

    // Kitap silme metodu
    @Delete
    void delete(Book book); // Arka planda çalıştırılmalı!

    // Tüm kitapları silme metodu (Opsiyonel)
    @Query("DELETE FROM books")
    void deleteAllBooks(); // Arka planda çalıştırılmalı!

}