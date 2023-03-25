package com.example.arsignlanguageii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.opencv.android.OpenCVLoader;

public class ListActivity extends AppCompatActivity {
    Button cameraBtn,bankBtn,hospitalBtn,shopBtn,stationBtn;
    EditText editServer;
    static Integer category;
    static String IPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        Log.d("OpenCVModule", "OpenCV Loading Status: " + OpenCVLoader.initDebug());

        cameraBtn = findViewById(R.id.ar_words_btn);
        editServer = findViewById(R.id.editAddress);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 1;
                Intent intent = new Intent(ListActivity.this, CameraActivity.class);
                IPAddress = editServer.getText().toString();
                startActivity(intent);
            }
        });
        bankBtn = findViewById(R.id.ar_bank_btn);

        bankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 2;
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                IPAddress = editServer.getText().toString();
                startActivity(intent);

            }
        });
        hospitalBtn = findViewById(R.id.ar_hospital_btn);
        hospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 3;
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                IPAddress = editServer.getText().toString();
                startActivity(intent);

            }
        });
        shopBtn = findViewById(R.id.ar_shop_btn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 4;
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                IPAddress = editServer.getText().toString();
                startActivity(intent);

            }
        });
        stationBtn = findViewById(R.id.ar_station_btn);
        stationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 5;
                Intent intent = new Intent( ListActivity.this, CameraActivity.class);
                IPAddress = editServer.getText().toString();
                startActivity(intent);

            }
        });


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if (hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}