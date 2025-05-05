package com.example.wifibluetoothcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Wifi extends AppCompatActivity {

    private WifiManager wifiManager;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        toggleButton = findViewById(R.id.toggleButton);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            toggleButton.setChecked(wifiManager.isWifiEnabled());
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Toast.makeText(Wifi.this, "Android 10 ve üzeri sürümlerde uygulama içinden Wifi doğrudan kontrol edilemez. Lütfen Ayarlar menüsünü kullanın.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.Panel.ACTION_WIFI);
                    startActivity(intent);
                    buttonView.postDelayed(() -> {
                        if (wifiManager != null) {
                            buttonView.setChecked(wifiManager.isWifiEnabled());
                        }
                    } , 500);

                } else {
                    if (wifiManager != null) {
                        boolean success = wifiManager.setWifiEnabled(isChecked);
                        if (success) {
                            Toast.makeText(Wifi.this, isChecked ? "Wifi Açıldı" : "Wifi Kapatıldı", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Wifi.this, "Wifi durumu değiştirilemedi.", Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(!isChecked);
                        }
                    } else {
                        Toast.makeText(Wifi.this, "WifiManager alınamadı.", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(!isChecked);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wifiManager != null) {
            // Android Q ve üzeri için Ayarlar panelinden dönüldüğünde durumu güncelle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toggleButton.setChecked(wifiManager.isWifiEnabled());
            }
            // Eski sürümler için de durumu kontrol etmekte fayda var.
            else {
                toggleButton.setChecked(wifiManager.isWifiEnabled());
            }
        }
    }
}
