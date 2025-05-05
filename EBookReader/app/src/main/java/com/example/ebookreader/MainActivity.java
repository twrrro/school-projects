package com.example.ebookreader; // Senin paket adınla eşleşmeli

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem; // Import edildi

import com.google.android.material.bottomnavigation.BottomNavigationView; // Import edildi
import com.google.android.material.navigation.NavigationBarView; // Import edildi
import androidx.annotation.NonNull; // Import edildi


public class MainActivity extends AppCompatActivity {

    // BottomNavigationView için değişken tanımlandı
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main.xml layout'u set edildi
        setContentView(R.layout.activity_main);

        // Layout'taki BottomNavigationView bulundu
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Aktivite ilk kez oluşturuluyorsa (ekran döndürme gibi durumlarda tekrar yüklememek için)
        // başlangıç fragment'ı olarak MainPageFragment yükleniyor.
        if (savedInstanceState == null) {
            loadFragment(new MainPageFragment());
        }


        // BottomNavigationView için tıklama dinleyicisi (listener) ayarlandı
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId(); // Tıklanan öğenin ID'si alındı

                // Tıklanan ID'ye göre hangi fragment'ın yükleneceğine karar veriliyor
                if (itemId == R.id.nav_main_page) {
                    selectedFragment = new MainPageFragment();
                } else if (itemId == R.id.nav_books) {
                    selectedFragment = new BooksFragment();
                } else if (itemId == R.id.nav_jokes) {
                    selectedFragment = new JokesFragment();
                } else if (itemId == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                }

                // Eğer bir fragment seçildiyse (geçerli bir ID ise)
                if (selectedFragment != null) {
                    // loadFragment metodu çağrılarak ilgili fragment yükleniyor
                    loadFragment(selectedFragment);
                    return true; // İşlem başarılı
                }
                return false; // Geçersiz ID veya işlem başarısız
            }
        });
    }

    // Fragment'ları FrameLayout'a yüklemek için kullanılan yardımcı metot
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager(); // Fragment yöneticisi alındı
        FragmentTransaction transaction = fragmentManager.beginTransaction(); // Transaction başlatıldı
        // R.id.fragment_container ID'li FrameLayout içeriği verilen fragment ile değiştirildi
        transaction.replace(R.id.fragment_container, fragment);
        // Geri tuşu davranışı için isteğe bağlı: transaction.addToBackStack(null);
        transaction.commit(); // İşlem onaylandı
    }
}