package com.example.ebookreader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // TextView import edildi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays; // Arrays import edildi
import java.util.Calendar; // Calendar import edildi
import java.util.List; // List import edildi
import java.util.TimeZone; // TimeZone import edildi

public class JokesFragment extends Fragment {

    // Fıkra listesi
    private final List<String> jokes = Arrays.asList(
            // 1. Fıkra
            "AĞIR\n\nDelikanlı çalıştığı şirketin mektuplarını postalayacaktı. Postacı mektuplardan birisini tartıp; \"Bu çok ağır!\" dedi. \"Biraz daha pul yapıştırmamız lazım.\"\nDelikanlı:\n\"Abi!” dedi. \"O zaman daha ağır olmaz mı?”",
            // 2. Fıkra
            "SAAT\n\nTemel aldığı bir daktiloyu bozuk diye geri götürdü. Satıcı;\n- Neresi bozuk, dün aldığında sağlamdı.\nTemel:\n- İki tane \"a\" yok, saat yazamıyorum.",
            // 3. Fıkra
            "MAYMUN\n\nKadın bebeğiyle otobüse binerken otobüs şoförü kendini tutamayıp şöyle demiş:\n- \"Aman tanrım ne kadar çirkin bir bebek...\"\nKadın sinirle biletini kutuya basmış, en arka tarafa geçmiş, bir adamın yanındaki boş yere oturmuş. Adam dönmüş kadına;\n- \"Özür dilerim. Acaba az önce şoförle aranızda ne geçti?\"\nKadın:\n- \"Büyük bir terbiyesizlik etti. Hakaret...\"\nAdam:\n- \"Bir kamu görevlisi insanlara hakaret edemez. Suç teşkil eder.\"\nKadın:\n- \"Doğru. Gideyim de şunu bir azarlayayım.\"\n- \"Merak etmeyin, ben maymununuza göz kulak olurum...\"",
            // 4. Fıkra
            "PAPAĞAN\n\nAdam, papağanını gümrükten kolay geçirebilmek için bir kutuya koymuş, üstüne de \"kırılacak eşya\" diye yazmıştı.\nGümrük memuru yazıyı okuyunca, kutuyu şöyle bir silkelemeye başladı. Aynı anda içeriden papağanın bağırdığı duyuldu:\n\"Şangur şungur.. Şangur şungur..\"",
            // 5. Fıkra
            "İKİ OLASILIK\n\nTemel ile Dursun konuşuyorlardı…\nTemel Dursun’a sorar:\n– Savaş çıkarsa yandık galiba.\nDursun düşündü:\n– İki olasılık var, dedi. Ya çıkar ya çıkmaz. Çıkmazsa mesele yok, çıkarsa iki olasılık var:\nYa çürüğe çıkarız ya askere alınırız. Çürüğe çıkarsak mesele yok, askere alınırsak iki olasılık var: Ya geri cephe ya ileri cephe. Geri cephede kalırsak mesele yok, ileri cepheye gidersek iki olasılık var: Savaşı ya kazanırız ya kaybederiz. Kazanırsak mesele yok, kaybedersek iki olasılık var: Ya esir düşeriz ya ölürüz. Esir düşersek mesele yok, ölürsek iki olasılık var: Ya gömerler ya kağıt fabrikasına yollarlar. Gömerlerse mesele yok, kağıt fabrikasına yollarlarsa iki olasılık var: Ya gazete kağıdı ya tuvalet kağıdı. Gazete kağıdı olursak mesele yok, tuvalet kağıdı olursaaak… İşte o zaman yandık Temel!"
    );

    private TextView jokeContentTextView; // Fıkrayı gösterecek TextView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_jokes.xml layout'unu inflate et
        View view = inflater.inflate(R.layout.fragment_jokes, container, false);
        return view;
    }

    // onCreateView bittikten sonra, View'lar hazır olduğunda çağrılır
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout'taki TextView'i ID ile bul
        jokeContentTextView = view.findViewById(R.id.text_joke_content);

        // Günün fıkrasını göster
        displayJokeOfTheDay();
    }

    // Günün fıkrasını seçip TextView'e yazan metot
    private void displayJokeOfTheDay() {
        if (jokes == null || jokes.isEmpty()) {
            jokeContentTextView.setText("Gösterilecek fıkra bulunamadı.");
            return;
        }

        // Bulunduğumuz yerin saat dilimini al (Türkiye için)
        TimeZone turkeyTimeZone = TimeZone.getTimeZone("Europe/Istanbul");
        Calendar calendar = Calendar.getInstance(turkeyTimeZone);

        // Yılın kaçıncı günü olduğunu al (1'den başlar)
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        // Fıkra listesindeki indeksi hesapla
        // (dayOfYear - 1) kullanıyoruz çünkü indeksler 0'dan başlar
        int jokeIndex = (dayOfYear - 1) % jokes.size();

        // Seçilen fıkrayı al
        String joke = jokes.get(jokeIndex);

        // TextView'e fıkrayı yazdır
        jokeContentTextView.setText(joke);
    }
}
