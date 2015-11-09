package com.example.benjamin.thief_catcher;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    ImageButton imageButtonLock;
    ImageButton imageButtonMove;
    ImageButton imageButtonSms;
    ImageButton imageButtonCharge;
    public Boolean useMove = false;
    public Boolean useCharge = false;
    public Boolean useSms = false;
    private Toast toast;
    private SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonMove = (ImageButton) this.findViewById(R.id.imageButtonMove);
        imageButtonMove.setImageResource(R.drawable.ic_screen_rotation_black);
        imageButtonMove.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));

        imageButtonCharge = (ImageButton) this.findViewById(R.id.imageButtonCharge);
        imageButtonCharge.setImageResource(R.drawable.ic_power_black);
        imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));

        imageButtonSms = (ImageButton) this.findViewById(R.id.imageButtonSms);
        imageButtonSms.setImageResource(R.drawable.ic_message_black);
        imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));

        imageButtonLock = (ImageButton) this.findViewById(R.id.imageButtonLock);
        imageButtonLock.setImageResource(R.drawable.unlockicon);

        imageButtonMove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_MoveClicked();
            }
        });
        imageButtonCharge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_ChargeClicked();
            }
        });
        imageButtonSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_SmsClicked();
            }
        });
        imageButtonLock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                D_LockClicked();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void D_MoveClicked(){
        if (useMove == false){
            useMove = true;
            imageButtonMove.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
        }
        else
        {
            useMove = false;
            imageButtonMove.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
        }
    }
    private void D_ChargeClicked(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);

        int chargeState = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        switch (chargeState) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
            case BatteryManager.BATTERY_STATUS_FULL:
                // Le téléphone est branché
                if (useCharge == false){
                    useCharge = true;
                    imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
                }
                else
                {
                    useCharge = false;
                    imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
                }
                break;
            default:
                useCharge = false;
                imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, "Branchez le téléphone pour pouvoir choisir ce mode de déclenchement.", duration);
                toast.show();
        }


    }
    private void D_SmsClicked(){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (useSms == false){
                useSms = true;
                imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
            }
            else
            {
                useSms = false;
                imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
            }
        } else {
            useSms = false;
            imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));

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
