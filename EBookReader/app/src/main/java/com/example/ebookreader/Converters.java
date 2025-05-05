package com.example.ebookreader;

import androidx.room.TypeConverter;

public class Converters {

    // BookType'ı String'e çevirir (Veritabanına yazarken kullanılır)
    @TypeConverter
    public static String fromBookType(BookType bookType) {
        return bookType == null ? null : bookType.name(); // Enum'ın adını (PDF veya EPUB) String olarak döndür
    }

    // String'i BookType'a çevirir (Veritabanından okurken kullanılır)
    @TypeConverter
    public static BookType toBookType(String name) {
        // String null ise veya geçerli bir enum adı değilse null döndür (veya varsayılan)
        return name == null ? null : BookType.valueOf(name);
    }
}