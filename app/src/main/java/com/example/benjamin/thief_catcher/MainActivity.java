package com.example.benjamin.thief_catcher;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {



    ImageButton imageButtonLock;
    ToggleButton toggleButtonMove;
    ToggleButton toggleButtonSms;
    ToggleButton toggleButtonCharge;
    public Boolean useMove = false;
    public Boolean useCharge = false;
    public Boolean useSms = false;
    private Toast toast;
    private SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);



        toggleButtonMove = (ToggleButton) this.findViewById(R.id.toggleButtonMove);
        toggleButtonSms = (ToggleButton) this.findViewById(R.id.toggleButtonSms);
        toggleButtonCharge = (ToggleButton) this.findViewById(R.id.toggleButtonCharge);
        imageButtonLock = (ImageButton) this.findViewById(R.id.imageButtonLock);

        imageButtonLock.setImageResource(R.drawable.unlockicon);

        ImageSpan imageSpanMoveRed = new ImageSpan(this, R.drawable.moveiconred);
        ImageSpan imageSpanMoveGreen = new ImageSpan(this, R.drawable.moveicongreen);
        SpannableString contentMoveRed = new SpannableString("X");
        SpannableString contentMoveGreen = new SpannableString("X");
        contentMoveRed.setSpan(imageSpanMoveRed, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentMoveGreen.setSpan(imageSpanMoveGreen, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleButtonMove.setText(contentMoveRed);
        toggleButtonMove.setTextOn(contentMoveGreen);
        toggleButtonMove.setTextOff(contentMoveRed);

        ImageSpan imageSpanSmsRed = new ImageSpan(this, R.drawable.smsiconred);
        ImageSpan imageSpanSmsGreen = new ImageSpan(this, R.drawable.smsicongreen);
        SpannableString contentSmsRed = new SpannableString("X");
        SpannableString contentSmsGreen = new SpannableString("X");
        contentSmsRed.setSpan(imageSpanSmsRed, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentSmsGreen.setSpan(imageSpanSmsGreen, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleButtonSms.setText(contentSmsRed);
        toggleButtonSms.setTextOn(contentSmsGreen);
        toggleButtonSms.setTextOff(contentSmsRed);

        ImageSpan imageSpanChargeRed = new ImageSpan(this, R.drawable.chargeiconred);
        ImageSpan imageSpanChargeGreen = new ImageSpan(this, R.drawable.chargeicongreen);
        SpannableString contentChargeRed = new SpannableString("X");
        SpannableString contentChargeGreen = new SpannableString("X");
        contentChargeRed.setSpan(imageSpanChargeRed, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentChargeGreen.setSpan(imageSpanChargeGreen, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        toggleButtonCharge.setText(contentChargeRed);
        toggleButtonCharge.setTextOn(contentChargeGreen);
        toggleButtonCharge.setTextOff(contentChargeRed);

        toggleButtonMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                D_MoveChanged(isChecked);
            }
        }) ;
        toggleButtonCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                D_ChargeChanged(isChecked);
            }
        }) ;
        toggleButtonSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                D_SmsChanged(isChecked);
            }
        }) ;
        imageButtonLock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_LockClicked();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Le premier paramètre est le nom de l'activité actuelle
            // Le second est le nom de l'activité de destination
            Intent secondeActivite = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(secondeActivite);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void D_MoveChanged(Boolean isChecked){
        useMove = isChecked;
    }

    private void D_ChargeChanged(Boolean isChecked){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);

        int chargeState = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        switch (chargeState) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
            case BatteryManager.BATTERY_STATUS_FULL:
                useCharge = isChecked;
                // Le téléphone est branché

                break;
            default:
                toggleButtonCharge.setChecked(false);

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, "Branchez le téléphone pour pouvoir choisir ce mode de déclenchement.", duration);
                toast.show();
        }


    }
    private void D_SmsChanged(Boolean isChecked){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            useSms = isChecked;
        } else {
            toggleButtonSms.setChecked(false);

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            toast = Toast.makeText(context, "Votre appareil ne peut pas recevoir de SMS.", duration);
            toast.show();
        }

    }
    private void D_LockClicked(){
        Intent activationIntent = new Intent(this, ActivationActivity.class);
        activationIntent.putExtra("useCharge", useCharge);
        activationIntent.putExtra("useMove", useMove);
        activationIntent.putExtra("useSms", useSms);

        //On regarde si au moins un mode de déclenchement est séléctionné
        if (useCharge || useMove || useSms){

            //On regarde si l'utilisateur à choisit un code de désactivation
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if(!sharedPref.getString("pin", ("-1")).equals("-1")) {
                        startActivity(activationIntent);
            }
            else{
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, "Vous devez choisir un code de désactivation.", duration);
                toast.show();
            }
        }
        else{
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            toast = Toast.makeText(context, "Choisissez un mode de déclenchement.", duration);
            toast.show();
        }
    }

}
