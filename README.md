## MaterialSeekBarPreference
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialSeekBarPreference-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1756)
As far as I checked, there are no cool implementations of SeekBarPreference. So I decided to make one.

![](https://raw.githubusercontent.com/MrBIMC/MaterialSeekBarPreference/master/SCREENSHOT-1.png)

#Usage

Add this to your module dependencies:
```groovy
    compile 'com.pavelsikun:material-seekbar-preference:0.5'
````

Reference namespace on top of your preferences layout file:
```xml
    xmlns:sample="http://schemas.android.com/apk/res-auto">
````

Now you can use this view in your preferences layout, just like any other normal preference:
```xml
    <com.pavelsikun.seekbarpreference.SeekBarPreference
        android:key="your_pref_key"
        android:title="SeekbarPreference 2"
        android:summary="Some summary"
        android:enabled="false"
        android:defaultValue="5000"
        sample:minValue="100"
        sample:maxValue="10000"
        sample:interval="200"
        sample:measurementUnit="%"/>
````

As you can see, lib provides 4 custom attributes(minValue, maxValue, interval and measurementUnit). 
Use them to define look and desired behavior of your preference.

#Known bugs and planned features
1. Seekbar is not themmable on pre-lollipop. I can fix it for API16+ though(through custom attribute for accent color).
2. No support of RTL yet.
3. Small bug: It takes 2 taps on seekbar value indicator to open up the keyboard.

#Licence
Lib is licenced under *MIT licence*, so you can do whatever you want with it.
I'd highly recommend to push changes back to make it cooler :D

