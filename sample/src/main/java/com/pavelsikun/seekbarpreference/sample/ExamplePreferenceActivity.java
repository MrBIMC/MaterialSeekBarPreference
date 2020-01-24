package com.pavelsikun.seekbarpreference.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.pavelsikun.seekbarpreference.SeekBarPreference;

/**
 * Created by mrbimc on 02.04.16.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExamplePreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
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

    public static class PreferencesScreen extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            SeekBarPreference pref = new SeekBarPreference(getActivity());
            pref.setTitle("Dynamic Preference");
            pref.setSummary("This one was added from Java");
            pref.setMinValue(222);
            pref.setMaxValue(999);
            pref.setCurrentValue(444);
            pref.setMeasurementUnit("nectarines");

            getPreferenceScreen().addPreference(pref);
        }


    }
}
