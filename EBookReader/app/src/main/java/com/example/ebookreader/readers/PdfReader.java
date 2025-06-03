// app/src/main/java/com/example/ebookreader/readers/PdfReader.java
package com.example.ebookreader.readers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class PdfReader {
    private static final String TAG = "PdfReader";
    private Context context;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage; //current

    public PdfReader(Context context) {
        this.context = context;
    }

    public boolean loadDocument(Uri fileUri) {
        if (fileUri == null) {
            Log.e(TAG, "loadDocument: fileUri is null");
            return false;
        }
        Log.d(TAG, "loadDocument: URI: " + fileUri.toString());
        try {
            closeDocument();
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(fileUri, "r");
            if (parcelFileDescriptor == null) {
                Log.e(TAG, "loadDocument: ParcelFileDescriptor is null for URI: " + fileUri);
                return false;
            }
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            Log.i(TAG, "PDF yüklendi, sayfa sayısı: " + pdfRenderer.getPageCount());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "PDF yüklenirken IOException oluştu: " + fileUri.toString(), e);
            closeDocument();
        } catch (SecurityException e) {
            Log.e(TAG, "PDF yüklenirken SecurityException (izin sorunu olabilir): " + fileUri.toString(), e);
            closeDocument();
        } catch (Exception e) {
            Log.e(TAG, "PDF yüklenirken genel bir hata oluştu: " + fileUri.toString(), e);
            closeDocument();
        }
        return false;
    }

    public Bitmap renderPage(int pageIndex) {
        if (pdfRenderer == null) {
            Log.e(TAG, "renderPage: pdfRenderer is null.");
            return null;
        }
        if (pageIndex < 0 || pageIndex >= pdfRenderer.getPageCount()) {
            Log.e(TAG, "renderPage: Geçersiz sayfa indeksi: " + pageIndex + ", Toplam Sayfa: " + pdfRenderer.getPageCount());
            return null;
        }

        try {
            // Mevcut bir sayfa açıksa kapat
            if (currentPage != null) {
                currentPage.close();
            }
            currentPage = pdfRenderer.openPage(pageIndex);

            int width = currentPage.getWidth();
            int height = currentPage.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            Log.d(TAG, "Sayfa " + pageIndex + " render edildi.");
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "PDF sayfası render edilirken hata: " + pageIndex, e);
            return null;
        }
    }

    public int getPageCount() {
        if (pdfRenderer != null) {
            return pdfRenderer.getPageCount();
        }
        return 0;
    }

    public void closeDocument() {
        Log.d(TAG, "closeDocument çağrıldı.");
        try {
            if (currentPage != null) {
                currentPage.close();
                currentPage = null;
            }
            if (pdfRenderer != null) {
                pdfRenderer.close();
                pdfRenderer = null;
            }
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
                parcelFileDescriptor = null;
            }
            Log.i(TAG, "PDF belgesi ve kaynaklar kapatıldı.");
        } catch (IOException e) {
            Log.e(TAG, "PDF belgesi kapatılırken IOException.", e);
        }
    }
}
