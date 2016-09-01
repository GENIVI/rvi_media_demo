package mediademo.jaguarlandrover.com.mediademo;/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *                                                                                                                          
*
* Copyright (c) 2016 Jaguar Land Rover.
*
* This program is licensed under the terms and conditions of the
* Mozilla Public License, version 2.0. The full text of the
* Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
*
* Project: MediaDemo
*
* Created by aren on 9/1/16.
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import static mediademo.jaguarlandrover.com.mediademo.R.xml.prefs;

public class Preferences extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(prefs);
    }
}

