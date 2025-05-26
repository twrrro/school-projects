package com.example.firebasetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    private Context context;
    private List<String> wallpaperUrls;
    private WallpaperClickListener listener; // listener olarak düzeltildi (PDF'te Listener)

    // Yapıcı metod
    public WallpaperAdapter(Context context, List<String> wallpaperUrls, WallpaperClickListener listener) {
        this.context = context; // this.context olarak düzeltildi (PDF'te $tinis,context=context,$)
        this.wallpaperUrls = wallpaperUrls; // this.wallpaperUrls olarak düzeltildi (PDF'te this.wallpaperünts)
        this.listener = listener; // this.listener olarak düzeltildi (PDF'te $ttiis.tistener=tisteneri$)
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // wallpaper_item layout'unu inflate et
        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false); // attachToRoot false olarak düzeltildi (PDF'te attachicRoot false)
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        // Mevcut pozisyondaki resim URL'sini al
        String url = wallpaperUrls.get(position);
        // Picasso kütüphanesi ile ImageView'a resmi yükle
        Picasso.get().load(url).into(holder.imageView); // imageView olarak düzeltildi (PDF'te holder.imageView)

        // Tıklama olayını ayarla
        holder.itemView.setOnClickListener(v -> listener.onWallpaperClick(url)); // Lambda ifadesiyle düzeltildi (PDF'te $(v-2)$)
    }

    @Override
    public int getItemCount() {
        // Listedeki toplam öğe sayısını döndür
        return wallpaperUrls.size();
    }

    // ViewHolder sınıfı
    public static class WallpaperViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // imageView olarak düzeltildi (PDF'te imageview)

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.wallpaperImageView); // wallpaperImageView olarak düzeltildi (PDF'te R.id.wallpaperImageView)
        }
    }

    // Tıklama olayları için arayüz
    public interface WallpaperClickListener {
        void onWallpaperClick(String url);
    }
}

