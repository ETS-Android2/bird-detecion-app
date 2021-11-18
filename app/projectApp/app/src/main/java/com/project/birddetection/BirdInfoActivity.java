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

    String getUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView birdView = findViewById(R.id.birdImageView);

        String value;

        Bundle bundle = getIntent().getExtras();

        value = bundle.getString("chave");

        getUrl = defineUrl(value);

        System.out.println("valor vindo da outra intent: "+getUrl);

        getBird();

    }

    private void getBird() {

        TextView specie = findViewById(R.id.infoTextView);
        ImageView birdView = findViewById(R.id.birdImageView);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                           String imageUrl = response.getString("image");
                           specie.setText(response.getString("species"));

                           Picasso.get().load(imageUrl).into(birdView);
                        } catch (JSONException jsonException) {

                            jsonException.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    public void returnDetection(View v){

        Intent intent = new Intent(this,  BirdDetectionActivity.class);
        startActivity(intent);
        finish();
    }

    public String defineUrl (String value){

        String end = value;

        return end;
    }

}
