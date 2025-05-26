package com.example.firebasetest;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast; // Hata durumları için Toast eklendi

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WallpaperAdapter.WallpaperClickListener {

    private RecyclerView recyclerView;
    private WallpaperAdapter adapter;
    private List<String> wallpaperUrls;
    private FirebaseStorage storage; // Firebase Storage referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main olarak düzeltildi (PDF'te activity_moin)

        recyclerView = findViewById(R.id.recyclerView); // recyclerView olarak düzeltildi (PDF'te recyclerview)
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // this olarak düzeltildi (PDF'te context this)

        wallpaperUrls = new ArrayList<>();
        adapter = new WallpaperAdapter(this, wallpaperUrls, this); // this olarak düzeltildi (PDF'te context this, listenen this)
        recyclerView.setAdapter(adapter);

        storage = FirebaseStorage.getInstance(); // Firebase Storage initialize edildi

        loadWallpapers();
    }

    private void loadWallpapers() {
        // Firebase Storage'dan 'wallpapers' klasöründeki resimleri listele
        StorageReference listRef = storage.getReference().child("wallpapers"); // pathString etiketi kaldırıldı

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            wallpaperUrls.add(uri.toString());
                            adapter.notifyItemInserted(wallpaperUrls.size() - 1); // Daha verimli güncelleme
                        }).addOnFailureListener(exception -> {
                            // Resim URL'si alınırken hata oluştu
                            Toast.makeText(MainActivity.this, "Resim URL'si alınamadı: " + item.getName(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    if (listResult.getItems().isEmpty()){
                        // HATA DÜZELTMESİ: Toast.LONG_SHOW -> Toast.LENGTH_LONG
                        Toast.makeText(MainActivity.this, "Firebase Storage 'wallpapers' klasöründe resim bulunamadı.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Resimler listelenirken hata oluştu
                    Toast.makeText(MainActivity.this, "Resimler yüklenirken hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onWallpaperClick(String url) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this); // MainActivity.this olarak düzeltildi (PDF'te context: MainActivity.this)
                try {
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(MainActivity.this, "Duvar kağıdı ayarlandı!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Duvar kağıdı ayarlanamadı.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Resim yüklenemedi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // İsteğe bağlı: Resim yüklenirken bir placeholder gösterilebilir
            }
        });
    }
}
