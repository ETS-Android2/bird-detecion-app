package com.project.birddetection;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class BirdDetectionActivity extends AppCompatActivity implements Dialog.dialogListener{

    private static int MICROPHONE_PERMISSION_CODE = 200;
    private  int REQ_MP3= 21;

    private String postUrl;

    Button btnRecord;
    Button btnStop;
    TextView timerText;
    TextView statusText;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        statusText = (TextView)findViewById(R.id.statusTextView);
        timerText = (TextView)findViewById(R.id.timeCounter);
        btnRecord = (Button)findViewById(R.id.roundedButton);
        btnStop = (Button)findViewById(R.id.stopButton);
        btnStop.setClickable(false);
        timer = new Timer();
        openDialog();

        if(isMicrophonePresent()){

            getPermission();
        }
    }

    //Método para gravar o audio
    public void recordPressed(View v){

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(recordedFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            startTimer(v);
            btnRecord.setBackgroundResource(R.drawable.rounded_button);
            btnRecord.setText("Gravando...");
            btnRecord.setClickable(false);
            btnStop.setClickable(true);
            statusText.setText("Gravação iniciada, para interromper pressione Parar.");
            System.out.println("Gravação iniciada");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    //Método para parar a gravação do audio
    public void stopPressed(View v){
        btnStop.setClickable(false);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        stopTimer(v);
        btnRecord.setClickable(true);
        btnRecord.setText("Gravar");
        btnRecord.setBackgroundResource(R.drawable.background_light_blue);
        statusText.setText("Gravação interrompida, em instantes traremos o resultado!");
        System.out.println("Gravação interrompida");

        try {

            postJson(v);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

//    //Método para reproduzir a gravação feita
//    public void playPressed(View v){
//
//        try {
//
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(recordedFilePath());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//
//            Toast.makeText(this, "Reproduzindo gravação", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//    }

    //Método para verificar se o microphone esta ativo
    private boolean isMicrophonePresent(){
        //Verifica se o sistema possui microfone
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){

            return true;
        }

        else{

            return false;
        }
    }
    //Método para pegar a autorização para usar o microfone
    private void getPermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    //Método para definir onde o arquivo de audio vai ser gravado
    private String recordedFilePath() throws IOException {

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        File musicDrectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        File file = new File(musicDrectory, "recordedFile" + ".mp3");

        return file.getPath();
    }

    //Método para transformar o arquivo em um array de bytes
    private static String getBytes(File file) throws  IOException {

        byte[] buffer = new byte[1024];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        FileInputStream fis = new FileInputStream(file);

        int read;

        while ((read = fis.read(buffer)) != -1){

            baos.write(buffer, 0, read);
        }

        fis.close();
        baos.close();

        //Transforma o byte array gerado em base 64
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public void postJson(View v) throws IOException {
        String value =  recordedFilePath();

        String ended = getBytes(new File(value));

        //Log.v("Base64 gerado:" ,ended);

        try {

            Audio audio = new Audio();

            audio.setAudio(ended);
            audio.setFormat("mp3");

            JSONObject jsonObj = new JSONObject();

            jsonObj.put("Audio", audio.getAudio());
            jsonObj.put("Format", audio.getFormat());

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            System.out.println("URL post:" + postUrl);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObj, new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response){

                    try {

                      String imageUrl = response.getString("image");
                      String specie = response.getString("species");

                      Handler handler = new Handler();
                      handler.postDelayed(new Runnable() {
                          @Override
                          public void run() {

                              Intent intent = new Intent(BirdDetectionActivity.this, BirdInfoActivity.class);
                              intent.putExtra("specie", specie);
                              intent.putExtra("image", imageUrl);
                              startActivity(intent);
                              finish();
                          }
                      }, 3000);

                    } catch (JSONException jsonException) {

                        jsonException.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error){

                    error.printStackTrace();
                }
            });

            requestQueue.add(jsonObjectRequest);

        }   catch (JSONException je){

            je.printStackTrace();

        }
    }

    public void startTimer(View v){

        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public String getTimerText(){

        int rounded = (int) Math.round(time);

        int sec = ((rounded % 86400) % 3600) %60;
        int min = ((rounded % 86400) % 3600)/60;


        return formatTime(sec, min);
    }

    public String formatTime(int sec, int min){

        return String.format("%02d",min) + " : " + String.format("%02d",sec);
    }

    public void stopTimer(View view)
    {

        timerTask.cancel();
    }

    public void resetTimer(View v){

        time = 0.0;
        timerText.setText(formatTime(0,0));
    }

    public void projectInfo(View v){


        Intent intent = new Intent(this,  ProjectInfoActivity.class);
        startActivity(intent);
        finish();
    }

    public void openDialog(){

        Dialog dialog = new Dialog();

        dialog.show(getSupportFragmentManager(), "example dialog");
    }


    @Override
    public String applyText(String ip, String porta) {

        postUrl = "http://" + ip + ":" + porta + "/detect";
        //postUrl = "https://mockbin.org/bin/af8baf33-9139-4d90-a9f0-aebe5f094c68";

        return postUrl;
    }
}
