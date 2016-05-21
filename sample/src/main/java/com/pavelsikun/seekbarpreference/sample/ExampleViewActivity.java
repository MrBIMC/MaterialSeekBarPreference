package com.pavelsikun.seekbarpreference.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pavelsikun.seekbarpreference.OnValueSelectedListener;
import com.pavelsikun.seekbarpreference.SeekBarPreferenceView;

/**
 * Created by Pavel Sikun on 21.05.16.
 */

public class ExampleViewActivity extends AppCompatActivity {

    LinearLayout root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_demo);

        findViewById(R.id.uselessTempButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExampleViewActivity.this, ExamplePreferenceActivity.class));
            }
        });

        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        root = (LinearLayout) findViewById(R.id.root);
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
        view.setOnValueSelectedListener(new OnValueSelectedListener() {
            @Override
            public void onValueSelected(int value) {
                Toast.makeText(ExampleViewActivity.this, "callback from view: " + value, Toast.LENGTH_SHORT).show();
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
