// app/src
package com.example.ebookreader.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebookreader.R;
import com.example.ebookreader.adapters.FavoriteJokeAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class JokesFragment extends Fragment implements FavoriteJokeAdapter.OnFavoriteInteractionListener {

    private static final String TAG = "JokesFragment";
    private static final String FAVORITE_JOKES_PREFS = "FavoriteJokesPrefs";
    private static final String KEY_FAVORITE_JOKE_INDICES = "favoriteJokeIndices";

    private TextView textViewJokeContent;
    private Button buttonRefreshJoke;
    private ImageButton buttonFavoriteJoke;
    private ImageButton buttonShareJoke;
    private Button buttonToggleJokeView;

    private RelativeLayout layoutJokeOfTheDay;
    private RelativeLayout layoutFavoriteJokes;
    private RecyclerView recyclerViewFavoriteJokes;
    private TextView textViewNoFavoriteJokes;
    private FavoriteJokeAdapter favoriteJokeAdapter;
    private List<String> displayedFavoriteJokes;

    private SharedPreferences favoritePrefs;
    private Set<String> favoriteJokeIndicesSet;
    private int currentJokeOfTheDayIndex = -1;
    private boolean showingFavorites = false;

    private final String[] allJokes = {
            "Temel bir gün Dursun'a sormuş: 'Bu teknoloji çok gelişti değil mi?' Dursun cevap vermiş: 'Evet, eskiden mektup beklerdik, şimdi güncelleme bekliyoruz.'",
            "Adamın biri yolda elli lira bulmuş. Hemen gidip bir elli lira daha bulmuş ki, parası yüz lira olsun.",
            "İki deli hastaneden kaçmaya karar vermişler. Biri diğerine: 'Eğer duvar çok yüksekse altından, çok alçaksa üstünden atlarız.' Diğeri: 'Peki ya duvar yoksa?'",
            "Temel Fransa’ya gitmiş. Bir restoranda sormuş: 'Bana en pahalı şarabınızdan getirin.' Garson getirmiş. Temel bir yudum almış ve 'Bu bizim köydeki pekmezden daha güzel değil!' demiş.",
            "Bilgisayar neden hasta olur? Çünkü Windows'u vardır!",
            "Öğretmen sormuş: 'Çocuklar, büyük başarılar tesadüf müdür?' Ali cevap vermiş: 'Hayır öğretmenim, benim karnemdeki bütün zayıflar tesadüf değil, hepsi hak edilmiş!'",
            "Doktor hastasına sormuş: 'Neyiniz var?' Hasta: 'Bir şeyim yok doktor bey, sadece ziyarete geldim.' Doktor: 'Ama burası hastane, ziyaret saatleri bitti!'",
            "Nasreddin Hoca eşeğine ters binmiş. Sormuşlar: 'Hocam neden ters bindin?' Hoca cevaplamış: 'Düz binsem arkamda kalacaktınız, böylece yüz yüze geliyoruz.'",
            "İki karınca yolda gidiyormuş. Biri diğerine sormuş: 'Nereye gidiyoruz?' Diğeri: 'Bilmem, öndeki nereye giderse!'",
            "Temel, Dursun'a telefon etmiş: 'Alo Dursun, benim evde yangın çıktı, itfaiyeyi ara!' Dursun: 'Tamam Temel, numarasını biliyor musun?'"
    };

    public JokesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            favoritePrefs = getContext().getSharedPreferences(FAVORITE_JOKES_PREFS, Context.MODE_PRIVATE);
            favoriteJokeIndicesSet = new HashSet<>(favoritePrefs.getStringSet(KEY_FAVORITE_JOKE_INDICES, new HashSet<>()));
        } else {
            favoriteJokeIndicesSet = new HashSet<>();
        }
        displayedFavoriteJokes = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jokes, container, false);
        Log.d(TAG, "onCreateView çağrıldı.");

        textViewJokeContent = view.findViewById(R.id.textViewJokeContent);
        buttonRefreshJoke = view.findViewById(R.id.buttonRefreshJoke);
        buttonFavoriteJoke = view.findViewById(R.id.buttonFavoriteJoke);
        buttonShareJoke = view.findViewById(R.id.buttonShareJoke);
        buttonToggleJokeView = view.findViewById(R.id.buttonToggleJokeView);

        layoutJokeOfTheDay = view.findViewById(R.id.layoutJokeOfTheDay);
        layoutFavoriteJokes = view.findViewById(R.id.layoutFavoriteJokes);
        recyclerViewFavoriteJokes = view.findViewById(R.id.recyclerViewFavoriteJokes);
        textViewNoFavoriteJokes = view.findViewById(R.id.textViewNoFavoriteJokes);

        if (recyclerViewFavoriteJokes != null) {
            recyclerViewFavoriteJokes.setLayoutManager(new LinearLayoutManager(getContext()));
            favoriteJokeAdapter = new FavoriteJokeAdapter(displayedFavoriteJokes, this);
            recyclerViewFavoriteJokes.setAdapter(favoriteJokeAdapter);
        } else {
            Log.e(TAG, "recyclerViewFavoriteJokes null!");
        }

        updateViewVisibility();
        if (!showingFavorites) {
            loadDailyJoke();
        } else {
            loadFavoriteJokesList();
        }

        if (buttonRefreshJoke != null) {
            buttonRefreshJoke.setOnClickListener(v -> {
                Log.d(TAG, "Başka Fıkra butonuna tıklandı."); //daha sonra yapıcam
                showingFavorites = false;
                updateViewVisibility();
                loadDailyJoke();
            });
        } else {
            Log.e(TAG, "buttonRefreshJoke null!");
        }

        if (buttonShareJoke != null) {
            buttonShareJoke.setOnClickListener(v -> {
                Log.d(TAG, "Fıkra Paylaş butonuna tıklandı.");
                shareCurrentJokeOfTheDay();
            });
        } else {
            Log.e(TAG, "buttonShareJoke null!");
        }

        if (buttonFavoriteJoke != null) {
            buttonFavoriteJoke.setOnClickListener(v -> {
                Log.d(TAG, "Favori butonuna tıklandı.");
                toggleFavoriteCurrentJokeOfTheDay();
            });
        } else {
            Log.e(TAG, "buttonFavoriteJoke null!");
        }

        if (buttonToggleJokeView != null) {
            buttonToggleJokeView.setOnClickListener(v -> {
                Log.d(TAG, "Görünüm Değiştir butonuna tıklandı.");
                toggleJokeView();
            });
        } else {
            Log.e(TAG, "buttonToggleJokeView null!");
        }

        return view;
    }

    private void toggleJokeView() {
        showingFavorites = !showingFavorites;
        Log.d(TAG, "toggleJokeView: showingFavorites = " + showingFavorites);
        updateViewVisibility();
        if (showingFavorites) {
            loadFavoriteJokesList();
        } else {
            if (currentJokeOfTheDayIndex != -1 && currentJokeOfTheDayIndex < allJokes.length) {
                textViewJokeContent.setText(allJokes[currentJokeOfTheDayIndex]);
                updateFavoriteButtonState();
            } else {
                loadDailyJoke(); //yeni fıkrayı yükle
            }
        }
    }

    private void updateViewVisibility() {
        if (layoutJokeOfTheDay == null || layoutFavoriteJokes == null || buttonToggleJokeView == null) {
            Log.e(TAG, "updateViewVisibility: Layout elemanlarından biri null.");
            return;
        }
        if (showingFavorites) {
            layoutJokeOfTheDay.setVisibility(View.GONE);
            layoutFavoriteJokes.setVisibility(View.VISIBLE);
            buttonToggleJokeView.setText("Günün Fıkrasını Göster");
        } else {
            layoutJokeOfTheDay.setVisibility(View.VISIBLE);
            layoutFavoriteJokes.setVisibility(View.GONE);
            buttonToggleJokeView.setText("Favorileri Göster");
        }
        Log.d(TAG, "View visibility güncellendi. Favoriler gösteriliyor: " + showingFavorites);
    }

    private void loadDailyJoke() {
        if (allJokes.length == 0 || textViewJokeContent == null) {
            if(textViewJokeContent != null) textViewJokeContent.setText("Fıkra bulunamadı.");
            currentJokeOfTheDayIndex = -1;
            updateFavoriteButtonState();
            Log.w(TAG, "Fıkra listesi boş veya textViewJokeContent null.");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        currentJokeOfTheDayIndex = dayOfYear % allJokes.length;

        textViewJokeContent.setText(allJokes[currentJokeOfTheDayIndex]);
        Log.d(TAG, "Günün fıkrası yüklendi: index " + currentJokeOfTheDayIndex);
        updateFavoriteButtonState();
    }

    private void shareCurrentJokeOfTheDay() {
        if (currentJokeOfTheDayIndex != -1 && currentJokeOfTheDayIndex < allJokes.length && getContext() != null) {
            String jokeText = allJokes[currentJokeOfTheDayIndex];
            shareJoke(jokeText);
        } else {
            Log.w(TAG, "Paylaşılacak geçerli bir günün fıkrası yok.");
            if (getContext() != null) {
                Toast.makeText(getContext(), "Paylaşılacak fıkra bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareJoke(String jokeText) {
        if (getContext() == null || jokeText == null || jokeText.isEmpty()) {
            Log.w(TAG, "Paylaşma işlemi için context veya fıkra metni eksik.");
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, jokeText);
        try {
            startActivity(Intent.createChooser(shareIntent, "Fıkrayı Paylaş"));
        } catch (Exception e) {
            Log.e(TAG, "Paylaşma intent'i başlatılamadı.", e);
            Toast.makeText(getContext(), "Paylaşma işlemi yapılamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavoriteCurrentJokeOfTheDay() {
        if (currentJokeOfTheDayIndex == -1 || getContext() == null || favoritePrefs == null) {
            if (getContext() != null) Toast.makeText(getContext(), "Fıkra favorilenemedi.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Favori işlemi için geçerli fıkra indeksi veya context/prefs yok.");
            return;
        }

        String jokeIndexStr = String.valueOf(currentJokeOfTheDayIndex);
        if (favoriteJokeIndicesSet.contains(jokeIndexStr)) {
            favoriteJokeIndicesSet.remove(jokeIndexStr);
            Toast.makeText(getContext(), "Favorilerden çıkarıldı.", Toast.LENGTH_SHORT).show();
        } else {
            favoriteJokeIndicesSet.add(jokeIndexStr);
            Toast.makeText(getContext(), "Favorilere eklendi!", Toast.LENGTH_SHORT).show();
        }
        saveFavoriteJokes();
        updateFavoriteButtonState();
        if (showingFavorites) {
            loadFavoriteJokesList();
        }
    }

    private void updateFavoriteButtonState() {
        if (buttonFavoriteJoke == null || getContext() == null) return;
        if (currentJokeOfTheDayIndex != -1 && favoriteJokeIndicesSet.contains(String.valueOf(currentJokeOfTheDayIndex))) {
            buttonFavoriteJoke.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            buttonFavoriteJoke.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void saveFavoriteJokes() {
        if (favoritePrefs != null) {
            favoritePrefs.edit().putStringSet(KEY_FAVORITE_JOKE_INDICES, favoriteJokeIndicesSet).apply();
            Log.d(TAG, "Favori fıkra indeksleri kaydedildi: " + favoriteJokeIndicesSet.toString());
        }
    }

    private void loadFavoriteJokesList() {
        if (displayedFavoriteJokes == null || favoriteJokeAdapter == null || textViewNoFavoriteJokes == null || recyclerViewFavoriteJokes == null || favoriteJokeIndicesSet == null) {
            Log.e(TAG, "loadFavoriteJokesList: Gerekli UI elemanlarından veya listelerden biri null.");
            return;
        }
        displayedFavoriteJokes.clear();
        if (favoriteJokeIndicesSet.isEmpty()) {
            textViewNoFavoriteJokes.setVisibility(View.VISIBLE);
            recyclerViewFavoriteJokes.setVisibility(View.GONE);
            Log.d(TAG, "Favori fıkra bulunmuyor.");
        } else {
            textViewNoFavoriteJokes.setVisibility(View.GONE);
            recyclerViewFavoriteJokes.setVisibility(View.VISIBLE);
            List<Integer> indices = new ArrayList<>();
            for (String s : favoriteJokeIndicesSet) {
                try {
                    indices.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Favori indeksi parse edilemedi: " + s, e);
                }
            }
            Collections.sort(indices);
            for (int index : indices) {
                if (index >= 0 && index < allJokes.length) {
                    displayedFavoriteJokes.add(allJokes[index]);
                }
            }
            Log.d(TAG, "Favori fıkralar yüklendi, sayı: " + displayedFavoriteJokes.size());
        }
        favoriteJokeAdapter.updateJokes(displayedFavoriteJokes);
    }

    @Override
    public void onUnfavoriteClicked(int position, String joke) {
        if (getContext() == null || favoriteJokeIndicesSet == null || displayedFavoriteJokes == null || position < 0 || position >= displayedFavoriteJokes.size()) {
            Log.w(TAG, "onUnfavoriteClicked: Geçersiz durum veya pozisyon.");
            return;
        }

        String jokeToUnfavorite = displayedFavoriteJokes.get(position);
        int originalIndex = -1;
        for(int i=0; i < allJokes.length; i++){
            if(allJokes[i].equals(jokeToUnfavorite)){
                originalIndex = i;
                break;
            }
        }

        if (originalIndex != -1) {
            String jokeIndexStr = String.valueOf(originalIndex);
            if (favoriteJokeIndicesSet.contains(jokeIndexStr)) {
                favoriteJokeIndicesSet.remove(jokeIndexStr);
                saveFavoriteJokes();
                loadFavoriteJokesList();
                updateFavoriteButtonState();
                Toast.makeText(getContext(), "Favorilerden çıkarıldı.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Favorilerden çıkarılacak fıkranın orijinal indeksi bulunamadı: " + jokeToUnfavorite);
        }
    }

    @Override
    public void onShareFavoriteClicked(String joke) {
        shareJoke(joke);
    }
}
