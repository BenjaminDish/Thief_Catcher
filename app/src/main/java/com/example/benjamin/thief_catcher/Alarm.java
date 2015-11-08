package com.example.benjamin.thief_catcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.TimerTask;

public class Alarm {
    private static MediaPlayer mediaPlayer;
    private static FrameLayout frameLayout;
    private static TextView textView;
    private static TimerTask task;
    private static Boolean isActive = false;
    private static SharedPreferences sharedPref;
    private static Integer alarmDelay;

    public static void start(Context context) throws InterruptedException {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if(!isActive) {
            textView.setText(sharedPref.getString("text_alarm", "Ce portable a été volé"));
            Thread.sleep(alarmDelay * 1000);
            frameLayout.setBackgroundColor(0xfff00000);

            mediaPlayer = MediaPlayer.create(context, R.raw.alarme);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isActive = true;
        }
    }

    public static void stop(){
        if(isActive) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            isActive = false;
        }
    }

    public static void initiate(FrameLayout frame, TextView text, SharedPreferences sharedPref) {
        frameLayout = frame;
        textView = text;
        alarmDelay = Integer.parseInt(sharedPref.getString("slider_déclenchement", "0"));
    }

    public static boolean getStatus(){
        return isActive;
    }

}
