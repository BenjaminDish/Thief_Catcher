package com.example.benjamin.thief_catcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SeekBarPreference _seekBarPref;
    private SeekBarListPreference _seekBarListPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        //setHasOptionsMenu(true);

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference("SEEKBAR_VALUE");
        _seekBarListPref = (SeekBarListPreference) this.findPreference("time");

        // Set listener :
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


        // Set seekbar summary :
        int radius1 = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);

        _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius1));
       // _seekBarListPref.setSummary("Valeur courante : $1 %". replace("$1", "" + radius2));
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Set seekbar summary :
        int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);
        _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", ""+radius));
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);



        View v = inflater.inflate(R.layout.settinglayout, container, false);


        return v;
    }

}