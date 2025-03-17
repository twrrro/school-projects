package com.example.ebookapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textJoke;
    private RecyclerView recyclerViewBooks;
    //private BookAdapter bookAdapter;
    private List<Book> bookList;
    private SharedPreferences prefs;

    private String[] jokes = {
            "Adamın biri düşmüş, çıkamamış!",
            "Temel neden tuvalete kırmızı boya dökmüş? Çünkü kırmızı çok moda!",
            "Bir matematikçi denize düşmüş, çarpanlarına ayrılmış!",
            "Doktora gittim, ‘Gözlerim iyi görmüyor’ dedim. O da bana gözlük verdi. Şimdi mükemmel görüyorum ama gözlük yok!",
            "Öğrenci: Hocam kağıt bitti! Hoca: Güzel, en azından bir şey yazmışsın!",
            "Telefonum çalınca korkuyorum, ya yine borç para isterse!",
            "En hızlı sayı hangisidir? On! Çünkü on numara!",
            "Şoför mü direksiyonu kullanır, direksiyon mu şoförü?",
            "Saat neden duvara asılmış? Çünkü zamanı yakalamaya çalışıyor!",
            "Mutfaktaki çatal bıçak kavga etmiş, en son kaşık hepsini yutmuş!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textJoke = findViewById(R.id.textJoke);
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);

        prefs = getSharedPreferences("JokePrefs", MODE_PRIVATE);
        showDailyJoke();

        setupRecyclerView();
    }

    private void showDailyJoke() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastDay = prefs.getInt("lastDay", -1);
        int lastJokeIndex = prefs.getInt("lastJokeIndex", -1);

        if (today != lastDay) {
            lastJokeIndex = (lastJokeIndex + 1) % jokes.length;
            prefs.edit().putInt("lastDay", today).putInt("lastJokeIndex", lastJokeIndex).apply();
        }

        textJoke.setText(jokes[lastJokeIndex]);
    }

    private void setupRecyclerView() {
        bookList = new ArrayList<>();
        bookList.add(new Book("Kitap 1", "path/to/book1.pdf"));
        bookList.add(new Book("Kitap 2", "path/to/book2.pdf"));
        bookList.add(new Book("Kitap 3", "path/to/book3.epub"));

        /*bookAdapter = new BookAdapter(bookList, book -> {
            Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
            intent.putExtra("filePath", book.getFilePath());
            startActivity(intent);
        });*/
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewBooks.setAdapter(bookAdapter);
    }
}
