## MaterialSeekBarPreference

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialSeekBarPreference-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1756)

As far as I checked, there are no cool implementations of SeekBarPreference. So I decided to make one.

![on LOLIPOP](https://raw.githubusercontent.com/MrBIMC/MaterialSeekBarPreference/master/SCREENSHOT_LP_511.png)
![on LOLIPOP with keyboard input](https://raw.githubusercontent.com/MrBIMC/MaterialSeekBarPreference/master/SCREENSHOT_LP_511_keyboard.png)
![on ICS](https://raw.githubusercontent.com/MrBIMC/MaterialSeekBarPreference/master/SCREENSHOT_ICS_404.png)

#Usage

Add this to your module dependencies:
```groovy
    compile 'com.pavelsikun:material-seekbar-preference:0.10+'
````

Reference namespace on top of your preferences layout file:
```xml
    xmlns:sample="http://schemas.android.com/apk/res-auto">
````

Now you can use this view in your preferences layout, just like any other normal preference.
```xml
    <com.pavelsikun.seekbarpreference.SeekBarPreference
        android:key="your_pref_key"
        android:title="SeekbarPreference 2"
        android:summary="Some summary"
        android:enabled="false"
        android:defaultValue="5000"
        sample:msbp_minValue="100"
        sample:msbp_maxValue="10000"
        sample:msbp_interval="200"
        sample:msbp_measurementUnit="%"/>
````

As you can see, lib provides 4 custom attributes(msbp_minValue, msbp_maxValue, msbp_interval and msbp_measurementUnit).
measurementUnit is should be String or a reference to a String (measurementUnit="%"  or measurementUnit="@string/my_preference_unit").
Every other attribute should be an Integer or reference to an Integer resource (interval="@integer/my_preference_interval" or interval="10").
Use them to define look and desired behavior of your preference. Prefixes used to avoid attribute collisions with other libs.

#Known bugs and planned features
1. No support of RTL yet.

# Collaborators
I'd really want to thank:

* [krage](https://github.com/krage) for adding support for referenced resources.
* [NitroG42](https://github.com/NitroG42) for pointing out to attribute collisions.

#Licence
Lib is licenced under *MIT licence*, so you can do whatever you want with it.
I'd highly recommend to push changes back to make it cooler :D

