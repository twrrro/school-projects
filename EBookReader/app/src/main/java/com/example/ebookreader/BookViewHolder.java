package com.example.ebookreader;

import android.view.View;
import android.widget.TextView; // Ekle
import androidx.annotation.NonNull; // Ekle
import androidx.recyclerview.widget.RecyclerView; // Ekle

public class BookViewHolder extends RecyclerView.ViewHolder {

    // Layout'taki TextView'ler için değişkenler
    TextView titleTextView;
    TextView detailsTextView;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        // Değişkenleri layout'taki ID'lerle eşleştir
        titleTextView = itemView.findViewById(R.id.text_book_title);
        detailsTextView = itemView.findViewById(R.id.text_book_details);

        // TODO: Satıra tıklama listener'ını burada ayarlayabiliriz
        // itemView.setOnClickListener(v -> { ... });
    }

    // Bu ViewHolder'ı belirli bir kitap verisiyle dolduran metot
    public void bind(Book book) {
        titleTextView.setText(book.getTitle());
        // Detay olarak şimdilik dosya yolunu gösterelim (URI string'i)
        detailsTextView.setText("Tür: " + book.getType() + " | Yol: " + book.getFilePath());
        // İleride yazar, sayfa sayısı gibi bilgiler de eklenebilir
    }
}