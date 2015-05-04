package com.pavelsikun.seekbarpreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener, TextWatcher {

    private final String TAG = getClass().getName();

    private static final String APPLICATION_NS ="http://schemas.android.com/apk/res-auto";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private int mMaxValue = 100;
    private int mMinValue = 0;
    private int mInterval = 1;
    private int mCurrentValue;
    private String mMeasurementUnitValue;

    private SeekBar mSeekBar;
    private EditText mSeekBarValue;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setValuesFromXml(attrs);
        setLayoutResource(R.layout.seekbar_preference);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setValuesFromXml(attrs);
        setLayoutResource(R.layout.seekbar_preference);
    }

    private void setValuesFromXml(AttributeSet attrs) {
        mMaxValue = attrs.getAttributeIntValue(APPLICATION_NS, "maxValue", 100);
        mMinValue = attrs.getAttributeIntValue(APPLICATION_NS, "minValue", 0);
        mCurrentValue = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 50);

        mMeasurementUnitValue = attrs.getAttributeValue(APPLICATION_NS, "measurementUnit");
        if(mMeasurementUnitValue == null) mMeasurementUnitValue = "";

        mInterval = attrs.getAttributeIntValue(APPLICATION_NS, "interval", 1);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);

        mSeekBarValue = (EditText) view.findViewById(R.id.seekbar_value);
        mSeekBarValue.setText(String.valueOf(mCurrentValue));
        mSeekBarValue.addTextChangedListener(this);

        TextView mMeasurementUnit = (TextView) view.findViewById(R.id.measurement_unit);
        mMeasurementUnit.setText(mMeasurementUnitValue);

        mSeekBar.setProgress(mCurrentValue - mMinValue);

        if (!view.isEnabled()) {
            mSeekBar.setEnabled(false);
            mSeekBarValue.setEnabled(false);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index){
        return ta.getInt(index, mCurrentValue);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if(restoreValue) mCurrentValue = getPersistedInt(mCurrentValue);
        else {
            int temp = 0;
            try { temp = (Integer)defaultValue; }
            catch(Exception ex) { Log.e(TAG, "Invalid default value: " + defaultValue.toString()); }

            persistInt(temp);
            mCurrentValue = temp;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(mSeekBar != null) mSeekBar.setEnabled(enabled);
        if(mSeekBarValue != null) mSeekBarValue.setEnabled(enabled);
    }

    @Override
    public boolean persistInt(int value) { return super.persistInt(value); }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
        if (mSeekBar != null) mSeekBar.setEnabled(!disableDependent);
        if(mSeekBarValue != null) mSeekBarValue.setEnabled(!disableDependent);
    }

    //SeekBarListener:
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + mMinValue;

        if(newValue > mMaxValue) newValue = mMaxValue;

        else if(newValue < mMinValue) newValue = mMinValue;

        else if(mInterval != 1 && newValue % mInterval != 0)
            newValue = Math.round(((float)newValue)/mInterval) * mInterval;

        // change rejected, revert to the previous value
        if(!callChangeListener(newValue)){
            seekBar.setProgress(mCurrentValue - mMinValue);
            return;
        }

        // change accepted, store it
        mCurrentValue = newValue;
        mSeekBarValue.setText(String.valueOf(newValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeekBarValue.removeTextChangedListener(this);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
        persistInt(mCurrentValue);
        mSeekBarValue.addTextChangedListener(this);
    }

    //TextWatcher:
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mSeekBar.setOnSeekBarChangeListener(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {

        int value = mMinValue;
        try {
            value = Integer.parseInt(s.toString());
            if(value > mMaxValue) value = mMaxValue;
            else if(value < mMinValue) value = mMinValue;
        }
        catch (Exception e) { Log.e(TAG, "non-integer data: " + s.toString()); }

        mCurrentValue = value;

        persistInt(mCurrentValue);
        mSeekBar.setProgress(mCurrentValue);

        mSeekBar.setOnSeekBarChangeListener(this);
    }
}
