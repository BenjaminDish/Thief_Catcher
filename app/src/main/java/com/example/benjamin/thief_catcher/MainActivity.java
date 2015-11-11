package com.example.benjamin.thief_catcher;

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

    private ImageButton imageButtonLock;
    private ImageButton imageButtonMove;
    private ImageButton imageButtonSms;
    private ImageButton imageButtonCharge;
    private Boolean useMove = false;
    private Boolean useCharge = false;
    private Boolean useSms = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonMove = (ImageButton) this.findViewById(R.id.imageButtonMove);
        imageButtonCharge = (ImageButton) this.findViewById(R.id.imageButtonCharge);
        imageButtonSms = (ImageButton) this.findViewById(R.id.imageButtonSms);
        imageButtonLock = (ImageButton) this.findViewById(R.id.imageButtonLock);

        /**Initialisation du layout*/
        D_Init_Layout();

        /**Activation des listeners sur les boutons*/
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
        int id = item.getItemId();

        /** Lancement du menu Settings quand on clique sur 'Settings' dans les options de la barre d'application*/
        if (id == R.id.action_settings) {
            LI_LaunchSettingsFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**Fonction de dialogue apellée à l'initilisation des composants*/
    private void D_Init_Layout(){
        useMove = false;
        useCharge = false;
        useSms = false;
        LI_MoveButton_Refresh();
        LI_ChargeButton_Refresh();
        LI_SmsButton_Refresh();
    }

    /**Fonction de dialogue apellée à chaque click sur le bouton Mouvement*/
    private void D_MoveClicked(){
        //Changement d'êtat du bouton (checked/unchecked)
        useMove = !useMove;
        LI_MoveButton_Refresh();
    }

    /**Fonction de dialogue apellée à chaque click sur le bouton Chargeur*/
    private void D_ChargeClicked(){

        // L'appareil est-il branché ?
        boolean isCharging = LI_isDeviceCharging();

        if (isCharging) {
            //Changement d'êtat du bouton (checked/unchecked)
            useCharge = !useCharge;
            LI_ChargeButton_Refresh();
        }
        else {
            //On blocke cette fonctionalité
            useCharge = false;
            LI_ChargeButton_Refresh();

            // On informe l'utilisateur que cette fonctionnalité n'est pas accessible dans l'êtat
            LI_showMessage(getString(R.string.message_charge_disable));
        }
    }

    /**Fonction de dialogue apellée à chaque click sur le bouton Sms*/
    private void D_SmsClicked(){
        if (!LI_canDeviceReceiveSms()) {
            //On blocke cette fonctionalité
            useSms = false;
            LI_SmsButton_Refresh();

            // On informe l'utilisateur que cette fonctionnalité n'est pas accessible dans l'êtat
            LI_showMessage(getString(R.string.message_sms_disable));
        }
        else if(!LI_isActivationMessageChosen())
        {
            LI_showMessage(getString(R.string.message_sms_message_no_choise));
        }
        else
        {
            //Changement d'êtat du bouton (checked/unchecked)
            useSms = !useSms;
            LI_SmsButton_Refresh();
        }
    }

    /**Fonction de dialogue apellée à chaque click sur le bouton Lock*/
    private void D_LockClicked(){
        if (!(useCharge || useMove || useSms)){ //Si aucun moyen de déclenchement n'a été choisi
            LI_showMessage(getString(R.string.message_no_detection_choise));
        }
        else if(!LI_isActivationCodeChosen()) //Si aucun code de désactivation n'a été choisi
        {
            LI_showMessage(getString(R.string.message_no_desactivation_code_choise));
        }
        else //Activation de l'alarme
        {
            LI_LaunchActivationActivity();
        }
    }

    /**Fonction de dialogue qui active le bouton Mouvement si useMove=true, le désactive sinon*/
    private void LI_MoveButton_Refresh(){
        if(useMove){
            imageButtonMove.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
        }
        else
        {
            imageButtonMove.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
        }
    }

    /**Fonction de dialogue qui active le bouton Chargeur si useCharge=true, le désactive sinon*/
    private void LI_ChargeButton_Refresh(){
        if(useCharge){
            imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
        }
        else
        {
            imageButtonCharge.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
        }
    }

    /**Fonction de dialogue qui active le bouton Sms si useSms=true, le désactive sinon*/
    private void LI_SmsButton_Refresh(){
        if(useSms){
            imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.green_button));
        }
        else
        {
            imageButtonSms.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red_button));
        }
    }

    /**Retourne true si l'appareil est branché, false sinon*/
    private boolean LI_isDeviceCharging(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        int chargeState = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean resultat;

        switch (chargeState) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
            case BatteryManager.BATTERY_STATUS_FULL: //Si l'appareil est branché
                resultat = true;
                break;
            default: //Si l'appareil n'est pas branché
                resultat = false;
        }
        return resultat;
    }

    /**Retourne true si l'appareil peut recevoir des sms, false sinon*/
    private boolean LI_canDeviceReceiveSms(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    /**Retourne true si le sms à envoyer pour déclencher l'alarme a été choisi par l'utilisateur, false sinon*/
    private boolean LI_isActivationMessageChosen(){
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return !sharedPref.getString("sms", ("")).equals("");
    }

    /**Retourne true si le code de désactivation a été choisi par l'utilisateur, false sinon*/
    private boolean LI_isActivationCodeChosen(){
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return !sharedPref.getString("pin", ("")).equals("");
    }

    /**Affiche le message à l'utilisateur sous forme de pop-up (toast android)*/
    private void LI_showMessage(String message){
        Toast toast;
        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    /**Ouvre la fenêtre temporaire avant l'activation*/
    private void LI_LaunchActivationActivity(){
        Intent activationIntent = new Intent(this, ActivationActivity.class);
        activationIntent.putExtra("useCharge", useCharge);
        activationIntent.putExtra("useMove", useMove);
        activationIntent.putExtra("useSms", useSms);
        startActivity(activationIntent);
    }

    /**Ouvre la fenêtre de réglage des paramètres*/
    private void LI_LaunchSettingsFragment(){
        Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsActivity);
    }
}
