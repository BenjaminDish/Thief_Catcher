package com.example.benjamin.thief_catcher;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.TimerTask;

/**
 * Created by Benjamin on 05/11/2015.
 */
public class Alarm {
    private static MediaPlayer mediaPlayer;
    private static FrameLayout frameLayout;
    private static TextView textView;
    private static TimerTask task;
    private static Boolean isActive = false;

    public static void start(Context context){
        if(!isActive) {
            frameLayout.setBackgroundColor(0xfff00000);
            mediaPlayer = MediaPlayer.create(context, R.raw.sun);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
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

    public static void initiate(FrameLayout frame, TextView text) {
        frameLayout = frame;
        textView = text;
    }

}
