package com.example.yenivizeoncesivize;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnNew;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = findViewById(R.id.listView);
        btnNew = findViewById(R.id.btnNew);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainActivity.validEntries);
        listView.setAdapter(adapter);

        btnNew.setOnClickListener(v -> {
            Toast.makeText(this, "Yeni oyun başlatılıyor kral!", Toast.LENGTH_SHORT).show();

            MainActivity.validEntries.clear();

            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        });
    }
}
