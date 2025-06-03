
package com.example.ebookreader.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ebookreader.R;
import com.example.ebookreader.fragments.BooksFragment;
import com.example.ebookreader.fragments.JokesFragment; // JokesFragment import edildi
import com.example.ebookreader.fragments.MainPageFragment;
import com.example.ebookreader.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new MainPageFragment();
                    Log.d(TAG, "Ana Sayfa sekmesi seçildi.");
                } else if (itemId == R.id.navigation_books) {
                    selectedFragment = new BooksFragment();
                    Log.d(TAG, "Kitaplarım sekmesi seçildi.");
                } else if (itemId == R.id.navigation_jokes) {
                    selectedFragment = new JokesFragment();
                    Log.d(TAG, "Fıkralar sekmesi seçildi.");
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new SettingsFragment();
                    Log.d(TAG, "Ayarlar sekmesi seçildi.");
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Başlangıç fragment'ını yükle
        if (savedInstanceState == null) {
            // Varsayılan olarak Ana Sayfa sekmesini seçili hale getir ve fragment'ı yükle
            // Listener zaten yükleyeceği için setSelectedItemId yeterli.
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        checkAndRequestPermissions();
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Animasyon eklemek istersen:
            // fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            // Geri tuşuyla fragmentlar arası geçiş isteniyorsa addToBackStack kullanılabilir,
            // ancak BottomNavigationView ile genellikle bu istenmez.
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            Log.d(TAG, fragment.getClass().getSimpleName() + " yüklendi.");
        } catch (Exception e) {
            Log.e(TAG, "Fragment yüklenirken hata oluştu", e);
        }
    }

    // Bu metot, MainPageFragment'tan çağrılabilir (örneğin bir buton ile)
    public void navigateToBooksFragment() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_books);
        }
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "READ_EXTERNAL_STORAGE izni isteniyor.");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE izni zaten verilmiş.");
                // İzin zaten varsa, fragment'ların listelerini yenilemesi için bir işaret gönderebiliriz
                // veya fragment'lar onResume içinde bu kontrolü yapabilir.
            }
        } else {
            // Android 6.0 altı için izinler manifest'te tanımlandığı için otomatik verilir.
            Log.d(TAG, "Android 6.0 altı, izinler manifestten alınır.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "READ_EXTERNAL_STORAGE izni kullanıcı tarafından verildi.");
                Toast.makeText(this, "Dosya okuma izni verildi.", Toast.LENGTH_SHORT).show();
                // İzin verildikten sonra, aktif olan fragment'ın listesini yenilemesi gerekebilir.
                // Bu, fragment'ların onResume metodunda yapılabilir veya buradan bir event gönderilebilir.
                // En basit yöntem, fragment'ların onResume'da listeyi yenilemesidir.
                // Örneğin, MainPageFragment ve BooksFragment onResume'da listelerini güncelliyor.
                // Eğer o an aktif bir fragment varsa ve izin gerektiren bir işlem yapıyorsa,
                // o fragment'ı bilgilendirmek için bir arayüz veya event kullanılabilir.
                // Şimdilik, fragment'ların onResume'da kendi listelerini yenilediğini varsayıyoruz.
                // Eğer MainActivity'de bir değişiklik yapılması gerekiyorsa (örn. bir fragment'ı yeniden yüklemek),
                // o zaman aşağıdaki gibi bir kontrol yapılabilir:
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof MainPageFragment) {
                    // ((MainPageFragment) currentFragment).onStoragePermissionGranted(); // Özel bir metot
                    ((MainPageFragment) currentFragment).onResume(); // Veya onResume ile listeyi yenile
                } else if (currentFragment instanceof BooksFragment) {
                    // ((BooksFragment) currentFragment).onStoragePermissionGranted(); // Özel bir metot
                    ((BooksFragment) currentFragment).onResume(); // Veya onResume ile listeyi yenile
                }

            } else {
                Log.w(TAG, "READ_EXTERNAL_STORAGE izni reddedildi.");
                Toast.makeText(this, "Dosya okuma izni gereklidir. Uygulamanın bazı özellikleri çalışmayabilir.", Toast.LENGTH_LONG).show();
                // Kullanıcıya neden izin gerektiği açıklanabilir veya uygulama işlevselliği kısıtlanabilir.
            }
        }
    }
}
