package com.example.benjamin.thief_catcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditTextDialogFragment extends DialogFragment {
    private EditText text;
    Intent intent;
    private SharedPreferences sharedPref;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        intent = new Intent(this.getActivity(), MainActivity.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater factory = LayoutInflater.from(this.getActivity());

        View layout = factory.inflate(R.layout.dialog_layout, null);

        builder.setView(layout);


        text  = (EditText) layout.findViewById(R.id.edit);


        builder.setMessage("Entrez votre code PIN");

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (text.getText().toString().equals(sharedPref.getString("pin", "1234"))) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }


        });
        builder.create();





        // Create the AlertDialog object and return it
        return builder.create();
    }



}