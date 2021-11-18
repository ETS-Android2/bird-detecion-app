package com.project.birddetection;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
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

    //Método para mandar o base 64 gerado pela função acima e receber informações do pássaro
    public void postJson(View v) throws IOException {

        String value =  recordedFilePath();
        String ended = getBytes(new File(value));
        try {

            Audio audio = new Audio();

            audio.setAudio(ended);
            audio.setFormat("mp3");

            JSONObject jsonObj = new JSONObject();

            jsonObj.put("Audio", audio.getAudio());
            jsonObj.put("Format", audio.getFormat());

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObj, new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response){

                    try {

                      String imageUrl = response.getString("image");
                      String specie = response.getString("species");

                      Handler handler = new Handler();

                      handler.postDelayed(new Runnable() {

                          //Método para passar as informações obtidas do server para a próxima tela com delay de 3 seg para mudar de tela
                          @Override
                          public void run() {

                              Intent intent = new Intent(BirdDetectionActivity.this, BirdInfoActivity.class);
                              intent.putExtra("specie", specie);
                              intent.putExtra("image", imageUrl);
                              resetTimer(v);
                              startActivity(intent);
                              finish();
                          }
                      }, 3000);

                    } catch (JSONException jsonException) {

                        String text = "Erro no json";
                        Toast toast = Toast.makeText(BirdDetectionActivity.this, text, Toast.LENGTH_LONG);
                        toast.show();
                        jsonException.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error){

                    String text = "Erro no volley";
                    Toast.makeText(BirdDetectionActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        }   catch (JSONException je){

            je.printStackTrace();
        }
    }

    //Método para iniciar o timer
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

    //Método para pegar os segundos e mintos
    public String getTimerText(){

        int rounded = (int) Math.round(time);

        int sec = ((rounded % 86400) % 3600) %60;
        int min = ((rounded % 86400) % 3600)/60;


        return formatTime(sec, min);
    }

    //Método para formatar os segundos
    public String formatTime(int sec, int min){

        return String.format("%02d",min) + " : " + String.format("%02d",sec);
    }

    //Método para prar o timer
    public void stopTimer(View view)
    {

        timerTask.cancel();
    }

    //Método para resetar o tiemr
    public void resetTimer(View v){

        time = 0.0;
        timerText.setText(formatTime(0,0));
    }

    //Método para abrir a tela de informações do projeto
    public void projectInfo(View v){

        Intent intent = new Intent(this,  ProjectInfoActivity.class);
        startActivity(intent);
        finish();
    }

    //Método para abrir a caixa de dialogo para pedir o IP e Porta
    public void openDialog(){

        Dialog dialog = new Dialog();

        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    //Método para aplicar oque foi digitado na caixa de dialogo em variaveis locais
    @Override
    public String applyText(String ip, String porta) {

        postUrl = "http://" + ip + ":" + porta + "/detect";
        //postUrl = "https://mockbin.org/bin/af8baf33-9139-4d90-a9f0-aebe5f094c68";

        return postUrl;
    }
}
