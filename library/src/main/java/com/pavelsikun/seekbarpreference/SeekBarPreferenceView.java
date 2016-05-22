package com.pavelsikun.seekbarpreference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Pavel Sikun on 21.05.16.
 */

public class SeekBarPreferenceView extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_INTERVAL = 1;
    private static final boolean DEFAULT_DIALOG_ENABLED = true;
    private static final boolean DEFAULT_IS_ENABLED = true;

    private static final int DEFAULT_DIALOG_STYLE = R.style.MSB_Dialog_Default;

    private int maxValue;
    private int minValue;
    private int interval;
    private int currentValue;
    private String measurementUnit;
    private boolean dialogEnabled;

    private int dialogStyle;

    private TextView valueView;
    private SeekBar seekBarView;
    private TextView measurementView;
    private LinearLayout valueHolderView;
    private FrameLayout bottomLineView;
    private View view;

    private TextView titleView, summaryView;

    private String title;
    private String summary;
    private boolean isEnabled;

    private OnValueSelectedListener onValueSelectedListener;

    public SeekBarPreferenceView(Context context) {
        super(context);
        init(null);
    }

    public SeekBarPreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBarPreferenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreferenceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        view = inflate(getContext(), R.layout.seekbar_view_layout, this);
        loadValuesFromXml(attrs);
    }

    private void loadValuesFromXml(AttributeSet attrs) {
        if(attrs == null) {
            currentValue = DEFAULT_CURRENT_VALUE;
            minValue = DEFAULT_MIN_VALUE;
            maxValue = DEFAULT_MAX_VALUE;
            interval = DEFAULT_INTERVAL;
            dialogEnabled = DEFAULT_DIALOG_ENABLED;
            isEnabled = DEFAULT_IS_ENABLED;
        }
        else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
            try {
                minValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE);
                maxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE);
                interval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL);
                dialogEnabled = a.getBoolean(R.styleable.SeekBarPreference_msbp_view_dialogEnabled, DEFAULT_DIALOG_ENABLED);

                measurementUnit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit);

//                dialogStyle = a.getInt(R.styleable.SeekBarPreference_msbp_dialogStyle, DEFAULT_DIALOG_STYLE);
                dialogStyle = DEFAULT_DIALOG_STYLE;

                title = a.getString(R.styleable.SeekBarPreference_msbp_view_title);
                summary = a.getString(R.styleable.SeekBarPreference_msbp_view_summary);
                currentValue = a.getInt(R.styleable.SeekBarPreference_msbp_view_defaultValue, DEFAULT_CURRENT_VALUE);
                isEnabled = a.getBoolean(R.styleable.SeekBarPreference_msbp_view_enabled, DEFAULT_IS_ENABLED);
            }
            finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        seekBarView = (SeekBar) view.findViewById(R.id.seekbar);
        measurementView = (TextView) view.findViewById(R.id.measurement_unit);
        valueView = (TextView) view.findViewById(R.id.seekbar_value);

        setMaxValue(maxValue);
        seekBarView.setOnSeekBarChangeListener(this);

        measurementView.setText(measurementUnit);


        setCurrentValue(currentValue);
        valueView.setText(String.valueOf(currentValue));

        titleView = (TextView) view.findViewById(android.R.id.title);
        summaryView = (TextView) view.findViewById(android.R.id.summary);

        titleView.setText(title);
        summaryView.setText(summary);

        bottomLineView = (FrameLayout) view.findViewById(R.id.bottom_line);
        valueHolderView = (LinearLayout) view.findViewById(R.id.value_holder);

        setDialogEnabled(dialogEnabled);

        if (!isEnabled) {
            Log.d(TAG, "view is disabled!");
            seekBarView.setEnabled(false);
            valueView.setEnabled(false);
            titleView.setEnabled(false);
            summaryView.setEnabled(false);
            measurementView.setEnabled(false);
            valueHolderView.setEnabled(false);
            view.findViewById(R.id.bottom_line).setEnabled(false);
        }
    }

    private void persistInt(int value) {
        if(value < minValue) value = minValue;
        if(value > maxValue) value = maxValue;

        currentValue = value;

        if(onValueSelectedListener != null) {
            onValueSelectedListener.onValueSelected(currentValue);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + minValue;

        if (newValue > maxValue) {
            newValue = maxValue;
        }
        else if (newValue < minValue) {
            newValue = minValue;
        }

        if (interval != 1 && newValue % interval != 0) {
            newValue = Math.round(((float) newValue) / interval) * interval;
        }

        currentValue = newValue;
        valueView.setText(String.valueOf(newValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setCurrentValue(currentValue);
    }

    @Override
    public void onClick(final View v) {
        new CustomValueDialog(getContext(), dialogStyle, minValue, maxValue, currentValue)
                .setOnValueSelectedListener(new OnValueSelectedListener() {
                    @Override
                    public void onValueSelected(int value) {
                        setCurrentValue(value);
                        seekBarView.setOnSeekBarChangeListener(null);
                        seekBarView.setProgress(currentValue - minValue);
                        seekBarView.setOnSeekBarChangeListener(SeekBarPreferenceView.this);

                        valueView.setText(String.valueOf(currentValue));
                    }
                })
                .show();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        if(seekBarView != null) {
            if(minValue <= 0 && maxValue >= 0) {
                seekBarView.setMax(maxValue - minValue);
            }
            else {
                seekBarView.setMax(maxValue);
            }

            seekBarView.setProgress(currentValue - minValue);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if(titleView != null) {
            titleView.setText(title);
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        if(seekBarView != null) {
            summaryView.setText(summary);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        setMaxValue(maxValue);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        persistInt(currentValue);
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
        if (measurementView != null) {
            measurementView.setText(measurementUnit);
        }
    }


    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        this.onValueSelectedListener = onValueSelectedListener;
    }

    public boolean isDialogEnabled() {
        return dialogEnabled;
    }

    public void setDialogEnabled(boolean dialogEnabled) {
        this.dialogEnabled = dialogEnabled;

        valueHolderView.setOnClickListener(dialogEnabled ? this : null);
        valueHolderView.setClickable(dialogEnabled);
        bottomLineView.setVisibility(dialogEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    public void setDialogStyle(int dialogStyle) {
        this.dialogStyle = dialogStyle;
    }
}
