package com.pavelsikun.seekbarpreference.sample;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

import static android.view.View.generateViewId;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        if(Build.VERSION.SDK_INT > 16) frame.setId(generateViewId());
        else //noinspection ResourceType
            frame.setId(676442);

        setContentView(frame);

        getFragmentManager()
        .beginTransaction()
        .add(frame.getId(), new CreditsFragment())
        .commit();
    }

    public static class CreditsFragment extends PreferenceFragment {

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }


        //Runtime preference demo:
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            final SeekBarPreference seekBarPreference1 = new SeekBarPreference(getActivity());
            this.getPreferenceScreen().addPreference(seekBarPreference1);

            seekBarPreference1.setTitle("Runtime-Added Preference");
            seekBarPreference1.setSummary("This preference was added directly from java");
            seekBarPreference1.setMaxValue(444);
            seekBarPreference1.setMinValue(111);
            seekBarPreference1.setInterval(4);
            seekBarPreference1.setMeasurementUnit("demos");
            seekBarPreference1.setCurrentValue(333);

            Toast.makeText(getActivity(), "In 10 seconds last seekbarPreference will change it's" +
                    " params(DEMO of manipulating from java :D)", Toast.LENGTH_LONG).show();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(@NonNull Void... params) {
                    try { Thread.sleep(10 * 1000); }catch (Exception e) {/*IGNORE*/}
                    return null;
                }

                @Override
                protected void onPostExecute(@NonNull Void aVoid) {
                    super.onPostExecute(aVoid);
                    seekBarPreference1.setMinValue(-222);
                    seekBarPreference1.setMaxValue(999);
                    seekBarPreference1.setInterval(1);
                    seekBarPreference1.setCurrentValue(777);
                    seekBarPreference1.setMeasurementUnit("<- changed!");
                    if(getActivity() != null) {
                        Toast.makeText(getActivity(), "SeekbarPreference params changed!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }
}
