// app/src/main/java/com/example/ebookreader/fragments/SettingsFragment.java
package com.example.ebookreader.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ebookreader.R;
import com.example.ebookreader.managers.SettingsManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private SettingsManager settingsManager;

    private RadioGroup radioGroupFontSize;
    private RadioButton radioButtonSmall, radioButtonMedium, radioButtonLarge;
    private SwitchMaterial switchDarkMode;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Context null değilse SettingsManager'ı başlat
        if (getContext() != null) {
            settingsManager = SettingsManager.getInstance(requireContext());
        } else {
            Log.e(TAG, "onCreate: Context is null, SettingsManager cannot be initialized early.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // UI elemanlarını bul
        radioGroupFontSize = view.findViewById(R.id.radioGroupFontSize);
        radioButtonSmall = view.findViewById(R.id.radioButtonSmall);
        radioButtonMedium = view.findViewById(R.id.radioButtonMedium);
        radioButtonLarge = view.findViewById(R.id.radioButtonLarge);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // SettingsManager null ise (örneğin onCreate'de context null idiyse) tekrar başlatmayı dene
        if (settingsManager == null && getContext() != null) {
            settingsManager = SettingsManager.getInstance(requireContext());
        }

        // SettingsManager başarıyla başlatıldıysa ayarları yükle ve listener'ları kur
        if (settingsManager != null) {
            loadCurrentSettings();
            setupListeners();
        } else {
            Log.e(TAG, "onCreateView: SettingsManager is null. Settings cannot be loaded or saved.");
            // Kullanıcıya bilgi ver (context null değilse)
            if(getContext() != null) {
                Toast.makeText(getContext(), "Ayarlar yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    /**
     * Mevcut ayarları SharedPreferences'tan yükler ve UI elemanlarına yansıtır.
     */
    private void loadCurrentSettings() {
        if (settingsManager == null) {
            Log.e(TAG, "loadCurrentSettings: SettingsManager is null.");
            return;
        }

        // Yazı tipi boyutu ayarını yükle
        float currentMultiplier = settingsManager.getFontSizeMultiplier();
        if (radioButtonSmall != null && currentMultiplier == SettingsManager.FONT_SIZE_SMALL) {
            radioButtonSmall.setChecked(true);
        } else if (radioButtonLarge != null && currentMultiplier == SettingsManager.FONT_SIZE_LARGE) {
            radioButtonLarge.setChecked(true);
        } else { // Varsayılan veya FONT_SIZE_MEDIUM
            if (radioButtonMedium != null) radioButtonMedium.setChecked(true);
        }
        Log.d(TAG, "Yüklü yazı tipi çarpanı: " + currentMultiplier);

        // Karanlık mod ayarını yükle
        if (switchDarkMode != null) {
            switchDarkMode.setChecked(settingsManager.isDarkModeEnabled());
            Log.d(TAG, "Yüklü karanlık mod durumu: " + settingsManager.isDarkModeEnabled());
        }
    }

    /**
     * UI elemanları için değişiklik dinleyicilerini ayarlar.
     */
    private void setupListeners() {
        if (settingsManager == null) {
            Log.e(TAG, "setupListeners: SettingsManager is null. Listeners cannot be set up.");
            return;
        }

        if (radioGroupFontSize != null) {
            radioGroupFontSize.setOnCheckedChangeListener((group, checkedId) -> {
                if (getContext() == null) return; // Context kontrolü
                float selectedMultiplier = SettingsManager.FONT_SIZE_MEDIUM; // Varsayılan
                if (checkedId == R.id.radioButtonSmall) {
                    selectedMultiplier = SettingsManager.FONT_SIZE_SMALL;
                } else if (checkedId == R.id.radioButtonLarge) {
                    selectedMultiplier = SettingsManager.FONT_SIZE_LARGE;
                }
                settingsManager.setFontSizeMultiplier(selectedMultiplier);
                Log.d(TAG, "Yazı tipi çarpanı ayarlandı: " + selectedMultiplier);
                Toast.makeText(getContext(), "Yazı tipi boyutu ayarlandı.", Toast.LENGTH_SHORT).show();
                // EPUB okuyucuda anında etki için bir mekanizma eklenebilir (örn: event bus veya ViewModel)
            });
        }

        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (getContext() == null) return; // Context kontrolü
                settingsManager.setDarkModeEnabled(isChecked);
                Log.d(TAG, "Karanlık mod ayarlandı: " + isChecked);
                Toast.makeText(getContext(), isChecked ? "Karanlık Mod Aktif" : "Karanlık Mod Pasif", Toast.LENGTH_SHORT).show();

                // Temayı anında uygula
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                // Değişikliğin tüm uygulamada geçerli olması için aktiviteyi yeniden başlatmak en garanti yoldur,
                // ancak kullanıcı deneyimini kesintiye uğratabilir.
                // if (getActivity() != null) {
                //     getActivity().recreate(); // Bu satır ayarların hemen uygulanmasını sağlar ama aktiviteyi yeniden başlatır.
                // }
            });
        }
    }
}
