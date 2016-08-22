/*
 * Copyright (c) 2016. Jaguar Land Rover.
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one
 * at https://mozilla.org/MPL/2.0/.
 * File: MainActivity.java
 * Project: MediaDemo
 * Created by aren on 8/22/16 3:09 PM
 */

package mediademo.jaguarlandrover.com.mediademo;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SpinnerAdapter;

import mediademo.jaguarlandrover.com.mediademo.MediaManager.MediaManagerListener;


public class MainActivity extends AppCompatActivity implements MediaManagerListener {
    @Override
    public void onNodeConnected() {

    }

    @Override
    public void onNodeDisconnected() {

    }

    @Override
    public void onServiceInvoked(String serviceIdentifier, Object parameters) {

    }
}
