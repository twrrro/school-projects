package com.example.ebookreader;

// Gerekli importlar
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns; // Dosya adı için
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Eklendi
import androidx.recyclerview.widget.RecyclerView; // Eklendi

import com.google.android.material.floatingactionbutton.FloatingActionButton; // Eklendi

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService; // Eklendi
import java.util.concurrent.Executors; // Eklendi

public class BooksFragment extends Fragment implements BookAdapter.OnBookItemClickListener {

    private final String TAG = "BooksFragment"; // Loglama için TAG

    // --- Sınıf Değişkenleri ---
    private List<Book> bookList = new ArrayList<>(); // Kitapları UI'da tutacak liste
    private RecyclerView recyclerView;               // Listeyi gösterecek RecyclerView
    private BookAdapter bookAdapter;                 // RecyclerView ve Liste arasındaki köprü
    private FloatingActionButton fabAddBook;         // Kitap ekle butonu

    // Dosya seçiciyi başlatmak ve sonucunu almak için Launcher
    private ActivityResultLauncher<Intent> selectBookLauncher;

    // --- Veritabanı ve Arka Plan İşlemleri İçin ---
    private BookDao bookDao; // Veritabanı Erişim Nesnesi

    private ExecutorService databaseWriteExecutor; // Arka plan işlemleri için Executor

    // --- onCreate ---
    // Fragment oluşturulduğunda çağrılır (View oluşturulmadan önce)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Veritabanı DAO'sunu ve Arka Plan Executor'ını initialize et
        // ApplicationContext kullanmak sızıntıları önler
        AppDatabase db = AppDatabase.getDatabase(requireActivity().getApplicationContext());
        bookDao = db.bookDao();
        // Tek thread kullanan bir executor oluşturuyoruz (basitlik için)
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        // Dosya Seçici sonucunu işlemek için launcher'ı register et (kaydet)
        selectBookLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { // Lambda: Sonuç geldiğinde çalışacak kod
                    // Sonuç başarılı mı ve veri var mı kontrolü
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData(); // Seçilen dosyanın URI'sini al
                        if (selectedFileUri != null) {
                            Log.d(TAG, "Dosya seçildi URI: " + selectedFileUri.toString());
                            // Kalıcı okuma izni almayı dene (Uygulama yeniden başlasa da erişim için)
                            try {
                                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                // ÖNEMLİ: Kalıcı izin alma işlemi sadece ACTION_OPEN_DOCUMENT ile alınan
                                // URI'ler için gereklidir ve her zaman başarılı olmayabilir.
                                requireContext().getContentResolver().takePersistableUriPermission(selectedFileUri, takeFlags);
                                Log.d(TAG, "Kalıcı okuma izni alındı (veya zaten vardı) URI: " + selectedFileUri);

                                // Seçilen dosyadan Book nesnesi oluştur ve veritabanına ekle
                                addBookFromUri(selectedFileUri);

                            } catch (SecurityException e) {
                                // İzin alınamazsa (bazı dosya sağlayıcıları desteklemeyebilir)
                                Log.e(TAG, "Kalıcı izin alınamadı URI: " + selectedFileUri, e);
                                // İzin alınamasa bile URI hala geçerli olabilir, yine de eklemeyi deneyelim.
                                // Ama uzun süreli erişim garanti olmayabilir.
                                addBookFromUri(selectedFileUri); // Yine de eklemeyi dene
                                if(getContext() != null) Toast.makeText(getContext(), "Dosya erişim izni alınamadı, okuma sorunları olabilir.", Toast.LENGTH_LONG).show();
                            } catch (Exception e){ // Diğer hatalar
                                Log.e(TAG, "Dosya URI işlenirken hata: " + selectedFileUri, e);
                                if(getContext() != null) Toast.makeText(getContext(), "Dosya işlenirken hata.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // Kullanıcı dosya seçmedi veya geri tuşuna bastı
                        Log.d(TAG, "Dosya seçilmedi veya işlem iptal edildi.");
                    }
                });
    }

    // --- onCreateView ---
    // Fragment'ın arayüzü (layout) oluşturulduğunda çağrılır
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_books.xml layout'unu inflate et
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        // Layout içindeki elemanları ID'leri ile bul
        fabAddBook = view.findViewById(R.id.fab_add_book);
        recyclerView = view.findViewById(R.id.books_recycler_view);

        // RecyclerView'ı kur (Adapter ve LayoutManager ayarla)
        setupRecyclerView();

        return view; // Oluşturulan View nesnesini döndür
    }

    // --- onViewCreated ---
    // onCreateView bittikten hemen sonra, View'lar hazır olduğunda çağrılır
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kitap Ekle butonuna tıklama olayını dinle
        fabAddBook.setOnClickListener(v -> {
            openFilePicker(); // Dosya seçiciyi açan metodu çağır
        });

        // Fragment görünümü oluşturulduğunda veritabanından kitapları yükle
        loadBooksFromDb();
    }

    // --- Dosya Seçiciyi Açan Metot ---
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Genel tip
        String[] mimeTypes = {"application/pdf", "application/epub+zip"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes); // Spesifik türler

        try {
            selectBookLauncher.launch(intent);
            Log.d(TAG, "Dosya seçici başlatıldı.");
        } catch (Exception e) {
            Log.e(TAG, "Dosya seçici başlatılamadı!", e);
            if(getContext() != null) {
                Toast.makeText(getContext(), "Dosya seçici açılamadı.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- Seçilen URI'den Kitap Ekleyen Metot ---
    private void addBookFromUri(Uri uri) {
        // URI'den dosya adını ve MIME türünü al
        String displayName = getFileNameFromUri(uri);
        String mimeType = getMimeTypeFromUri(uri);
        Log.d(TAG, "addBookFromUri: Name=" + displayName + ", Mime=" + mimeType + ", Uri=" + uri.toString());

        // BookType belirle
        BookType bookType = null;
        if (mimeType != null) {
            if (mimeType.equals("application/pdf")) { bookType = BookType.PDF; }
            else if (mimeType.equals("application/epub+zip")) { bookType = BookType.EPUB; }
        }
        if (bookType == null && displayName != null) { // MIME yoksa isme bak
            if (displayName.toLowerCase().endsWith(".pdf")) { bookType = BookType.PDF; }
            else if (displayName.toLowerCase().endsWith(".epub")) { bookType = BookType.EPUB;}
        }

        // Geçerli bir tür bulunduysa devam et
        if (bookType != null) {
            // Book nesnesini oluştur (filePath olarak URI'nin String halini kullan)
            // ÖNEMLİ: URI'yi birincil anahtar olarak kullandığımız için @NonNull olmalı.
            // Eğer uri.toString() null dönerse (pek olası değil ama) hata alabiliriz.
            String uriString = uri.toString();
            Book book = new Book(uriString, bookType);

            // Başlığı displayName'den (uzantısız) al
            if (displayName != null && displayName.contains(".")) {
                book.setTitle(displayName.substring(0, displayName.lastIndexOf('.')));
            } else if (displayName != null) {
                book.setTitle(displayName);
            } else {
                Log.w(TAG,"Display name null geldi, varsayılan başlık kullanılacak.");
                // Book constructor zaten bir başlık atamış olabilir (URI'den), kontrol et.
                if (book.getTitle() == null || book.getTitle().isEmpty() || book.getTitle().equals("Başlıksız Kitap")){
                    book.setTitle("Bilinmeyen Kitap"); // Son çare
                }
            }
            Log.d(TAG, "Veritabanına eklenecek Kitap: " + book.getTitle() + ", Tür: " + book.getType() + ", URI: " + book.getFilePath());

            // Veritabanına ekleme işlemini arka plan thread'inde yap
            databaseWriteExecutor.execute(() -> {
                Log.d(TAG,"Veritabanına ekleniyor (arka plan): " + book.getTitle());
                // INSERT işlemi (IGNORE stratejisi ile aynı URI tekrar eklenmeyecek)
                bookDao.insert(book);
                Log.d(TAG,"Veritabanı insert işlemi tamamlandı (arka plan).");

                // Ekleme sonrası listeyi UI thread'inde yeniden yükle
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(TAG,"Veritabanına ekleme sonrası UI yenileme tetikleniyor.");
                        loadBooksFromDb(); // Listeyi DB'den güncelleyelim.
                    });
                }
            });

            // Kullanıcıya ekleme bilgisini hemen verelim
            if(getContext() != null) Toast.makeText(getContext(), "'" + book.getTitle() + "' ekleniyor...", Toast.LENGTH_SHORT).show();

        } else {
            Log.w(TAG, "Desteklenmeyen dosya türü veya bilgi alınamadı: " + displayName);
            if(getContext() != null) Toast.makeText(getContext(), "Desteklenmeyen dosya türü.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Veritabanından Kitapları Yükleyen Metot ---
    private void loadBooksFromDb() {
        Log.d(TAG,"Veritabanından kitaplar yükleniyor (arka plan tetikleniyor)...");
        // Veritabanı okuma işlemini arka plan thread'inde yap
        databaseWriteExecutor.execute(() -> {
            // DAO üzerinden tüm kitapları çek
            List<Book> loadedBooks = bookDao.getAllBooks();
            Log.d(TAG,"Veritabanından " + loadedBooks.size() + " kitap çekildi (arka plan).");

            // Sonucu UI thread'inde işle (Adapter'ı güncellemek için)
            if (getActivity() != null) { // Fragment hala Activity'e bağlı mı kontrolü
                getActivity().runOnUiThread(() -> {
                    Log.d(TAG,"Kitap listesi UI thread'inde güncelleniyor.");
                    // bookList'i direkt değiştirmek yerine adapter'a yeni listeyi vermek daha iyi olabilir
                    // Ama şimdilik bu yöntemle devam edelim:
                    bookList.clear(); // Mevcut hafızadaki listeyi temizle
                    bookList.addAll(loadedBooks); // Veritabanından gelenleri ekle

                    // Adapter null değilse verinin değiştiğini bildir
                    if (bookAdapter != null) {
                        bookAdapter.notifyDataSetChanged(); // Tüm listenin değiştiğini bildir
                        Log.d(TAG,"Adapter güncellendi. Gösterilen kitap sayısı: " + bookAdapter.getItemCount());
                    } else {
                        Log.w(TAG,"Adapter null, RecyclerView güncellenemedi.");
                    }
                    // Kullanıcıya bilgi ver (liste boşsa)
                    if (bookList.isEmpty() && getContext() != null) {
                        // Toast.makeText(getContext(), "Kaydedilmiş kitap bulunamadı.", Toast.LENGTH_SHORT).show();
                        // Veya ekranda bir mesaj gösterilebilir.
                    }
                });
            } else {
                Log.w(TAG,"Activity null, UI güncellenemedi (arka plan).");
            }
        });
    }


    // --- Yardımcı Metot: URI'den Dosya Adı Alma ---
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            // ContentResolver kullanarak URI'yi sorgula
            cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // DISPLAY_NAME sütununun indeksini bul
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                // Eğer sütun varsa ve geçerliyse, dosya adını al
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "URI'den dosya adı alınırken hata.", e);
            fileName = null;
        } finally {
            // Cursor'ı kapatmayı unutma!
            if (cursor != null) {
                cursor.close();
            }
        }
        // ContentResolver ile alınamazsa, URI'nin son bölümünü almayı dene (daha az güvenilir)
        if (fileName == null) {
            fileName = uri.getPath();
            if (fileName != null) {
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }
        }
        return fileName;
    }

    // --- Yardımcı Metot: URI'den MIME Türü Alma ---
    private String getMimeTypeFromUri(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        // ContentResolver.getType() metodu URI'nin MIME türünü verir
        return cR.getType(uri);
    }

    // --- RecyclerView Kurulum Metodu ---
    private void setupRecyclerView() {
        // Adapter zaten varsa tekrar oluşturma
        if (bookAdapter == null) {
            bookAdapter = new BookAdapter(bookList); // Adapter'ı oluştur
            Log.d(TAG,"BookAdapter oluşturuldu.");
        }
        // Adapter'ı RecyclerView'a bağla
        recyclerView.setAdapter(bookAdapter);
        // Layout Manager'ı ayarla (dikey liste için)
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Log.d(TAG, "RecyclerView kurulumu tamamlandı.");
    }


    @Override
    public void onBookClick(Book book) {
        Log.d(TAG, "Kitaba tıklandı: " + book.getTitle() + " | URI: " + book.getFilePath());
        Toast.makeText(getContext(), book.getTitle() + " açılıyor...", Toast.LENGTH_SHORT).show();

        // ReaderActivity'yi başlatmak için Intent oluştur
        Intent intent = new Intent(getActivity(), ReaderActivity.class);

        // Açılacak kitabın URI'sini (filePath alanında saklıyoruz) Intent'e ekstra olarak ekle
        intent.putExtra("bookUri", book.getFilePath()); // Anahtar: "bookUri", Değer: kitabın URI string'i
        intent.putExtra("bookTitle", book.getTitle()); // Başlığı da gönderelim (opsiyonel)

        // Yeni Activity'yi başlat
        startActivity(intent);
    }

}