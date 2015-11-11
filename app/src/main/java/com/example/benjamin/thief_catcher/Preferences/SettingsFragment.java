package com.example.benjamin.thief_catcher.Preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benjamin.thief_catcher.CustomWidgets.SeekBarPreference;
import com.example.benjamin.thief_catcher.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SeekBarPreference sensibiliteSlider;
    private EditTextPreference messageAlarmeEdit;
    private SeekBarPreference delaiActivationSlider;
    private SeekBarPreference delaiDeclenchementSlider;
    private EditTextPreference codeDesactivationEdit;
    private ListPreference sonnerieList;
    private ListPreference smsList;
    private SharedPreferences sharedPref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Charge les preferences depuis le fichier xml
        addPreferencesFromResource(R.xml.pref_general);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        // Get widgets :
        sensibiliteSlider = (SeekBarPreference) this.findPreference("slider_mouvement");
        messageAlarmeEdit = (EditTextPreference) this.findPreference("text_alarme");
        delaiActivationSlider = (SeekBarPreference) this.findPreference("slider_activation");
        delaiDeclenchementSlider = (SeekBarPreference) this.findPreference("slider_déclenchement");
        codeDesactivationEdit = (EditTextPreference) this.findPreference("pin");
        sonnerieList = (ListPreference) this.findPreference("theme_sonore");
        smsList = (ListPreference) this.findPreference("sms");

        //Maj des summary pour toutes les preferences au chargement
        LI_MajSummarySensibilité();
        LI_MajSummeryActivation();
        LI_MajSummeryCode();
        LI_MajSummeryDeclenchement();
        LI_MajSummerySms();
        LI_MajSummerySonnerie();
        LI_MajSummeryMessage();
    }

    //Maj des summary pour toutes les preferences quand elles changent
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        switch(key) {
            case "slider_mouvement":
                LI_MajSummarySensibilité();
                break;
            case "text_alarme":
                LI_MajSummeryMessage();
                break;
            case "slider_activation":
                LI_MajSummeryActivation();
                break;
            case "slider_déclenchement":
                LI_MajSummeryDeclenchement();
                break;
            case "pin":
                LI_MajSummeryCode();
                break;
            case "sms":
                LI_MajSummerySms();
                break;
            case "theme_sonore":
                LI_MajSummerySonnerie();
                break;
            default:
                break;
        }
    }

    //Maj du summary sensibilité du capteur de mouvement
    private void LI_MajSummarySensibilité(){
        int sensibilite = sharedPref.getInt("slider_mouvement", 50);
        sensibiliteSlider.setSummary(this.getString(R.string.pref_slider_mouvement_summary).replace("$1", "" + sensibilite));
    }

    //Maj du summary délai d'activation
    private void LI_MajSummeryActivation(){
        int delaiActivation = sharedPref.getInt("slider_activation", 5);
        delaiActivationSlider.setSummary(this.getString(R.string.pref_slider_activation_summary).replace("$1", "" + delaiActivation));
    }

    //Maj du summary délai de déclenchement
    private void LI_MajSummeryDeclenchement(){
        int delaiDéclenchement = sharedPref.getInt("slider_déclenchement", 5);
        delaiDeclenchementSlider.setSummary(this.getString(R.string.pref_slider_déclenchement_summary).replace("$1", "" + delaiDéclenchement));
    }

    //Maj du summary code de désactivation
    private void LI_MajSummeryCode(){
        String code = sharedPref.getString("pin", "");
        codeDesactivationEdit.setSummary(this.getString(R.string.pref_PIN_summary).replace("$1", "" + code));
    }

    //Maj du summary thème sonnore
    private void LI_MajSummerySonnerie(){
        String sonnerie = sharedPref.getString("theme_sonore", "usa");
        switch(sonnerie){
            case "usa" :
                sonnerie = "Sirène 1";
                break;
            case "fr" :
                sonnerie = "Sirène 2";
                break;
            case "poney" :
                sonnerie = "Petit Poney";
                break;
            default :
                break;
        }
        sonnerieList.setSummary(this.getString(R.string.pref_theme_sonore_summary).replace("$1", "" + sonnerie));
    }

    //Maj du summary sms de déclenchement
    private void LI_MajSummerySms(){
        String sms = sharedPref.getString("sms", "ring");
        if(!sms.equals("")) {
            sms = sms.concat("+pin");
        }
        smsList.setSummary(this.getString(R.string.pref_SMS_summary).replace("$1", "" + sms));
    }

    //Maj du summary message
    private void LI_MajSummeryMessage(){
        String message = sharedPref.getString("text_alarme", "Ce portable a été volé.");
        messageAlarmeEdit.setSummary(this.getString(R.string.pref_texte_alarme_summary).replace("$1", "" + message));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.settinglayout, container, false);
        return v;
    }

}