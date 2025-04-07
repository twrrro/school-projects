package com.example.yenivizeoncesivize;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private Button btnStart, btnConfirm;
    private SeekBar seekBar;
    private EditText editText;
    private int selectedPlaka = 1;
    private HashMap<Integer, String> plakaMap;
    public static ArrayList<String> validEntries = new ArrayList<>();
    public static final int MAX_ENTRIES = 3;

    private TextView txtPlaka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPlaka = findViewById(R.id.txtPlaka);
        btnStart = findViewById(R.id.btnStart);
        btnConfirm = findViewById(R.id.btnConfirm);
        seekBar = findViewById(R.id.seekBar);
        editText = findViewById(R.id.editText);

        seekBar.setMax(80); // çünkü 0-80 = 1-81
        plakaMap = getPlakaMap();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedPlaka = progress + 1;
                txtPlaka.setText("Plaka: " + selectedPlaka);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        btnStart.setOnClickListener(v -> {
            Toast.makeText(this, "Oyuna başla gardas!", Toast.LENGTH_SHORT).show();
        });

        btnConfirm.setOnClickListener(v -> {
            String userInput = editText.getText().toString().trim().toLowerCase();
            String correctCity = plakaMap.get(selectedPlaka);

            if (correctCity != null && userInput.equals(correctCity.toLowerCase())) {
                validEntries.add(capitalize(correctCity) + " 5 saniye");
                Toast.makeText(this, "Doğru emmi! ➕", Toast.LENGTH_SHORT).show();
                editText.setText("");

                if (validEntries.size() == MAX_ENTRIES) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    startActivity(intent);
                    finish();
                }

            } else {
                Toast.makeText(this, "Yanlış kardeşim! Bu " + correctCity + "’du", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<Integer, String> getPlakaMap() {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "Adana");
        map.put(2, "Adıyaman");
        map.put(3, "Afyonkarahisar");
        map.put(4, "Ağrı");
        map.put(5, "Amasya");
        map.put(6, "Ankara");
        map.put(7, "Antalya");
        map.put(8, "Artvin");
        map.put(9, "Aydın");
        map.put(10, "Balıkesir");
        map.put(11, "Bilecik");
        map.put(12, "Bingöl");
        map.put(13, "Bitlis");
        map.put(14, "Bolu");
        map.put(15, "Burdur");
        map.put(16, "Bursa");
        map.put(17, "Çanakkale");
        map.put(18, "Çankırı");
        map.put(19, "Çorum");
        map.put(20, "Denizli");
        map.put(21, "Diyarbakır");
        map.put(22, "Edirne");
        map.put(23, "Elazığ");
        map.put(24, "Erzincan");
        map.put(25, "Erzurum");
        map.put(26, "Eskişehir");
        map.put(27, "Gaziantep");
        map.put(28, "Giresun");
        map.put(29, "Gümüşhane");
        map.put(30, "Hakkari");
        map.put(31, "Hatay");
        map.put(32, "Iğdır");
        map.put(33, "Isparta");
        map.put(34, "İstanbul");
        map.put(35, "İzmir");
        map.put(36, "Kahramanmaraş");
        map.put(37, "Karabük");
        map.put(38, "Karaman");
        map.put(39, "Kars");
        map.put(40, "Kastamonu");
        map.put(41, "Kayseri");
        map.put(42, "Kırıkkale");
        map.put(43, "Kırklareli");
        map.put(44, "Malatya");
        map.put(45, "Manisa");
        map.put(46, "Kahramanmaraş");
        map.put(47, "Mardin");
        map.put(48, "Muğla");
        map.put(49, "Muş");
        map.put(50, "Nevşehir");
        map.put(51, "Niğde");
        map.put(52, "Ordu");
        map.put(53, "Rize");
        map.put(54, "Sakarya");
        map.put(55, "Samsun");
        map.put(56, "Siirt");
        map.put(57, "Sinop");
        map.put(58, "Sivas");
        map.put(59, "Tekirdağ");
        map.put(60, "Tokat");
        map.put(61, "Trabzon");
        map.put(62, "Tunceli");
        map.put(63, "Şanlıurfa");
        map.put(64, "Uşak");
        map.put(65, "Van");
        map.put(66, "Yalova");
        map.put(67, "Yozgat");
        map.put(68, "Zonguldak");
        map.put(69, "Aksaray");
        map.put(70, "Bayburt");
        map.put(71, "Düzce");
        map.put(72, "Karabük");
        map.put(73, "Kırıkkale");
        map.put(74, "Kırklareli");
        map.put(75, "Afşin");
        map.put(76, "Bartın");
        map.put(77, "Bolu");
        map.put(78, "Burhaniye");
        map.put(79, "Edirne");
        map.put(80, "Erzincan");
        map.put(81, "Zonguldak");
        return map;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
