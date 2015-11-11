package com.example.benjamin.thief_catcher.Preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Affiche le frragmet
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}