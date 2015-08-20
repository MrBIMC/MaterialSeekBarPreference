package com.pavelsikun.seekbarpreference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.os.Build.VERSION.SDK_INT;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener, TextWatcher {

    private final String TAG = getClass().getName();

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_INTERVAL = 1;
    private static final String DEFAULT_MEASUREMENT_UNIT = "";

    private int mMaxValue = 100;
    private int mMinValue = 0;
    private int mInterval = 1;
    private int mCurrentValue;
    private String mMeasurementUnit;

    private SeekBar mSeekBar;
    private EditText mSeekBarValue;
    private TextView mMeasurementUnitView;


    public SeekBarPreference(Context context) {
        super(context);
        setValuesFromXml(null);
        setLayoutResource(R.layout.seekbar_preference);
    }

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setValuesFromXml(attrs);
        setLayoutResource(R.layout.seekbar_preference);
    }

    private void setValuesFromXml(AttributeSet attrs) {
        if (attrs == null) {
            mCurrentValue = DEFAULT_CURRENT_VALUE;
            mMinValue = DEFAULT_MIN_VALUE;
            mMaxValue = DEFAULT_MAX_VALUE;
            mInterval = DEFAULT_INTERVAL;
            mMeasurementUnit = DEFAULT_MEASUREMENT_UNIT;
        } else {
            mCurrentValue = attrs.getAttributeIntValue(android.R.attr.defaultValue, DEFAULT_CURRENT_VALUE);
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
            try {
                mMinValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE);
                mMaxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE);
                mInterval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL);
                mMeasurementUnit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit);
                if (mMeasurementUnit == null)
                    mMeasurementUnit = DEFAULT_MEASUREMENT_UNIT;
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);

        mSeekBarValue = (EditText) view.findViewById(R.id.seekbar_value);
        mSeekBarValue.setText(String.valueOf(mCurrentValue));
        mSeekBarValue.addTextChangedListener(this);

        mMeasurementUnitView = (TextView) view.findViewById(R.id.measurement_unit);
        mMeasurementUnitView.setText(mMeasurementUnit);

        mSeekBar.setProgress(mCurrentValue - mMinValue);

        setSeekBarTintOnPreLollipop();

        if (!view.isEnabled()) {
            mSeekBar.setEnabled(false);
            mSeekBarValue.setEnabled(false);
        }
    }

    static int pxFromDp(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    void setSeekBarTintOnPreLollipop() { //TMP: I hope google will introduce native seekbar tinting for appcompat users
        if (SDK_INT < 21) {
            Resources.Theme theme = getContext().getTheme();

            int attr = R.attr.colorAccent;
            int fallbackColor = Color.parseColor("#009688");
            int accent = theme.obtainStyledAttributes(new int[]{attr}).getColor(0, fallbackColor);

            ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
            thumb.setIntrinsicHeight(pxFromDp(15, getContext()));
            thumb.setIntrinsicWidth(pxFromDp(15, getContext()));
            thumb.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.SRC_ATOP));
            mSeekBar.setThumb(thumb);

            Drawable progress = mSeekBar.getProgressDrawable();
            progress.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.MULTIPLY));
            mSeekBar.setProgressDrawable(progress);
        }
    }

    @Override
    protected Object onGetDefaultValue(@NonNull TypedArray ta, int index) {
        return ta.getInt(index, mCurrentValue);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, @NonNull Object defaultValue) {
        if (restoreValue) mCurrentValue = getPersistedInt(mCurrentValue);
        else {
            int temp = 0;
            try {
                temp = (Integer) defaultValue;
            } catch (Exception ex) {
                Log.e(TAG, "Invalid default value: " + defaultValue.toString());
            }

            persistInt(temp);
            mCurrentValue = temp;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mSeekBar != null) mSeekBar.setEnabled(enabled);
        if (mSeekBarValue != null) mSeekBarValue.setEnabled(enabled);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
        if (mSeekBar != null) mSeekBar.setEnabled(!disableDependent);
        if (mSeekBarValue != null) mSeekBarValue.setEnabled(!disableDependent);
    }

    //SeekBarListener:
    @Override
    public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
        int newValue = progress + mMinValue;

        if (newValue > mMaxValue) newValue = mMaxValue;

        else if (newValue < mMinValue) newValue = mMinValue;

        else if (mInterval != 1 && newValue % mInterval != 0)
            newValue = Math.round(((float) newValue) / mInterval) * mInterval;

        // change rejected, revert to the previous value
        if (!callChangeListener(newValue)) {
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
    public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
        notifyChanged();
        persistInt(mCurrentValue);
        mSeekBarValue.addTextChangedListener(this);
    }

    //TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mSeekBar.setOnSeekBarChangeListener(null);
    }

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        int value = mMinValue;
        try {
            value = Integer.parseInt(s.toString());
            if (value > mMaxValue) value = mMaxValue;
            else if (value < mMinValue) value = mMinValue;
        } catch (Exception e) {
            Log.e(TAG, "non-integer data: " + s.toString());
        }

        mCurrentValue = value;

        persistInt(mCurrentValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    //public methods for manipulating this widget from java:
    public void setCurrentValue(int value) {
        mCurrentValue = value;
        super.persistInt(value);
        notifyChanged();
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }


    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        if (mSeekBar != null) mSeekBar.setMax(mMaxValue - mMinValue);
    }

    public int getMaxValue() {
        return mMaxValue;
    }


    public void setMinValue(int minValue) {
        mMinValue = minValue;
        if (mSeekBar != null) mSeekBar.setMax(mMaxValue - mMinValue);
    }

    public int getMinValue() {
        return mMinValue;
    }


    public void setInterval(int interval) {
        mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }


    public void setMeasurementUnit(String measurementUnit) {
        mMeasurementUnit = measurementUnit;
        if (mMeasurementUnitView != null) mMeasurementUnitView.setText(mMeasurementUnit);
    }

    public String getMeasurementUnit() {
        return mMeasurementUnit;
    }
}
