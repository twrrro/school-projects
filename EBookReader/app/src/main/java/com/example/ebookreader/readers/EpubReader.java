
package com.example.ebookreader.readers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.IOException; // IOException importu eklendi

public class EpubReader {
    private static final String TAG = "EpubReader";
    private Context context;
    private Uri currentFileUri;
    private List<String> htmlEntryNames;
    private String baseHrefPath = ""; //resim ve css

    public EpubReader(Context context) {
        this.context = context;
        this.htmlEntryNames = new ArrayList<>();
    }

    public boolean loadDocument(Uri fileUri) {
        this.currentFileUri = fileUri;
        this.htmlEntryNames.clear();
        this.baseHrefPath = "";
        Log.i(TAG, "loadDocument: EPUB yükleniyor - " + fileUri);

        if (fileUri == null) {
            Log.e(TAG, "loadDocument: fileUri null.");
            return false;
        }

        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             ZipInputStream zisForOpf = (inputStream != null) ? new ZipInputStream(inputStream) : null) {

            if (zisForOpf == null) {
                Log.e(TAG, "loadDocument: InputStream (for OPF) null for URI: " + fileUri);
                if (inputStream != null) inputStream.close(); // inputStream null değilse kapat
                return false;
            }

            ZipEntry zipEntry;
            String oebpsPath = null;

            while ((zipEntry = zisForOpf.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (entryName.toLowerCase().endsWith(".opf")) {
                    int lastSlash = entryName.lastIndexOf('/');
                    if (lastSlash != -1) {
                        oebpsPath = entryName.substring(0, lastSlash + 1);
                        baseHrefPath = oebpsPath;
                        Log.d(TAG, "OPF dosyası bulundu, baseHrefPath ayarlandı: " + baseHrefPath);
                    }
                    break;
                }
            }
            // zisForOpf try-with-resources ile kapanacak, inputStream de onunla birlikte

        } catch (IOException e) {
            Log.e(TAG, "EPUB OPF okunurken IOException: " + fileUri.toString(), e);
            return false; // OPF okunamadıysa devam etme
        } catch (Exception e) {
            Log.e(TAG, "EPUB OPF okunurken genel hata: " + fileUri.toString(), e);
            return false;
        }


        // HTML dosyalarını listele
        try (InputStream htmlInputStream = context.getContentResolver().openInputStream(fileUri);
             ZipInputStream contentZis = (htmlInputStream != null) ? new ZipInputStream(htmlInputStream) : null) {

            if (contentZis == null) {
                Log.e(TAG, "loadDocument: InputStream (for HTML) null for URI: " + fileUri);
                if (htmlInputStream != null) htmlInputStream.close();
                return false;
            }
            ZipEntry zipEntry;
            while ((zipEntry = contentZis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if ((entryName.toLowerCase().endsWith(".xhtml") || entryName.toLowerCase().endsWith(".html"))) {
                    htmlEntryNames.add(entryName);
                    Log.d(TAG, "Bulunan HTML/XHTML dosyası: " + entryName);
                }
            }
            // contentZis try-with-resources ile kapanacak

        } catch (IOException e) {
            Log.e(TAG, "EPUB HTML listesi okunurken IOException: " + fileUri.toString(), e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "EPUB HTML listesi okunurken genel hata: " + fileUri.toString(), e);
            return false;
        }

        Collections.sort(htmlEntryNames);
        Log.i(TAG, "EPUB yüklendi, " + htmlEntryNames.size() + " HTML bölümü bulundu.");
        return !htmlEntryNames.isEmpty();
    }

    public String getPageContent(int pageIndex) {
        if (currentFileUri == null || htmlEntryNames.isEmpty() || pageIndex < 0 || pageIndex >= htmlEntryNames.size()) {
            Log.e(TAG, "getPageContent: Geçersiz sayfa indeksi veya belge yüklenmemiş: " + pageIndex);
            return "<html><body><h1>İçerik Yüklenemedi</h1><p>Belirtilen sayfa bulunamadı.</p></body></html>";
        }

        String entryName = htmlEntryNames.get(pageIndex);
        Log.d(TAG, "Sayfa " + pageIndex + " için içerik okunuyor: " + entryName);

        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = context.getContentResolver().openInputStream(currentFileUri);
             ZipInputStream zis = (inputStream != null) ? new ZipInputStream(inputStream) : null) {

            if (zis == null) {
                Log.e(TAG, "getPageContent: InputStream null for URI: " + currentFileUri);
                if (inputStream != null) inputStream.close();
                return "<html><body><h1>Hata</h1><p>Kitap dosyası okunamadı.</p></body></html>";
            }

            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals(entryName)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    Log.d(TAG, entryName + " başarıyla okundu.");
                    break;
                }
            }
            // zis ve inputStream try-with-resources ile kapanacak

            return content.toString();

        } catch (IOException e) {
            Log.e(TAG, "EPUB içeriği okunurken IOException: " + entryName, e);
        } catch (Exception e) {
            Log.e(TAG, "EPUB içeriği okunurken genel hata: " + entryName, e);
        }
        return "<html><body><h1>İçerik Yüklenemedi</h1><p>Sayfa içeriği okunurken bir hata oluştu.</p></body></html>";
    }

    public int getPageCount() {
        return htmlEntryNames.size();
    }

    public void closeDocument() {
        Log.i(TAG, "EpubReader.closeDocument çağrıldı.");
        htmlEntryNames.clear();
        currentFileUri = null;
        baseHrefPath = "";
    }


    public String getBaseHrefPathForWebView() {
        if (currentFileUri != null && currentFileUri.getScheme() != null && currentFileUri.getScheme().equals("file")) {

            if (!baseHrefPath.isEmpty()) {
            }
        }
        return null;
    }
}
