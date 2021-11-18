package com.project.birddetection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            //Método para fazer com que a tela inicial dure por 3 segundos e depois mude para tela principal
            public void run() {

                Intent intent = new Intent(MainActivity.this,  BirdDetectionActivity.class);

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}