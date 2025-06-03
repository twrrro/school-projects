
package com.example.ebookreader.activities;

import android.content.Intent; // Eklendi
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ebookreader.R;
import com.example.ebookreader.managers.BookManager;
import com.example.ebookreader.models.Book;
import com.example.ebookreader.models.BookType;
import com.example.ebookreader.readers.EpubReader;
import com.example.ebookreader.readers.PdfReader;

import java.io.FileNotFoundException; // Eklendi
import java.io.IOException;
import java.io.InputStream; // Eklendi

public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";
    public static final String EXTRA_BOOK_URI = "extra_book_uri";

    private Book currentBook;
    private Uri bookUri;
    private BookManager bookManager;

    private PdfReader pdfReader;
    private EpubReader epubReader;

    private ImageView pdfPageView;
    private WebView epubWebView;
    private TextView textViewPageInfo;
    private Button buttonNextPage, buttonPrevPage;
    private ProgressBar progressBarLoading;
    private SeekBar seekBarPage;

    private int currentPage = 0;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate başladı.");

        try {
            setContentView(R.layout.activity_reader);

            Toolbar toolbarReader = findViewById(R.id.toolbar_reader);
            setSupportActionBar(toolbarReader);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            bookManager = BookManager.getInstance(this.getApplicationContext());

            pdfPageView = findViewById(R.id.pageViewReader);
            epubWebView = findViewById(R.id.webViewReader);
            textViewPageInfo = findViewById(R.id.textViewPageInfo);
            buttonNextPage = findViewById(R.id.buttonNextPage);
            buttonPrevPage = findViewById(R.id.buttonPrevPage);
            progressBarLoading = findViewById(R.id.progressBarLoading);
            seekBarPage = findViewById(R.id.seekBarPage);

            if (pdfPageView == null || epubWebView == null || textViewPageInfo == null || buttonNextPage == null ||
                    buttonPrevPage == null || progressBarLoading == null || seekBarPage == null) {
                Log.e(TAG, "Bir veya daha fazla UI elemanı null! Layout dosyasını kontrol edin.");
                Toast.makeText(this, "Okuyucu arayüzü yüklenemedi.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            WebSettings webSettings = epubWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            epubWebView.setWebViewClient(new WebViewClient());

            String bookUriString = getIntent().getStringExtra(EXTRA_BOOK_URI);
            if (bookUriString == null) {
                Toast.makeText(this, "Kitap yolu bulunamadı.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "EXTRA_BOOK_URI bulunamadı.");
                finish();
                return;
            }

            try {
                bookUri = Uri.parse(bookUriString);
            } catch (Exception e) {
                Toast.makeText(this, "Geçersiz kitap yolu.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Book URI parse edilemedi: " + bookUriString, e);
                finish();
                return;
            }

            // URI erişimini kontrol et
            if (!canAccessUri(bookUri)) {
                Toast.makeText(this, "Kitap dosyasına erişim izni yok veya dosya bulunamadı. Lütfen dosyayı tekrar seçin.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Kitap URI'sine erişilemiyor: " + bookUri);
                // Belki kullanıcıyı dosyayı yeniden seçmeye yönlendirebilir veya kitabı listeden kaldırabilirsiniz.
                // bookManager.deleteBookWithUri(bookUri); // Böyle bir metot eklenebilir
                finish();
                return;
            }

            currentBook = bookManager.getBookByUri(bookUri);
            if (currentBook == null) {
                Log.w(TAG, "Kitap BookManager'da bulunamadı, URI'den geçici nesne oluşturuluyor: " + bookUri);
                String tempTitle = "Bilinmeyen Kitap";
                if (bookUri != null && bookUri.getLastPathSegment() != null) {
                    tempTitle = bookUri.getLastPathSegment();
                }
                BookType tempType = determineBookTypeFromUri(bookUri);
                if (tempType == BookType.UNKNOWN) {
                    Toast.makeText(this, "Desteklenmeyen veya belirlenemeyen kitap formatı.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                currentBook = new Book(tempTitle, "Bilinmeyen Yazar", bookUri, tempType);
            }

            if (getSupportActionBar() != null && currentBook != null) {
                getSupportActionBar().setTitle(currentBook.getTitle());
            }

            if (currentBook != null) {
                loadBookContent();
            } else {
                Toast.makeText(this, "Kitap yüklenemedi (currentBook null).", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            buttonNextPage.setOnClickListener(v -> {
                if (totalPages > 0 && currentPage < totalPages - 1) {
                    currentPage++;
                    displayPage(currentPage);
                }
            });

            buttonPrevPage.setOnClickListener(v -> {
                if (currentPage > 0) {
                    currentPage--;
                    displayPage(currentPage);
                }
            });

            if (seekBarPage != null) {
                seekBarPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (totalPages > 0) {
                            currentPage = seekBar.getProgress();
                            displayPage(currentPage);
                        }
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "onCreate içinde beklenmedik bir hata oluştu!", e);
            Toast.makeText(this, "Okuyucu başlatılırken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean canAccessUri(Uri uri) {
        if (uri == null) return false;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                inputStream.close();
                return true;
            }
        } catch (FileNotFoundException | SecurityException e) {
            Log.w(TAG, "canAccessUri: URI'ye erişilemedi - " + uri.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, "canAccessUri: InputStream kapatılırken hata - " + uri.toString(), e);
        }
        return false;
    }


    private BookType determineBookTypeFromUri(Uri uri) {
        // ... (öncekiyle aynı)
        if (uri == null) return BookType.UNKNOWN;
        String path = uri.getPath();
        if (path != null) {
            String lowerPath = path.toLowerCase();
            if (lowerPath.endsWith(".pdf")) return BookType.PDF;
            if (lowerPath.endsWith(".epub")) return BookType.EPUB;
        }
        try {
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null) {
                if (mimeType.equals("application/pdf")) return BookType.PDF;
                if (mimeType.equals("application/epub+zip")) return BookType.EPUB;
            }
        } catch (Exception e) {
            Log.w(TAG, "MIME type alınırken hata: " + uri, e);
        }
        return BookType.UNKNOWN;
    }

    private void loadBookContent() {
        // ... (öncekiyle aynı)
        if (currentBook == null || bookUri == null) {
            Toast.makeText(this, "Kitap veya URI null, yüklenemiyor.", Toast.LENGTH_SHORT).show();
            if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
            updateNavigationUi();
            return;
        }

        if (progressBarLoading != null) progressBarLoading.setVisibility(View.VISIBLE);
        if (pdfPageView != null) pdfPageView.setVisibility(View.GONE);
        if (epubWebView != null) epubWebView.setVisibility(View.GONE);

        BookType type = currentBook.getType();
        if (type == BookType.UNKNOWN) {
            type = determineBookTypeFromUri(bookUri);
            currentBook.setType(type);
        }

        Log.d(TAG, "Kitap türü: " + type);
        totalPages = 0;
        currentPage = 0;

        if (type == BookType.PDF) {
            if (pdfPageView == null) { Log.e(TAG, "pdfPageView null!"); if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE); updateNavigationUi(); return; }
            pdfPageView.setVisibility(View.VISIBLE);
            pdfReader = new PdfReader(this);
            if (pdfReader.loadDocument(bookUri)) {
                totalPages = pdfReader.getPageCount();
                if (currentBook.getTotalPages() == 0 || currentBook.getTotalPages() != totalPages) {
                    currentBook.setTotalPages(totalPages);
                }
                int lastRead = currentBook.getLastReadPage();
                currentPage = Math.max(0, Math.min(lastRead, totalPages > 0 ? totalPages - 1 : 0));

                if (totalPages > 0) {
                    displayPage(currentPage);
                } else {
                    Log.w(TAG, "PDF yüklendi ancak sayfa sayısı 0.");
                    Toast.makeText(this, "PDF boş veya okunamadı.", Toast.LENGTH_LONG).show();
                    if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "PDF yüklenemedi.", Toast.LENGTH_LONG).show();
                if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
            }
        } else if (type == BookType.EPUB) {
            if (epubWebView == null) { Log.e(TAG, "epubWebView null!"); if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE); updateNavigationUi(); return; }
            epubWebView.setVisibility(View.VISIBLE);
            epubReader = new EpubReader(this);
            if (epubReader.loadDocument(bookUri)) {
                totalPages = epubReader.getPageCount();
                if (currentBook.getTotalPages() == 0 || currentBook.getTotalPages() != totalPages) {
                    currentBook.setTotalPages(totalPages);
                }
                int lastRead = currentBook.getLastReadPage();
                currentPage = Math.max(0, Math.min(lastRead, totalPages > 0 ? totalPages - 1 : 0));

                if (totalPages > 0) {
                    displayPage(currentPage);
                } else {
                    Log.w(TAG, "EPUB yüklendi ancak bölüm sayısı 0.");
                    Toast.makeText(this, "EPUB boş veya bölüm bulunamadı.", Toast.LENGTH_LONG).show();
                    if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "EPUB yüklenemedi veya bölüm bulunamadı.", Toast.LENGTH_LONG).show();
                if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Desteklenmeyen kitap formatı: " + type, Toast.LENGTH_LONG).show();
            if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
        }
        updateNavigationUi();
    }

    private void displayPage(int pageOrSectionIndex) {
        // ... (öncekiyle aynı)
        if (currentBook == null) {
            if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
            return;
        }
        this.currentPage = Math.max(0, Math.min(pageOrSectionIndex, totalPages > 0 ? totalPages - 1 : 0));

        Log.d(TAG, "displayPage: " + this.currentPage + ", Tür: " + currentBook.getType() + ", Toplam: " + totalPages);

        if (progressBarLoading != null) progressBarLoading.setVisibility(View.VISIBLE);

        if (currentBook.getType() == BookType.PDF && pdfReader != null) {
            if (pdfPageView == null) { Log.e(TAG, "displayPage (PDF): pdfPageView null"); if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE); updateNavigationUi(); return; }
            Bitmap pageBitmap = pdfReader.renderPage(this.currentPage);
            if (pageBitmap != null) {
                pdfPageView.setImageBitmap(pageBitmap);
            } else {
                pdfPageView.setImageResource(android.R.color.darker_gray);
                Toast.makeText(this, (currentBook.getType() == BookType.PDF ? "Sayfa " : "Bölüm ") + (this.currentPage + 1) + " yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
        } else if (currentBook.getType() == BookType.EPUB && epubReader != null) {
            if (epubWebView == null) { Log.e(TAG, "displayPage (EPUB): epubWebView null"); if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE); updateNavigationUi(); return; }
            String htmlContent = epubReader.getPageContent(this.currentPage);
            if (htmlContent != null) {
                String baseUrl = epubReader.getBaseHrefPathForWebView();
                if (baseUrl == null && currentBook.getFileUri() != null && "file".equals(currentBook.getFileUri().getScheme())) {
                    baseUrl = currentBook.getFileUri().toString().substring(0, currentBook.getFileUri().toString().lastIndexOf('/') + 1);
                } else if (baseUrl == null) {
                    baseUrl = "file:///android_asset/";
                }
                epubWebView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null);
            } else {
                epubWebView.loadData("İçerik yüklenemedi.", "text/html", "UTF-8");
                Toast.makeText(this, (currentBook.getType() == BookType.PDF ? "Sayfa " : "Bölüm ") + (this.currentPage + 1) + " içeriği boş.", Toast.LENGTH_SHORT).show();
            }
        }

        if (progressBarLoading != null) progressBarLoading.setVisibility(View.GONE);
        updateNavigationUi();

        if (bookManager != null && currentBook != null && bookManager.getBookByUri(currentBook.getFileUri()) != null && totalPages > 0) {
            bookManager.updateBookProgress(currentBook, this.currentPage, this.totalPages);
        }
    }

    private void updateNavigationUi() {
        // ... (öncekiyle aynı)
        if (textViewPageInfo == null || seekBarPage == null || buttonPrevPage == null || buttonNextPage == null || currentBook == null) {
            Log.w(TAG, "updateNavigationUi: Bir veya daha fazla UI elemanı veya currentBook null.");
            return;
        }

        String pageLabel = (currentBook.getType() == BookType.EPUB) ? "Bölüm " : "Sayfa ";

        if (totalPages > 0) {
            textViewPageInfo.setText(pageLabel + (currentPage + 1) + " / " + totalPages);
            textViewPageInfo.setVisibility(View.VISIBLE);

            seekBarPage.setMax(totalPages - 1);
            seekBarPage.setProgress(currentPage);
            seekBarPage.setVisibility(View.VISIBLE);

            buttonPrevPage.setEnabled(currentPage > 0);
            buttonNextPage.setEnabled(currentPage < totalPages - 1);
            buttonPrevPage.setVisibility(View.VISIBLE);
            buttonNextPage.setVisibility(View.VISIBLE);
        } else {
            textViewPageInfo.setText(pageLabel + "yok");
            seekBarPage.setVisibility(View.GONE);
            buttonPrevPage.setEnabled(false);
            buttonNextPage.setEnabled(false);
        }
        Log.d(TAG, "Navigasyon UI güncellendi: current=" + currentPage + ", total=" + totalPages + ", label=" + pageLabel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bookManager != null && currentBook != null && bookManager.getBookByUri(currentBook.getFileUri()) != null && totalPages > 0) {
            bookManager.updateBookProgress(currentBook, currentPage, totalPages);
            Log.d(TAG, "onPause: " + currentBook.getTitle() + " için ilerleme kaydedildi: Sayfa " + currentPage + ", Toplam: " + totalPages);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfReader != null) pdfReader.closeDocument();
        if (epubReader != null) epubReader.closeDocument();
        if (epubWebView != null) {
            epubWebView.destroy();
            epubWebView = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
