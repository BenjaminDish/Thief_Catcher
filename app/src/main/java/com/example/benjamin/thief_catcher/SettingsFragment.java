package com.example.benjamin.thief_catcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SeekBarPreference _seekBarPref;
    private EditTextPreference messageAlarme;
    private SeekBarListPreference delaiActivation;
    private SeekBarListPreference delaiDeclenchement;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference("slider_mouvement");
        messageAlarme = (EditTextPreference) this.findPreference("text_alarme");
        delaiActivation = (SeekBarListPreference) this.findPreference("slider_activation");
        delaiDeclenchement = (SeekBarListPreference) this.findPreference("slider_déclenchement");





        // Sensibilité mouvement summary :
        int sensibilite = sharedPref.getInt("slider_mouvement", 50);
        _seekBarPref.setSummary(this.getString(R.string.pref_slider_mouvement_summary).replace("$1", "" + sensibilite));

        //Message alarme summary
        String text = sharedPref.getString("text_alarme", "voleur");
        messageAlarme.setSummary(this.getString(R.string.pref_texte_alarme_summary ).replace("$1", "" + text));

        //Delai activation summary
        String delai1 = sharedPref.getString("slider_activation" , "0");
        delaiActivation.setSummary(this.getString(R.string.pref_slider_activation_summary).replace("$1", "" + delai1));

        //Delai declenchement summary
        String delai2 = sharedPref.getString("slider_déclenchement" , "0");
        delaiDeclenchement.setSummary(this.getString(R.string.pref_slider_déclenchement_summary).replace("$1", "" + delai2));

    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        if(key.equals("slider_mouvement")) {
            int radius = sharedPref.getInt("slider_mouvement", 50);
            _seekBarPref.setSummary(this.getString(R.string.pref_slider_mouvement_summary).replace("$1", "" + radius));

        }

        if(key.equals("text_alarme")) {
            String text = sharedPref.getString("text_alarme", "voleur");
            messageAlarme.setSummary(this.getString(R.string.pref_texte_alarme_summary ).replace("$1", "" + text));
        }

        if(key.equals("slider_activation")) {
            String delai1 = sharedPref.getString("slider_activation", "0");
            delaiActivation.setSummary(this.getString(R.string.pref_slider_activation_summary ).replace("$1", "" + delai1));
        }

        if(key.equals("slider_déclenchement")) {
            String delai2 = sharedPref.getString("slider_déclenchement" , "0");
            delaiDeclenchement.setSummary(this.getString(R.string.pref_slider_déclenchement_summary).replace("$1", "" + delai2));
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);



        View v = inflater.inflate(R.layout.settinglayout, container, false);


        return v;
    }

}