package com.pavelsikun.seekbarpreference.sample;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.pavelsikun.seekbarpreference.SeekBarPreferenceCompat;

/**
 * Created by mrbimc on 02.04.16.
 */

public class ExamplePreferenceCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesScreen())
                .commit();

        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PreferencesScreen extends PreferenceFragmentCompat {

        //I also recommend you to use some 3rd-party lib to style PreferenceFragmentCompat
        //because by default v7 prefs don't respect device theme and look ugly,
        //even on v21+(thought afaik since v23.4.0 look on lollipop+ was fixed)

        //theoretically useful links:
        // * http://stackoverflow.com/questions/32070670/preferencefragmentcompat-requires-preferencetheme-to-be-set
        // * https://github.com/consp1racy/android-support-preference

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            SeekBarPreferenceCompat pref = new SeekBarPreferenceCompat(getActivity());
            pref.setTitle("Dynamic PreferenceCompat Example");
            pref.setSummary("This one was added from Java");
            pref.setMinValue(222);
            pref.setMaxValue(999);
            pref.setCurrentValue(444);
            pref.setMeasurementUnit("nectarines");

            getPreferenceScreen().addPreference(pref);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_general_compat); // load your SeekBarPreferenceCompat prefs from xml
        }
    }
}
