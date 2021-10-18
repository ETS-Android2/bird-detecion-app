package com.example.birddetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


public class MainActivity extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE = 200;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isMicrophonePresent()){

            getPermission();
        }
    }

    //Método para gravar o audio
    public void recordPressed(View v){

        try {

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordedFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this, "Gravação iniciada", Toast.LENGTH_LONG).show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    //Método para parar a gravação do audio
    public void stopPressed(View v){

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(this, "Gravação encerrada", Toast.LENGTH_LONG).show();

    }

    //Método para reproduzir a gravação feita
    public void playPressed(View v){

        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(recordedFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Reproduzindo gravação", Toast.LENGTH_LONG).show();

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

    public void test(View v) throws IOException {
        String value =  recordedFilePath();

        String ended = getBytes(new File(value));

        System.out.println(ended);
    }
}