package com.example.benjamin.thief_catcher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ActivationActivity extends Activity {

    private Timer chrono;
    private TimerTask task;
    private Integer activationTime;
    private Boolean useCharge;
    private Boolean useMove;
    private Boolean useSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activation);

        //Initialisation des attributs
        LI_initValues();

        //Initialisation du layout
        String message = "";
        if(useMove){
            message = getString(R.string.contextual_message_activation_move);
        }
        if(useCharge){
            message = message + "\n" + getString(R.string.contextual_message_activation_charge);
        }
        LI_setContextualText(message);
        LI_majTextCompteur();

        //Lancement du décompte
        LI_LaunchCount();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //Si on le bouton retour est préssé, on retourne sur l'activité précédente
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LI_Finish();
        }
         return super.onKeyDown(keyCode, event);
    }

    /** Met à jour l'affichage du temps restant avant l'activation de l'alarme*/
    private void LI_majTextCompteur(){
        TextView textViewCompteur = (TextView) this.findViewById(R.id.textViewCompteur);
        textViewCompteur.setText(activationTime.toString());
    }

    /** Affiche le message dans la zone de texte contextuel*/
    private void LI_setContextualText(String message){
        TextView textViewContextual = (TextView) this.findViewById(R.id.textViewContextual);
        textViewContextual.setText(message);
    }

    /** Récupère les paramètres nécessaires au fonctionnement de cette activity*/
    private void LI_initValues(){
        //Récupération de la valeur du temps d'activation
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        activationTime = sharedPref.getInt("slider_activation", 5);

        //Récupération des variables passées par la mainActivity
        if(getIntent().getExtras() != null){
            useCharge = getIntent().getBooleanExtra("useCharge", false);
            useMove = getIntent().getBooleanExtra("useMove", false);
            useSms = getIntent().getBooleanExtra("useSms", false);
        }
    }

    /** Lance un décompte qui met à jour le temps restant avant l'activation et lance la vue suivante quand le temps est */
    private void LI_LaunchCount(){
        // Création d'une tâche à jour le label du nombre de secondes restantes avant activation de l'alarme
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activationTime > 0) {
                            activationTime--;
                            LI_majTextCompteur();
                        } else {
                            LI_LaunchAlarmActivity();
                        }
                    }
                });
            }
        };

        //On programme cette tâche toutes les secondes
        chrono = new Timer();
        chrono.schedule(task,(long)1000.0,(long)1000.0);
    }

    /**Ouvre la fenêtre de désactivation de l'alarme (alarme activée) et détruit celle-ci*/
    private void LI_LaunchAlarmActivity(){
        Intent alarmActivity = new Intent(ActivationActivity.this, AlarmActivity.class);
        alarmActivity.putExtra("useCharge", useCharge);
        alarmActivity.putExtra("useMove", useMove);
        alarmActivity.putExtra("useSms", useSms);
        startActivity(alarmActivity);
        LI_Finish();
    }

    /** Termine cette activité proprement*/
    private void LI_Finish(){
        task.cancel();
        chrono.cancel();
        this.finish();
    }
}
