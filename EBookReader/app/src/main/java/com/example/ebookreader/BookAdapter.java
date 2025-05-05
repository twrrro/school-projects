package com.example.ebookreader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// RecyclerView.Adapter'dan türetip ViewHolder olarak BookViewHolder'ı belirtiyoruz
public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private List<Book> bookList;
    private OnBookItemClickListener clickListener; // <<<--- Tıklama dinleyicisi için değişken EKLENDİ

    /**
     * RecyclerView'daki bir öğeye tıklandığında tetiklenecek olayları dinlemek için arayüz (interface).
     * Bu interface, Fragment tarafından implemente edilir.
     */
    // --- INTERFACE TANIMI EKLENDİ ---
    public interface OnBookItemClickListener {
        /**
         * Bir kitap öğesine tıklandığında çağrılır.
         * @param book Tıklanan Book nesnesi.
         */
        void onBookClick(Book book);
    }
    // ------------------------------

    // Constructor: Adapter oluşturulurken kitap listesini alır
    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    /**
     * Dışarıdan (Fragment'tan) tıklama dinleyicisini ayarlamak için metot.
     */
    // --- LISTENER SET METODU EKLENDİ ---
    public void setOnBookItemClickListener(OnBookItemClickListener listener) {
        this.clickListener = listener;
    }
    // ---------------------------------

    // Yeni bir ViewHolder (liste satırı) oluşturulduğunda çağrılır
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // list_item_book.xml layout'unu inflate et (şişir/oluştur)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_book, parent, false);
        // Yeni bir BookViewHolder oluşturup döndür
        return new BookViewHolder(view);
    }

    // Bir ViewHolder'a veri bağlandığında (ekranda görünürken) çağrılır
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Listenin o anki pozisyonundaki kitabı al
        Book currentBook = bookList.get(position);
        // ViewHolder'ın bind metodu ile verileri TextView'lere yazdır
        holder.bind(currentBook);

        // --- TIKLAMA OLAYI AYARLAMASI EKLENDİ ---
        // ViewHolder'ın temsil ettiği tüm satıra (itemView) tıklama dinleyicisi ekle
        holder.itemView.setOnClickListener(v -> {
            // Eğer dışarıdan bir listener atanmışsa...
            if (clickListener != null) {
                // Tıklamanın yapıldığı anki güncel pozisyonu al (pozisyon değişmiş olabilir)
                int currentPosition = holder.getAdapterPosition();
                // Eğer pozisyon hala geçerliyse (silinmediyse vb.)...
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Listener'ın onBookClick metodunu çağır ve tıklanan kitabı gönder
                    clickListener.onBookClick(bookList.get(currentPosition));
                }
            }
        });
        // ---------------------------------------
    }

    // Listedeki toplam öğe sayısını döndürür
    @Override
    public int getItemCount() {
        // Liste null ise 0, değilse boyutunu döndür (NullPointerException önler)
        return bookList == null ? 0 : bookList.size();
    }

    // Adapter'ın listesini dışarıdan güncellemek için metot (Kullanılmadıysa silinebilir)
    public void setBooks(List<Book> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged(); // Tüm listenin değiştiğini bildir (daha az verimli)
    }
}