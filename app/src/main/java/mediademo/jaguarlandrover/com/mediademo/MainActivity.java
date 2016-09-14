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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaguarlandrover.rvi.Util;

import java.util.HashMap;

import mediademo.jaguarlandrover.com.mediademo.MediaManager.MediaManagerListener;


public class MainActivity extends AppCompatActivity implements MediaManagerListener {
    private static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private HashMap<Integer, String> mViewIdsToServiceIds;
    private HashMap<String, Integer> mServiceIdsToViewIds;
    private Boolean mPlaying = false;

    @Override
    public void onNodeConnected() {

    }

    @Override
    public void onNodeDisconnected() {

    }

    @Override
    public void onServiceInvoked(String serviceIdentifier, Object parameters) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, Util.getMethodName());
        MediaManager.setListener(this);
        if (!MediaManager.isRviConfigured()) {
            Log.d(TAG, "RVI Not configured, figure out the toolbar");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Log.d(TAG, prefs.getString("server_url", "None"));
            String msg = "Configure RVI in settings";
            Toast configureToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            configureToast.show();
            //settingsBar.show();
        } else {
            MediaManager.start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewIdsToServiceIds = MainActivityUtil.initializeViewToServiceIdMap();
        mServiceIdsToViewIds = MainActivityUtil.initializeServiceToViewIdMap();

        //initalize button off images
        //initalize button on images
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.settings_bar);
        setSupportActionBar(settingsToolbar);
        //ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);

        //playpause onclick
        ImageButton play_pause = (ImageButton) findViewById(R.id.playPauseButton);
        play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseButtonPressed(view);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Log.d(TAG, "You selected settings");
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mediademo.jaguarlandrover.com.mediademo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mediademo.jaguarlandrover.com.mediademo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    String getServiceIdentifiersFromViewId(Integer uiControlId) {
        return mViewIdsToServiceIds.get(uiControlId);
    }

    public void playPauseButtonPressed(View view) {
        Log.d(TAG, Util.getMethodName());
        if (!MediaManager.isRviConfigured()) {
            String msg = "Configure RVI in settings";
            Toast configureToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            configureToast.show();
        }
        ImageButton playpause = (ImageButton) findViewById(R.id.playPauseButton);
        if (true == mPlaying) {
            playpause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mPlaying = false;
        } else {
            playpause.setImageResource(R.drawable.ic_pause_black_24dp);
            mPlaying = true;
        }
        MediaManager.invokeService("PLAYPAUSE", Boolean.toString(mPlaying));
    }
}
