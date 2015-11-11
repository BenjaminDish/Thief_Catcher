package com.example.benjamin.thief_catcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class Alarm {
    private static MediaPlayer mediaPlayer;
    private static TextView textView;
    private static TimerTask taskClignotement;
    private static Timer chronoClign;
    private static Boolean isActive = false;
    private static Integer alarmDelay;

    /** Déclenche l'effet de l'alarme*/
    public static void start(Context context) throws InterruptedException {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(!isActive) {
            isActive = true;

            // Attente du delai de déclenchement de l'alarme
            Thread.sleep(alarmDelay * 1000);

            // Affichage du message
            textView.setText(sharedPref.getString("text_alarme", "Ce portable a été volé"));

            // Lancement du clignotement bleu/rouge
            chronoClign = new Timer();
            chronoClign.schedule(taskClignotement, (long) 0.0, (long) 200.0);

            // Lancement de la musique
            String nomMusic = sharedPref.getString("theme_sonore", "usa");
            int identif = context.getResources().getIdentifier(nomMusic, "raw", context.getPackageName());
            mediaPlayer = MediaPlayer.create(context, identif);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    /** Arrête l'effet de l'alarme */
    public static void stop(){
        if(isActive) {
            isActive = false;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                chronoClign.cancel();
            }
        }
    }

    /** Initialise la classe static avec le widget où afficher le message et la tâche de clignotement à lancer */
    public static void initiate(TimerTask tClign, TextView text, Context context) {
        taskClignotement = tClign;
        textView = text;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        alarmDelay = sharedPref.getInt("slider_déclenchement", 0);
    }

    /** Renvoie true si l'alarme est déclenchée et false sinon*/
    public static boolean getStatus(){
        return isActive;
    }
}
