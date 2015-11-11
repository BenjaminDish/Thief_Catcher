package com.example.benjamin.thief_catcher.DetectionModes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

import com.example.benjamin.thief_catcher.View.Alarm;

public class SMSReceiver extends BroadcastReceiver
{
    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (!Alarm.getStatus()) //Si l'alarme n'est pas déjà activée
        {
            if (intent.getAction().equals(ACTION_RECEIVE_SMS)) //On agit que si on a bien reçu un sms
            {
                if (LI_isSmsActivation(context, intent)) { //Si le sms reçu est le bon
                    // Déclenchement de l'alarme
                    try {
                        Alarm.start(context);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** Retourne true si le message reçu est le sms d'activation, false sinon*/
    private boolean LI_isSmsActivation(Context context, Intent intent){
        boolean resultat = false;

        //Récupération du message d'activation dans les settings
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
        String smsMessage = sharedPref.getString("sms", "ring").concat(sharedPref.getString("pin", "1234"));

        //On teste si le sms reçu est le même que celui des settings
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            if (messages.length > -1)
            {
                final String messageBody = messages[0].getMessageBody();

                if(messageBody.equals(smsMessage)){
                   resultat = true;
                }
            }
        }
        return resultat;
    }
}