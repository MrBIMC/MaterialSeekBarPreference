package com.pavelsikun.seekbarpreference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by mrbimc on 30.09.15.
 */
public class MaterialSeekBarController implements TextWatcher, SeekBar.OnSeekBarChangeListener {

    private final String TAG = getClass().getName();

    private static final int DEFAULT_CURRENT_VALUE = 50;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_INTERVAL = 1;
    private static final String DEFAULT_MEASUREMENT_UNIT = "";

    private int mMaxValue;
    private int mMinValue;
    private int mInterval;
    private int mCurrentValue;
    private String mMeasurementUnit;

    private SeekBar mSeekBar;
    private EditText mSeekBarValue;
    private TextView mMeasurementUnitView;

    private String mTitle;
    private String mSummary;

    private Context mContext;

    private Persistable mPersistable;

    public MaterialSeekBarController(Context context, AttributeSet attrs, Persistable persistable) {
        mContext = context;
        mPersistable = persistable;
        init(attrs, null);
    }

    public MaterialSeekBarController(Context context, AttributeSet attrs, View view, Persistable persistable) {
        mContext = context;
        mPersistable = persistable;
        init(attrs, view);
    }

    private void init(AttributeSet attrs, View view) {
        setValuesFromXml(attrs);
        if(view != null) onBindView(view);
    }
    private void setValuesFromXml(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            mCurrentValue = DEFAULT_CURRENT_VALUE;
            mMinValue = DEFAULT_MIN_VALUE;
            mMaxValue = DEFAULT_MAX_VALUE;
            mInterval = DEFAULT_INTERVAL;
            mMeasurementUnit = DEFAULT_MEASUREMENT_UNIT;
        } else {
            TypedArray a = mContext.obtainStyledAttributes(attrs, com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference);
            try {
                mMinValue = a.getInt(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_minValue, DEFAULT_MIN_VALUE);
                mMaxValue = a.getInt(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_maxValue, DEFAULT_MAX_VALUE);
                mInterval = a.getInt(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_interval, DEFAULT_INTERVAL);
                mCurrentValue = attrs.getAttributeIntValue(android.R.attr.defaultValue, DEFAULT_CURRENT_VALUE);

                mTitle = a.getString(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_title);
                mSummary = a.getString(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_summary);

                if(mCurrentValue < mMinValue) mCurrentValue = (mMaxValue - mMinValue) / 2;
                mMeasurementUnit = a.getString(com.pavelsikun.seekbarpreference.R.styleable.SeekBarPreference_msbp_measurementUnit);
                if (mMeasurementUnit == null)
                    mMeasurementUnit = DEFAULT_MEASUREMENT_UNIT;
            } finally {
                a.recycle();
            }
        }
    }

    public void onBindView(@NonNull View view) {

        mSeekBar = (SeekBar) view.findViewById(com.pavelsikun.seekbarpreference.R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);

        mSeekBarValue = (EditText) view.findViewById(com.pavelsikun.seekbarpreference.R.id.seekbar_value);
        mSeekBarValue.setText(String.valueOf(mCurrentValue));
        mSeekBarValue.addTextChangedListener(this);

        mMeasurementUnitView = (TextView) view.findViewById(com.pavelsikun.seekbarpreference.R.id.measurement_unit);
        mMeasurementUnitView.setText(mMeasurementUnit);

        mSeekBar.setProgress(mCurrentValue - mMinValue);

        setSeekBarTintOnPreLollipop();

        if (!view.isEnabled()) {
            mSeekBar.setEnabled(false);
            mSeekBarValue.setEnabled(false);
        }

        if(mTitle != null || mSummary != null) {
            TextView title = (TextView) view.findViewById(android.R.id.title);
            TextView summary = (TextView) view.findViewById(android.R.id.summary);

            if(mTitle != null) title.setText(mTitle);
            if(mSummary != null) summary.setText(mSummary);
        }
    }

    void setSeekBarTintOnPreLollipop() { //TMP: I hope google will introduce native seekbar tinting for appcompat users
        if (SDK_INT < 21) {
            Resources.Theme theme = mContext.getTheme();

            int attr = com.pavelsikun.seekbarpreference.R.attr.colorAccent;
            int fallbackColor = Color.parseColor("#009688");
            int accent = theme.obtainStyledAttributes(new int[]{attr}).getColor(0, fallbackColor);

            ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
            thumb.setIntrinsicHeight(pxFromDp(15, mContext));
            thumb.setIntrinsicWidth(pxFromDp(15, mContext));
            thumb.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.SRC_ATOP));
            mSeekBar.setThumb(thumb);

            Drawable progress = mSeekBar.getProgressDrawable();
            progress.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.MULTIPLY));
            mSeekBar.setProgressDrawable(progress);
        }
    }

    protected Object onGetDefaultValue(@NonNull TypedArray ta, int index) {
        return ta.getInt(index, mCurrentValue);
    }

    protected void onSetInitialValue(boolean restoreValue, @NonNull Object defaultValue) {
        mCurrentValue = (mMaxValue - mMinValue) / 2;
        try {
            mCurrentValue = (Integer) defaultValue;
        } catch (Exception ex) {
            Log.e(TAG, "Invalid default value: " + defaultValue.toString());
        }
    }

    public void setEnabled(boolean enabled) {
        if (mSeekBar != null) mSeekBar.setEnabled(enabled);
        if (mSeekBarValue != null) mSeekBarValue.setEnabled(enabled);
    }

    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
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
        mSeekBarValue.addTextChangedListener(this);
        setCurrentValue(mCurrentValue);
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
        Log.d(TAG, "after text changed");
        int value = mMinValue;
        try {
            value = Integer.parseInt(s.toString());
            if (value > mMaxValue) value = mMaxValue;
            else if (value < mMinValue) value = mMinValue;
        } catch (Exception e) {
            Log.e(TAG, "non-integer data: " + s.toString());
        }
        setCurrentValue(value);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
    }


    //public methods for manipulating this widget from java:
    public void setCurrentValue(int value) {
        mCurrentValue = value;
        if(mPersistable != null) mPersistable.onPersist(value);
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

    static int pxFromDp(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
