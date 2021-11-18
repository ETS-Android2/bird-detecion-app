package com.project.birddetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BirdInfoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView birdView = findViewById(R.id.birdImageView);
        TextView specieTv = findViewById(R.id.infoTextView);

        Bundle bundleImage = getIntent().getExtras();
        Bundle bundleText = getIntent().getExtras();

        String imageUrl = bundleImage.getString("image");
        String specie = bundleText.getString("specie");

        System.out.println("Url vinda da main:" + imageUrl + " Especie vinda da main:" + specie);

        specieTv.setText(specie);

        Picasso.get().load(imageUrl).into(birdView);
    }


    public void returnDetection(View v){

        Intent intent = new Intent(this,  BirdDetectionActivity.class);
        startActivity(intent);
        finish();
    }

}
