package com.pavelsikun.seekbarpreference.sample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pavelsikun.seekbarpreference.PersistValueListener;
import com.pavelsikun.seekbarpreference.SeekBarPreferenceView;

/**
 * Created by Pavel Sikun on 21.05.16.
 */

public class ExampleViewActivity extends AppCompatActivity {

    private LinearLayout root;

    boolean isCompatPrefs = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_demo);

        findViewById(R.id.uselessTempButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCompatPrefs) {
                    startActivity(new Intent(ExampleViewActivity.this, ExamplePreferenceCompatActivity.class));
                }
                else {
                    if(Build.VERSION.SDK_INT < 11) {
                        Toast.makeText(
                                ExampleViewActivity.this,
                                "API-11+ required for this. Switch to preference-v7 mode instead",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        startActivity(new Intent(ExampleViewActivity.this, ExamplePreferenceActivity.class));
                    }
                }
            }
        });

        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        root = (LinearLayout) findViewById(R.id.root);

        final SwitchCompat s = (SwitchCompat) findViewById(R.id.switchWidget);
        RelativeLayout text = (RelativeLayout) findViewById(R.id.switchHolder);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.toggle();
            }
        });

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCompatPrefs = isChecked;
                Log.d(getClass().getSimpleName(), "switch state changed " + isChecked);
            }
        });
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SeekBarPreferenceView view = new SeekBarPreferenceView(this);
        view.setTitle("Dynamic View");
        view.setSummary("This one was added from Java");
        view.setMaxValue(5000);
        view.setMinValue(-5000);
        view.setInterval(10);
        view.setCurrentValue(0);
        view.setMeasurementUnit("points");
        view.setOnValueSelectedListener(new PersistValueListener() {
            @Override
            public boolean persistInt(int value) {
                Toast.makeText(ExampleViewActivity.this, "callback from view: " + value, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        root.addView(view);
        Log.d(getClass().getSimpleName(), "added new view");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
