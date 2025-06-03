
package com.example.ebookreader.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookreader.R; // namespace'e göre R sınıfı
import com.example.ebookreader.models.Book;
import com.example.ebookreader.models.BookType;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener onBookClickListener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onBookLongClick(Book book);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.onBookClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_book, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);
        holder.titleTextView.setText(currentBook.getTitle());
        holder.authorTextView.setText(currentBook.getAuthor() != null ? currentBook.getAuthor() : "Bilinmiyor");
        if (currentBook.getType() != null) {
            holder.typeTextView.setText(currentBook.getType().toString());
        } else {
            holder.typeTextView.setText(BookType.UNKNOWN.toString());
        }


        // Kapak resmi için placeholder veya gerçek resim yükleme logiği
        if (currentBook.getType() == BookType.PDF) {
            holder.coverImageView.setImageResource(R.drawable.ic_pdf_icon);
        } else if (currentBook.getType() == BookType.EPUB) {
            holder.coverImageView.setImageResource(R.drawable.ic_epub_icon);
        } else {
            holder.coverImageView.setImageResource(R.drawable.ic_book_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onBookClickListener != null) {
                onBookClickListener.onBookClick(currentBook);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onBookClickListener != null) {
                onBookClickListener.onBookLongClick(currentBook);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public void updateData(List<Book> newBookList) {
        this.bookList.clear();
        if (newBookList != null) {
            this.bookList.addAll(newBookList);
        }
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView typeTextView;

        BookViewHolder(View view) {
            super(view);
            coverImageView = view.findViewById(R.id.imageViewBookCover);
            titleTextView = view.findViewById(R.id.textViewBookTitle);
            authorTextView = view.findViewById(R.id.textViewBookAuthor);
            typeTextView = view.findViewById(R.id.textViewBookType);
        }
    }
}
