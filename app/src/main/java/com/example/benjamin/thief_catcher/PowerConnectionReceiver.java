package com.example.benjamin.thief_catcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class PowerConnectionReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(intent.ACTION_BATTERY_CHANGED)) //On agit que si l'etat de la batterie à changé
        {
            if (! LI_isDeviceCharging(intent)) { //Si l'appareil est débranché
                // Déclenchement de l'alarme
                try {
                    Alarm.start(context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**Retourne true si l'appareil est branché, false sinon*/
    private boolean LI_isDeviceCharging(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }
}
