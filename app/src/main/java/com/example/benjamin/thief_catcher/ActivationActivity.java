package com.example.benjamin.thief_catcher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.benjamin.thief_catcher.util.SystemUiHider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ActivationActivity extends Activity {

    Timer chrono = new Timer();
    TimerTask task;
    Integer activationTime;
    TextView textViewCompteur;
    SharedPreferences sharedPref;
    Boolean useCharge;
    Boolean useMove;
    Boolean useSms;
    private TextView messageMove;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        activationTime = Integer.parseInt(sharedPref.getString("slider_activation", "5"));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_activation);

        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        //Récupération des variales passées par la mainActivity
        if(getIntent().getExtras() != null){
            useCharge = getIntent().getBooleanExtra("useCharge", false);
            useMove = getIntent().getBooleanExtra("useMove", false);
            useSms = getIntent().getBooleanExtra("useSms", false);
        }

        messageMove = (TextView) this.findViewById(R.id.messageMove);
        if(useMove){
            messageMove.setText("Posez votre appareil");
        }

        textViewCompteur = (TextView) this.findViewById(R.id.textViewCompteur);
        majTextCompteur();

        // Met à jour le label du nombre de secondes restantes avant activation de l'alarme
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activationTime > 0) {
                            activationTime--;
                            majTextCompteur();
                        } else {
                            Intent thirdActivity = new Intent(ActivationActivity.this, AlarmActivity.class);
                            thirdActivity.putExtra("useCharge", useCharge);
                            thirdActivity.putExtra("useMove", useMove);
                            thirdActivity.putExtra("useSms", useSms);
                            startActivity(thirdActivity);
                            chrono.cancel();
                            ActivationActivity.this.finish();
                        }
                    }
                });
            }
        };

        //On programme cette tâche toutes les secondes
        chrono.schedule(task,(long)1000.0,(long)1000.0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            task.cancel();
            chrono.cancel();
            this.finish();
        }
         return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void majTextCompteur(){
        textViewCompteur.setText(activationTime.toString());
    }
}
