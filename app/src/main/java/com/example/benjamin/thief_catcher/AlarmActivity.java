package com.example.benjamin.thief_catcher;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.benjamin.thief_catcher.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class AlarmActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver;
    private EditTextDialogFragment dial;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private Intent intent;
    private ImageButton imageButtonUnlock;
    Boolean useCharge;
    Boolean useMove;
    Boolean useSms;
    private FrameLayout frameAlarm;
    private TextView message;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm);

        frameAlarm = (FrameLayout) findViewById(R.id.frameAlarm);
        message = (TextView) findViewById(R.id.fullscreen_content);

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



        fragmentManager = getFragmentManager();

        imageButtonUnlock = (ImageButton) this.findViewById(R.id.imageButtonUnlock);
        imageButtonUnlock.setImageResource(R.drawable.unlockicon);

        imageButtonUnlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_UnLockClicked();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void D_UnLockClicked() {

        dial = new EditTextDialogFragment();
        dial.show(fragmentManager, "edit");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Alarm.initiate(frameAlarm,message);

        //Récupération des variales passées par la mainActivity
        if(getIntent().getExtras() != null){
            useCharge = getIntent().getBooleanExtra("useCharge", false);
            useMove = getIntent().getBooleanExtra("useMove", false);
            useSms = getIntent().getBooleanExtra("useSms", false);
        }

        // Ecoute des sms reçus
        if(useSms){
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            mReceiver = new SMSReceiver();
            this.registerReceiver(mReceiver, intentFilter);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister our receiver
        if(useSms) {
            this.unregisterReceiver(this.mReceiver);
        }
    }
}
