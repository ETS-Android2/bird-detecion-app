package com.project.birddetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ProjectInfoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_info);
    }

    public void back(View v){

        Intent intent = new Intent(this,  BirdDetectionActivity.class);
        startActivity(intent);
        finish();
    }

}
