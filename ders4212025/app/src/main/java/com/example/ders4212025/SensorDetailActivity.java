package com.example.ders4212025;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SensorDetailActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView textViewSensorTitle;
    private TextView textViewSensorValue;

    private int currentSensorType;
    private String sensorName = "Bilinmeyen Sensör";


    private Sensor compassAccelerometerSensor;
    private Sensor compassMagnetometerSensor;
    private float[] accelerometerReading = new float[3];
    private float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private boolean isCompassMode = false;

    public static final String EXTRA_SENSOR_TYPE = "sensor_type";
    public static final String EXTRA_SENSOR_NAME = "sensor_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        textViewSensorTitle = findViewById(R.id.textViewSensorTitle);
        textViewSensorValue = findViewById(R.id.textViewSensorValue);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_SENSOR_TYPE) && intent.hasExtra(EXTRA_SENSOR_NAME)) {
            currentSensorType = intent.getIntExtra(EXTRA_SENSOR_TYPE, -1);
            sensorName = intent.getStringExtra(EXTRA_SENSOR_NAME);
            textViewSensorTitle.setText(sensorName); // Başlığı ayarla


            if (currentSensorType == Sensor.TYPE_MAGNETIC_FIELD + Sensor.TYPE_ACCELEROMETER) { // Pusula için özel bir değer kullandık
                isCompassMode = true;
                compassAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                compassMagnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                if (compassAccelerometerSensor == null || compassMagnetometerSensor == null) {
                    textViewSensorValue.setText("Pusula için gerekli sensörler bulunamadı.");
                    Toast.makeText(this, "Pusula için gerekli sensörler (Accelerometer/Magnetometer) bulunamadı.", Toast.LENGTH_LONG).show();
                }
            } else {
                isCompassMode = false;
                sensor = sensorManager.getDefaultSensor(currentSensorType);
                if (sensor == null) {
                    textViewSensorValue.setText("Bu sensör cihazınızda bulunmuyor.");
                    Toast.makeText(this, sensorName + " bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            textViewSensorTitle.setText("Hata");
            textViewSensorValue.setText("Sensör tipi bilgisi alınamadı.");
            Toast.makeText(this, "Sensör bilgisi aktiviteye gönderilmedi.", Toast.LENGTH_SHORT).show();
            finish(); //
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCompassMode) {

            if (compassAccelerometerSensor != null) {
                sensorManager.registerListener(this, compassAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (compassMagnetometerSensor != null) {
                sensorManager.registerListener(this, compassMagnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else if (sensor != null) {

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isCompassMode) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
            }
            updateOrientationAngles();
            return;
        }


        if (event.sensor.getType() == currentSensorType) {
            float[] values = event.values;
            String valueText = "";

            switch (currentSensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                case Sensor.TYPE_GYROSCOPE:
                case Sensor.TYPE_MAGNETIC_FIELD:

                    valueText = String.format(Locale.getDefault(), "X: %.2f\nY: %.2f\nZ: %.2f",
                            values[0], values[1], values[2]);
                    if (currentSensorType == Sensor.TYPE_ACCELEROMETER) valueText += " m/s²";
                    else if (currentSensorType == Sensor.TYPE_GYROSCOPE) valueText += " rad/s";
                    else valueText += " µT"; // TYPE_MAGNETIC_FIELD
                    break;

                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    valueText = String.format(Locale.getDefault(), "%.1f %%", values[0]);
                    break;

                case Sensor.TYPE_LIGHT:
                    valueText = String.format(Locale.getDefault(), "%.1f lx", values[0]);
                    break;

                case Sensor.TYPE_PRESSURE:
                    valueText = String.format(Locale.getDefault(), "%.2f hPa", values[0]);
                    break;

                case Sensor.TYPE_PROXIMITY:
                    valueText = String.format(Locale.getDefault(), "%.1f cm", values[0]);

                    if (sensor != null && values[0] < sensor.getMaximumRange()) {
                        valueText += " (Yakın)";
                    } else {
                        valueText += " (Uzak)";
                    }
                    break;

                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    valueText = String.format(Locale.getDefault(), "%.1f °C", values[0]);
                    break;

                default:

                    StringBuilder sb = new StringBuilder();
                    for (float value : values) {
                        sb.append(String.format(Locale.getDefault(), "%.2f", value)).append("\n");
                    }
                    valueText = sb.toString().trim();
                    break;
            }
            textViewSensorValue.setText(valueText);
        }
    }

    public void updateOrientationAngles() {
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        if (success) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles);
            float azimuthInDegrees = (float) Math.toDegrees(orientationAngles[0]);
            azimuthInDegrees = (azimuthInDegrees + 360) % 360; // 0-360 aralığına getir

            String compassData = String.format(Locale.getDefault(), "Azimuth (Kuzey): %.1f°", azimuthInDegrees);
            textViewSensorValue.setText(compassData);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}