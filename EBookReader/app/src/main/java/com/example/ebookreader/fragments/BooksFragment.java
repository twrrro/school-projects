
package com.example.ebookreader.fragments;
import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission; // Eklendi
import android.net.Uri;
import android.os.Build; // Eklendi
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.ebookreader.R;
import com.example.ebookreader.activities.ReaderActivity;
import com.example.ebookreader.adapters.BookAdapter;
import com.example.ebookreader.managers.BookManager;
import com.example.ebookreader.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private static final String TAG = "BooksFragment";
    private static final int PICK_FILE_REQUEST_CODE_BOOKS = 102;

    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private BookManager bookManager;
    private List<Book> allBooksList;
    private FloatingActionButton fabAddBook;
    private TextView textViewEmptyBooks;

    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            bookManager = BookManager.getInstance(requireContext());
        }
        allBooksList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        booksRecyclerView = view.findViewById(R.id.recyclerViewAllBooks);
        booksRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bookAdapter = new BookAdapter(new ArrayList<>(), this);
        booksRecyclerView.setAdapter(bookAdapter);

        textViewEmptyBooks = view.findViewById(R.id.textViewEmptyAllBooks);
        fabAddBook = view.findViewById(R.id.fabAddBook);
        fabAddBook.setOnClickListener(v -> openFilePicker());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume çağrıldı, tüm kitaplar yükleniyor.");
        loadAllBooks();
    }

    private void loadAllBooks() {
        if (bookManager == null && getContext() != null) {
            bookManager = BookManager.getInstance(requireContext());
        }
        if (bookManager == null) {
            Log.e(TAG, "BookManager hala null, tüm kitaplar yüklenemiyor.");
            if (textViewEmptyBooks != null) textViewEmptyBooks.setVisibility(View.VISIBLE);
            if (booksRecyclerView != null) booksRecyclerView.setVisibility(View.GONE);
            return;
        }

        allBooksList = bookManager.getAllBooks();
        if (bookAdapter != null) {
            bookAdapter.updateData(allBooksList);
        } else {
            if (getContext() != null && booksRecyclerView != null) {
                bookAdapter = new BookAdapter(allBooksList, this);
                booksRecyclerView.setAdapter(bookAdapter);
            }
        }

        if (allBooksList.isEmpty()) {
            if (textViewEmptyBooks != null) textViewEmptyBooks.setVisibility(View.VISIBLE);
            if (booksRecyclerView != null) booksRecyclerView.setVisibility(View.GONE);
        } else {
            if (textViewEmptyBooks != null) textViewEmptyBooks.setVisibility(View.GONE);
            if (booksRecyclerView != null) booksRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBookClick(Book book) {
        if (book == null || book.getFileUri() == null) {
            Toast.makeText(getContext(), "Kitap açılamıyor: URI eksik.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Kitap tıklandı: " + book.getTitle() + " URI: " + book.getFileUri().toString());
        Intent intent = new Intent(getActivity(), ReaderActivity.class);
        intent.putExtra(ReaderActivity.EXTRA_BOOK_URI, book.getFileUri().toString());
        startActivity(intent);
    }

    @Override
    public void onBookLongClick(Book book) {
        Log.d(TAG, "Kitaba uzun tıklandı: " + book.getTitle());
        showDeleteConfirmationDialog(book);
    }

    private void showDeleteConfirmationDialog(final Book book) {
        if (getContext() == null || bookManager == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Kitabı Sil")
                .setMessage(book.getTitle() + " adlı kitabı kütüphaneden silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && book.getFileUri() != null) {
                        try {
                            getContext().getContentResolver().releasePersistableUriPermission(book.getFileUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Log.d(TAG, "Kalıcı URI izni kaldırıldı: " + book.getFileUri().toString());
                        } catch (SecurityException e) {
                            Log.e(TAG, "Kalıcı URI izni kaldırılırken hata: " + book.getFileUri().toString(), e);
                        }
                    }
                    bookManager.deleteBook(book);
                    loadAllBooks();
                    Toast.makeText(getContext(), book.getTitle() + " silindi.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/epub+zip"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        try {
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE_BOOKS);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Lütfen bir dosya yöneticisi uygulaması yükleyin.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE_BOOKS && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null && getContext() != null) {
                Uri fileUri = data.getData();
                Log.d(TAG, "Dosya seçildi (BooksFragment): " + fileUri.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContext().getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
                        Log.d(TAG, "Kalıcı URI izni alındı: " + fileUri.toString());
                    } catch (SecurityException e) {
                        Log.e(TAG, "Kalıcı URI izni alınamadı: " + fileUri.toString(), e);
                        Toast.makeText(getContext(), "Dosya erişim izni kalıcı yapılamadı.", Toast.LENGTH_LONG).show();
                    }
                }

                if (bookManager == null) {
                    bookManager = BookManager.getInstance(requireContext());
                }
                Book addedBook = bookManager.addBook(fileUri);
                if (addedBook != null) {
                    Toast.makeText(getContext(), addedBook.getTitle() + " kütüphaneye eklendi.", Toast.LENGTH_SHORT).show();
                    loadAllBooks();
                } else {
                    Toast.makeText(getContext(), "Kitap eklenemedi veya desteklenmiyor.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
