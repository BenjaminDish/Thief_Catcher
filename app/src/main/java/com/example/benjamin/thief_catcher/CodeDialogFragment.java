package com.example.benjamin.thief_catcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CodeDialogFragment extends DialogFragment {

    private View layout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater factory = LayoutInflater.from(this.getActivity());
        layout = factory.inflate(R.layout.code_dialog_layout, null);

        return LI_InitBuilder();
    }

    //Fonction appellée en cas de click sur le bouton confirmer
    private void D_PositiveButtonClicked(){
        if (LI_isCodeRight()) {
            LI_BackMainActivity();
            Alarm.stop();
        }
    }

    //Fonction appellée en cas de click sur le bouton annuler
    private void D_NegativeButtonClicked(){
        //Inutile pour l'instant mais doit être implémentée
    }

    //Renvoie true si le code entré est correct, false sinon
    private boolean LI_isCodeRight(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        EditText text  = (EditText) layout.findViewById(R.id.edit);

        String codeEntre = text.getText().toString();
        String codePref = sharedPref.getString("pin", "1234");
        return codeEntre.equals(codePref);
    }

    /**Retourne à l'activité d'accueil et détruit celle-ci*/
    private void LI_BackMainActivity(){
        Intent alarmActivity = new Intent(this.getActivity(), MainActivity.class);
        alarmActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(alarmActivity);
    }

    //Initialise la vue et le comportement du builder
    private Dialog LI_InitBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(layout);
        builder.setMessage("Entrez votre code");
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                D_PositiveButtonClicked();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                D_NegativeButtonClicked();
            }
        });

        return builder.create();
    }
}