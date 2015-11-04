package com.example.benjamin.thief_catcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver
{
    private final String   ACTION_RECEIVE_SMS  = "android.provider.Telephony.SMS_RECEIVED";
    private String smsMess;
    SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
        smsMess = sharedPref.getString("sms", "ring").concat(sharedPref.getString("pin","1234"));

        if (intent.getAction().equals(ACTION_RECEIVE_SMS))
        {
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

                    if(messageBody.equals(smsMess)){
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sun);
                        mediaPlayer.start(); // no need to call prepare(); create() does that for you
                    }


                }
            }
        }

    }

}