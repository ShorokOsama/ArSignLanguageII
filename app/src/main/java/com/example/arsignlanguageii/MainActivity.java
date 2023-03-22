package com.example.arsignlanguageii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    Button camBtn;
    EditText editServer;
    static String IPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("OpenCVModule", "OpenCV Loading Status: " + OpenCVLoader.initDebug());

        camBtn = findViewById(R.id.ar_cam_btn);
        editServer = findViewById(R.id.editAddress);

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
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