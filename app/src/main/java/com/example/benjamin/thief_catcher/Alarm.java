package com.example.benjamin.thief_catcher;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Benjamin on 05/11/2015.
 */
public class Alarm {
    MediaPlayer mediaPlayer;

    public void start(Context context){
        mediaPlayer = MediaPlayer.create(context, R.raw.sun);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    public void stop(){
        mediaPlayer.stop();
    }


}
