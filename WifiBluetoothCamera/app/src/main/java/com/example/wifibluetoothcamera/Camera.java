package com.example.wifibluetoothcamera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;

public class Camera extends AppCompatActivity {

    Button b5, b6;
    ImageView imageView;
    VideoView videoView;

    ActivityResultLauncher<Intent> imageCaptureLauncher;
    ActivityResultLauncher<Intent> videoCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_camera.xml layout'unun kullanıldığından emin olun
        setContentView(R.layout.activity_camera);

        b5 = findViewById(R.id.button5);
        b6 = findViewById(R.id.button6);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        // Resim çekme sonucunu işleyen ActivityResultLauncher
        imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                if (imageBitmap != null) {
                                    imageView.setImageBitmap(imageBitmap);
                                    imageView.setVisibility(View.VISIBLE); // Sadece ImageView'ı görünür yap
                                } else {
                                    Toast.makeText(Camera.this, "Resim verisi alınamadı.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Camera.this, "Resim verisi alınamadı (extras null).", Toast.LENGTH_SHORT).show();
                            }
                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(Camera.this, "Resim çekme iptal edildi.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Camera.this, "Resim çekme başarısız.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Video çekme sonucunu işleyen ActivityResultLauncher
        videoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri videoUri = result.getData().getData();
                            if (videoUri != null) {
                                videoView.setVideoURI(videoUri);
                                MediaController mediaController = new MediaController(Camera.this);
                                videoView.setMediaController(mediaController);
                                mediaController.setAnchorView(videoView);
                                videoView.setVisibility(View.VISIBLE); // Sadece VideoView'ı görünür yap
                                videoView.requestFocus();
                                videoView.start(); // Videoyu otomatik başlat
                            } else {
                                Toast.makeText(Camera.this, "Video URI alınamadı.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(Camera.this, "Video çekme iptal edildi.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Camera.this, "Video çekme başarısız.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Butonlara tıklama olaylarını ata
        b5.setOnClickListener(v -> captureImage());
        b6.setOnClickListener(v -> captureVideo());
    }

    // Resim çekme Intent'ini başlatan metod
    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Cihazda kamera uygulaması olup olmadığını kontrol et
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "Kamera uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    // Video çekme Intent'ini başlatan metod
    private void captureVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Cihazda video kayıt uygulaması olup olmadığını kontrol et
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            videoCaptureLauncher.launch(takeVideoIntent);
        } else {
            Toast.makeText(this, "Video kayıt uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }
}
