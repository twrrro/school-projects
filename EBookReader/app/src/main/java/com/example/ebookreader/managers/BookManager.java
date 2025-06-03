
package com.example.ebookreader.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.database.Cursor;

import com.example.ebookreader.models.Book;
import com.example.ebookreader.models.BookType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookManager {
    private static final String TAG = "BookManager";
    private static final String PREFS_NAME = "EBookReaderBookPrefs";
    private static final String KEY_BOOKS_JSON = "booksJsonList";

    private static BookManager instance;
    private Context context;
    private List<Book> bookList;
    private SharedPreferences sharedPreferences;

    private BookManager(Context context) {
        this.context = context.getApplicationContext(); // Application context kullanmak önemli
        this.bookList = new ArrayList<>();
        this.sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadBooks(); // Uygulama başladığında veya instance oluşturulduğunda kitapları yükle
    }

    // Singleton deseni için public static metot
    public static synchronized BookManager getInstance(Context context) {
        if (instance == null) {
            instance = new BookManager(context.getApplicationContext());
        }
        return instance;
    }

    public Book addBook(Uri fileUri) {
        if (fileUri == null) {
            Log.e(TAG, "Dosya Uri'si null olamaz.");
            return null;
        }

        // Kalıcı izin almayı dene (SAF ile seçilen dosyalar için)
        try {
            // İçerik URI'leri için kalıcı izinler genellikle ACTION_OPEN_DOCUMENT ile zaten verilir.
            // Ancak emin olmak için ve API 30+ için takePersistableUriPermission gerekebilir.
            // final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            // context.getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
            // Log.d(TAG, "Kalıcı okuma izni alındı (veya zaten vardı): " + fileUri);
        } catch (SecurityException e) {
            Log.e(TAG, "Kalıcı okuma izni alınamadı: " + fileUri, e);
            // Kullanıcıya bilgi verilebilir.
        }


        String fileName = getFileName(fileUri);
        if (fileName == null) {
            Log.e(TAG, "Dosya adı alınamadı: " + fileUri);
            fileName = "unknown_file_" + System.currentTimeMillis(); // Benzersiz bir fallback
        }

        BookType type = determineBookType(fileName, fileUri);

        if (type == BookType.UNKNOWN) {
            Log.w(TAG, "Kitap türü belirlenemedi veya desteklenmiyor: " + fileName);
            return null;
        }

        String title = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String author = "Bilinmeyen Yazar"; // Gerçek uygulamada metadata'dan okunmalı

        // Kitap zaten listede var mı diye kontrol et
        for (Book existingBook : bookList) {
            if (existingBook.getFileUri() != null && existingBook.getFileUri().equals(fileUri)) {
                Log.i(TAG, "Kitap zaten mevcut: " + title);
                // İlerleme bilgilerini güncellemek için mevcut kitabı döndür ve son erişim zamanını güncelle
                existingBook.setLastReadTimestamp(System.currentTimeMillis());
                saveBooks();
                return existingBook;
            }
        }

        Book newBook = new Book(title, author, fileUri, type);
        // PDF ise sayfa sayısını almaya çalış (PdfReader bunu yapacak, burada gerek yok)
        bookList.add(newBook);
        sortBooksByTimestamp(); // Ekleme sonrası sırala
        saveBooks();
        Log.i(TAG, "Yeni kitap eklendi: " + newBook.getTitle());
        return newBook;
    }

    public List<Book> getAllBooks() {
        // Her zaman en son okunana göre sıralı listeyi döndür
        sortBooksByTimestamp();
        return new ArrayList<>(bookList); // Değiştirilemez bir kopya döndürmek daha güvenli
    }

    public List<Book> getRecentBooks(int limit) {
        sortBooksByTimestamp();
        return bookList.size() > limit ? new ArrayList<>(bookList.subList(0, limit)) : new ArrayList<>(bookList);
    }

    public void updateBookProgress(Book bookToUpdate, int currentPage, int totalPagesBook) {
        if (bookToUpdate == null || bookToUpdate.getFileUri() == null) return;

        boolean found = false;
        for (int i = 0; i < bookList.size(); i++) {
            Book bookInList = bookList.get(i);
            if (bookInList.getFileUri() != null && bookInList.getFileUri().equals(bookToUpdate.getFileUri())) {
                bookInList.setLastReadPage(currentPage);
                if (totalPagesBook > 0 && bookInList.getTotalPages() == 0) { // Sadece ilk kez veya 0 ise güncelle
                    bookInList.setTotalPages(totalPagesBook);
                }
                bookInList.setLastReadTimestamp(System.currentTimeMillis());
                found = true;
                Log.i(TAG, bookInList.getTitle() + " için ilerleme kaydedildi: Sayfa " + currentPage + ", Toplam: " + bookInList.getTotalPages());
                break;
            }
        }
        if (found) {
            sortBooksByTimestamp(); // İlerleme güncellendikten sonra sırala
            saveBooks();
        } else {
            Log.w(TAG, "İlerleme kaydedilecek kitap listede bulunamadı: " + bookToUpdate.getTitle());
        }
    }


    public void deleteBook(Book bookToDelete) {
        if (bookToDelete == null || bookToDelete.getFileUri() == null) return;
        boolean removed = bookList.removeIf(book -> book.getFileUri().equals(bookToDelete.getFileUri()));
        if (removed) {
            saveBooks();
            Log.i(TAG, "Kitap silindi: " + bookToDelete.getTitle());
        } else {
            Log.w(TAG, "Silinecek kitap bulunamadı: " + bookToDelete.getTitle());
        }
    }

    private void saveBooks() {
        JSONArray jsonArray = new JSONArray();
        for (Book book : bookList) {
            try {
                JSONObject bookJson = new JSONObject();
                bookJson.put("title", book.getTitle());
                bookJson.put("author", book.getAuthor());
                bookJson.put("fileUri", book.getFileUri().toString());
                bookJson.put("type", book.getType().name());
                bookJson.put("lastReadPage", book.getLastReadPage());
                bookJson.put("totalPages", book.getTotalPages());
                bookJson.put("lastReadTimestamp", book.getLastReadTimestamp());
                if (book.getCoverImage() != null) {
                    bookJson.put("coverImage", book.getCoverImage());
                }
                jsonArray.put(bookJson);
            } catch (JSONException e) {
                Log.e(TAG, "Kitap JSON'a dönüştürülürken hata", e);
            }
        }
        sharedPreferences.edit().putString(KEY_BOOKS_JSON, jsonArray.toString()).apply();
        Log.d(TAG, bookList.size() + " kitap SharedPreferences'a kaydedildi.");
    }

    private void loadBooks() {
        String json = sharedPreferences.getString(KEY_BOOKS_JSON, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                bookList.clear(); // Önceki listeyi temizle
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bookJson = jsonArray.getJSONObject(i);
                    String title = bookJson.getString("title");
                    String author = bookJson.getString("author");
                    Uri fileUri = Uri.parse(bookJson.getString("fileUri"));
                    BookType type = BookType.valueOf(bookJson.getString("type"));

                    Book book = new Book(title, author, fileUri, type);
                    book.setLastReadPage(bookJson.getInt("lastReadPage"));
                    book.setTotalPages(bookJson.getInt("totalPages"));
                    book.setLastReadTimestamp(bookJson.getLong("lastReadTimestamp"));
                    if (bookJson.has("coverImage")) {
                        book.setCoverImage(bookJson.getString("coverImage"));
                    }
                    bookList.add(book);
                }
                sortBooksByTimestamp(); // Yükleme sonrası sırala
                Log.d(TAG, bookList.size() + " kitap SharedPreferences'tan yüklendi.");
            } catch (JSONException e) {
                Log.e(TAG, "Kitaplar JSON'dan yüklenirken hata", e);
                bookList.clear(); // Hata durumunda listeyi temizle
            }
        } else {
            Log.d(TAG, "Kaydedilmiş kitap bulunamadı.");
        }
    }

    private void sortBooksByTimestamp() {
        // En son okunana göre (azalan sırada) sırala
        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return Long.compare(b2.getLastReadTimestamp(), b1.getLastReadTimestamp());
            }
        });
    }

    private BookType determineBookType(String fileName, Uri uri) {
        if (fileName != null) {
            String lowerCaseFileName = fileName.toLowerCase();
            if (lowerCaseFileName.endsWith(".pdf")) return BookType.PDF;
            if (lowerCaseFileName.endsWith(".epub")) return BookType.EPUB;
        }
        // Dosya adından tür belirlenemezse MIME türünü kontrol et
        if (context != null && uri != null) {
            try {
                String mimeType = context.getContentResolver().getType(uri);
                if (mimeType != null) {
                    if (mimeType.equals("application/pdf")) return BookType.PDF;
                    if (mimeType.equals("application/epub+zip")) return BookType.EPUB;
                }
            } catch (Exception e) {
                Log.w(TAG, "MIME type alınırken hata: " + uri, e);
            }
        }
        return BookType.UNKNOWN;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri == null || context == null) return null;

        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "İçerik Uri'sinden dosya adı alınamadı: " + uri.toString(), e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

    public Book getBookByUri(Uri fileUri) {
        if (fileUri == null) return null;
        for (Book book : bookList) {
            if (book.getFileUri() != null && book.getFileUri().equals(fileUri)) {
                return book;
            }
        }
        // Eğer kitap listede yoksa ve URI geçerliyse, geçici bir tane oluşturup döndür (ReaderActivity'de handle edilecek)
        // Bu, doğrudan bir dosya yöneticisinden açılan kitaplar için bir fallback olabilir.
        // Ancak bu durumda ilerleme kaydedilemez.
        Log.w(TAG, "getBookByUri: Kitap listede bulunamadı, geçici oluşturulabilir: " + fileUri);
        return null; // Veya yeni bir Book nesnesi oluşturup döndür
    }
}