package com.project.birddetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE = 200;

    Button btnRecord;
    Button btnStop;
    TextView timerText;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerText = (TextView)findViewById(R.id.timeCounter);
        btnRecord = (Button)findViewById(R.id.roundedButton);
        btnStop = (Button)findViewById(R.id.stopButton);
        btnStop.setClickable(false);
        timer = new Timer();
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
            btnRecord.setClickable(false);
            btnStop.setClickable(true);


            //Toast.makeText(this, "Gravação iniciada", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, "Gravação encerrada", Toast.LENGTH_SHORT).show();
        System.out.println("Gravação interrompida");
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

            System.out.println(jsonObj);

            String postUrl = "http://localhost:5000/detect";

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObj, new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response){

                    System.out.println(response);
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
        int hr = ((rounded % 86400) / 3600);

        return formatTime(sec, min, hr);
    }

    public String formatTime(int sec, int min, int hr){

        return String.format("%02d",hr) + " : " + String.format("%02d",min) + " : " + String.format("%02d",sec);
    }

    public void stopTimer(View view)
    {

        timerTask.cancel();
    }

    public void resetTimer(View v){

        time = 0.0;
        timerText.setText(formatTime(0,0,0));

        Intent intent = new Intent(this, BirdInfoActivity.class);
        startActivity(intent);

    }
}