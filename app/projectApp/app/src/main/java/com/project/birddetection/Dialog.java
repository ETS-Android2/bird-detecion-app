package com.project.birddetection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

    private EditText editTextIp;
    private EditText editTextPorta;

    private dialogListener listener;

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view).setTitle("IP e Porta").setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

            @Override
                    public void onClick(DialogInterface dialoginterface, int i){
            }

        }).setPositiveButton("Confirmar", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i){

                String ip = editTextIp.getText().toString();
                String porta = editTextPorta.getText().toString();
                listener.applyText(ip, porta);
            }
        });

        editTextIp = view.findViewById(R.id.ipEd);
        editTextPorta = view.findViewById(R.id.portaEd);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (dialogListener) context;
    }

    public interface dialogListener{

        String applyText(String ip, String porta);
    }
}
