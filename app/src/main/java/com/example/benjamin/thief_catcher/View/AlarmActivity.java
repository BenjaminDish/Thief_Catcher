package com.example.benjamin.thief_catcher.View;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.benjamin.thief_catcher.DetectionModes.MoveListener;
import com.example.benjamin.thief_catcher.DetectionModes.PowerConnectionReceiver;
import com.example.benjamin.thief_catcher.DetectionModes.SMSReceiver;
import com.example.benjamin.thief_catcher.CustomWidgets.CodeDialogFragment;
import com.example.benjamin.thief_catcher.R;

import java.util.TimerTask;

public class AlarmActivity extends AppCompatActivity{

    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver chargeReceiver;
    private MoveListener moveListener;
    private Boolean useCharge;
    private Boolean useMove;
    private Boolean useSms;
    private ImageButton imageButtonUnlock;
    private Integer couleur_fond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm);

        imageButtonUnlock = (ImageButton) this.findViewById(R.id.imageButtonUnlock);

        //Initialisation des attributs
        LI_initValues();

        //Initialisation de l'alarme
        LI_Init_Alarm();

        //Abonnement aux receivers de déclenchement de l'alarme
        LI_initReceivers();

        /**Activation du listener sur le bouton*/
        imageButtonUnlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_UnLockClicked();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Désabonnement aux receivers
        LI_finishReceivers();
    }

    /** Méthode appellée sur pression d'un des boutons de l'appareil*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        /** Désactivation du bouton back*/
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        /** Désactivation des boutons de réglage du volume sonore si l'alarme est déclenchée*/
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) && Alarm.getStatus()) {
            AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** Fonction appellée en cas de click du bouton Unlock*/
    private void D_UnLockClicked() {
        LI_OpenCodeKeyboard();
    }

    /** Initialise la classe static alarme avec les bons paramètres*/
    private void LI_Init_Alarm(){
        final FrameLayout frameAlarm = (FrameLayout) findViewById(R.id.frameAlarm);
        final TextView textViewMessage = (TextView) findViewById(R.id.textViewMessage);

        // Création d'une tâche qui gère le clignotement rouge/bleu
        couleur_fond = ContextCompat.getColor(this, R.color.blue);
        TimerTask taskClignotement = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (couleur_fond.equals(ContextCompat.getColor(AlarmActivity.this, R.color.blue))) {
                            frameAlarm.setBackgroundColor(ContextCompat.getColor(AlarmActivity.this, R.color.red));
                            imageButtonUnlock.setBackgroundColor(ContextCompat.getColor(AlarmActivity.this, R.color.red));
                            couleur_fond = ContextCompat.getColor(AlarmActivity.this, R.color.red);
                        }
                        else
                        {
                            frameAlarm.setBackgroundColor(ContextCompat.getColor(AlarmActivity.this, R.color.blue));
                            imageButtonUnlock.setBackgroundColor(ContextCompat.getColor(AlarmActivity.this, R.color.blue));
                            couleur_fond = ContextCompat.getColor(AlarmActivity.this, R.color.blue);
                        }
                    }
                });
            }
        };

        // Initilisation de l'objet alarme avec cette tâche
        Alarm.initiate(taskClignotement, textViewMessage, this);
    }

    /** Récupère les paramètres nécessaires au fonctionnement de cette activity*/
    private void LI_initValues() {
        //Récupération des variables passées par la ActivationActivity
        if(getIntent().getExtras() != null){
            useCharge = getIntent().getBooleanExtra("useCharge", false);
            useMove = getIntent().getBooleanExtra("useMove", false);
            useSms = getIntent().getBooleanExtra("useSms", false);
        }
    }

    /** Abonne l'activity aux receivers sur les évènements de déclenchement de l'alarme*/
    private void LI_initReceivers(){
        // Ecoute des sms reçus
        if(useSms){
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            smsReceiver = new SMSReceiver();
            this.registerReceiver(smsReceiver, intentFilter);
        }
        // Ecoute de l'évènement "débranchement du téléphone"
        if(useCharge){
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            chargeReceiver = new PowerConnectionReceiver();
            this.registerReceiver(chargeReceiver, intentFilter);
        }
        // Ecoute de l'évènement "mouvement du téléphone"
        if(useMove) {
            SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            moveListener = new MoveListener(this, mSensorManager);
        }
    }

    /** Désabonne l'activity aux receivers sur les évènements de déclenchement de l'alarme*/
    private void LI_finishReceivers(){
        if(useSms) {
            this.unregisterReceiver(this.smsReceiver);
        }
        if(useCharge) {
            this.unregisterReceiver(this.chargeReceiver);
        }
    }

    /** Ouvre une fenêtre avec clavier numérique qui permet de taper le code de désactivation*/
    private void LI_OpenCodeKeyboard(){
        FragmentManager fragmentManager = getFragmentManager();
        CodeDialogFragment dial = new CodeDialogFragment();
        dial.show(fragmentManager, "edit");
    }
}
