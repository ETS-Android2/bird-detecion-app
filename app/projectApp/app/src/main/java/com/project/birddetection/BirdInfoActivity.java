package com.project.birddetection;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView birdView = findViewById(R.id.birdImageView);
        getBird();

    }

    private void getBird() {

        //String getUrl = "http://localhost:5000/detect";
        String getUrl = "http://192.168.0.113:5500/Trabalho1/JSONTesting.json";

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

}
