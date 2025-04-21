package com.example.ders4212025;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtonClickListener(R.id.buttonAccelerometer, "Accelerometer Sensor", Sensor.TYPE_ACCELEROMETER);
        setupButtonClickListener(R.id.buttonCompass, "Compass Sensor", Sensor.TYPE_MAGNETIC_FIELD + Sensor.TYPE_ACCELEROMETER); // Pusula için özel tip
        setupButtonClickListener(R.id.buttonGyroscope, "Gyroscope Sensor", Sensor.TYPE_GYROSCOPE);
        setupButtonClickListener(R.id.buttonHumidity, "Humidity Sensor", Sensor.TYPE_RELATIVE_HUMIDITY);
        setupButtonClickListener(R.id.buttonLight, "Light Sensor", Sensor.TYPE_LIGHT);
        setupButtonClickListener(R.id.buttonMagnetometer, "Magnetometer Sensor", Sensor.TYPE_MAGNETIC_FIELD);
        setupButtonClickListener(R.id.buttonPressure, "Pressure Sensor", Sensor.TYPE_PRESSURE);
        setupButtonClickListener(R.id.buttonProximity, "Proximity Sensor", Sensor.TYPE_PROXIMITY);
        setupButtonClickListener(R.id.buttonThermometer, "Thermometer Sensor", Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    private void setupButtonClickListener(int buttonId, final String sensorName, final int sensorType) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SensorDetailActivity.class);
                    intent.putExtra(SensorDetailActivity.EXTRA_SENSOR_NAME, sensorName);
                    intent.putExtra(SensorDetailActivity.EXTRA_SENSOR_TYPE, sensorType);
                    startActivity(intent);
                }
            });
        }
    }
}