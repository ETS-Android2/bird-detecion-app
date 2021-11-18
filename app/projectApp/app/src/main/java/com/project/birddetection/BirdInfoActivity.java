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

    String aux;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView birdView = findViewById(R.id.birdImageView);
        TextView specieTv = findViewById(R.id.infoTextView);

        Bundle bundleImage = getIntent().getExtras();
        Bundle bundleText = getIntent().getExtras();

        //Recebe informações vindas da tela anterior
        String imageUrl = bundleImage.getString("image");
        String specie = bundleText.getString("specie");

        aux = specie;

        specieTv.setText(specie);
        //Coloca automaticamente a imagem da url
        Picasso.get().load(imageUrl).into(birdView);
    }

    //Método para retornar para a detecção
    public void returnDetection(View v){

        Intent intent = new Intent(this,  BirdDetectionActivity.class);
        startActivity(intent);
        finish();
    }

    //Método para compartilhar o resultado obtido
    public void share(View v){

        Intent intent  = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        String body = "body";
        String sub = "Hey olha que legal, consegui identificar um " + aux + " utilizando o app BirdSpot, caso queira saber mais sobre o projeto entre em: https://shortest.link/1PQc !";
        intent.putExtra(intent.EXTRA_TEXT, body);
        intent.putExtra(intent.EXTRA_TEXT, sub);
        startActivity(intent.createChooser(intent, "Compartilhar"));
    }
}
