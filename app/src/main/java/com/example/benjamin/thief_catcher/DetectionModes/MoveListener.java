package com.example.benjamin.thief_catcher.DetectionModes;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.example.benjamin.thief_catcher.View.Alarm;

public class MoveListener implements SensorEventListener {

    private Context context;
    private double motionSensibility;
    private SensorManager mSensorManager;

    public MoveListener(Context context, SensorManager mSensorManager){
        this.context = context;
        this.mSensorManager = mSensorManager;

        //Initialisation des valeurs
        LI_InitiateValues();

        //Abonnement au listener de mouvement
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /** Méthode appellée sur détection d'un mouvement du téléphone*/
    @Override
    public final void onSensorChanged(SensorEvent event){
        if (!Alarm.getStatus()) //Si l'alarme n'est pas déjà activée
        {
            if (LI_isMovementAccepted(event)) {
                // Déclenchement de l'alarme
                LI_stopListening();
                try {
                    Alarm.start(context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Cette méthode est inutilisée ici mais doit être implémentée.
    }

    /** Désabonnenement au listener*/
    public void LI_stopListening(){
        mSensorManager.unregisterListener(this);
    }

    /** Evalue si un mouvement est suffisant pour déclencher l'alarme. Retourne true si il est suffisant, false sinon*/
    private boolean LI_isMovementAccepted(SensorEvent event){
        //On récupère les accélérations selon les axes x et y de l'appareil
        float accX = event.values[0];
        float accY = event.values[1];
        //On calcule si le mouvement est assez vif pour déclencher l'alarme (en fonction de la sensibilité)
        return Math.abs(accX)> motionSensibility + 1 || Math.abs(accY)> motionSensibility + 1;
    }

    /** Charge les valeurs nécessaire à cette classe*/
    private void LI_InitiateValues(){
        //Récupération de la sensibilité dans les settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Integer valeur = sharedPref.getInt("slider_mouvement", 50);
        motionSensibility = (float) (100 - valeur) / 10.0;
    }
}
