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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.internal.LinkedTreeMap;
import com.jaguarlandrover.rvi.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mediademo.jaguarlandrover.com.mediademo.MediaManager.MediaManagerListener;


public class MainActivity extends AppCompatActivity implements MediaManagerListener {
    private static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Boolean mRviConnected = false;
    private Boolean mInitialized = false;
    private HashMap<Integer, String> mViewIdsToServiceIds;
    private HashMap<String, Integer> mServiceIdsToViewIds;
    private HashMap<String, MainActivityUtil.ParamGetter> mSignalToValue;
    private HashMap<String, Integer> mSignalToViewIds;
    private HashMap<String, Integer> mSignalToInvokable;
    private HashMap<Integer, Integer> mButtonOffImages;
    private HashMap<Integer, Boolean> mButtonStates;
    private HashMap<Integer, LinkedTreeMap> mediaList = new HashMap<>();
    private HashMap<String, LinkedTreeMap> multiMedia = new HashMap<>();
    private MediaListObject songList = MediaListObject.getInstance();
    private MultimediaListObject multiMediaList = MultimediaListObject.getInstance();
    private LinkedTreeMap<String, LinkedTreeMap> mMediaTree;
    private Integer currentIndex;

    @Override
    public void onNodeConnected() {
        String msg = "RVI Connected!";
        Toast connectedToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        connectedToast.show();
        mRviConnected = true;
        MediaManager.subscribeToMediaRvi();
        if (!mInitialized) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaManager.initializeUIState();
                }
            }, 2 * 1000);
            mInitialized = true;
        }
    }

    @Override
    public void onNodeDisconnected() {
        String msg = "RVI Disconnected!";
        Toast connectedToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        connectedToast.show();
        mRviConnected = false;
    }

    @Override
    public void onServiceInvoked(String serviceIdentifier, Object parameters) {
        Log.d(TAG, Util.getMethodName() + "::" + serviceIdentifier + "::" + parameters.toString());
        LinkedTreeMap msg = (LinkedTreeMap) parameters;
        String target;
        if (msg.get("target") != null) {
            target = msg.get("target").toString();
        } else {
            target = msg.get("signalName").toString().toUpperCase();
        }
        Integer string_id = mSignalToInvokable.get(target);
        if (string_id != null) {
            String actionable = getString(string_id);
            if (actionable != null) {
                MediaManager.invokeService(actionable, null);
            }
        } else {
            Integer view_id = mSignalToViewIds.get(target);
            MainActivityUtil.ParamGetter getter = mSignalToValue.get(target);
            if (getter != null) {
                Boolean new_value = getter.getParam(msg.get("value"));
                if (view_id != null) {
                    mButtonStates.put(view_id, new_value);
                    updateUI(view_id);
                }
            }
        }
        if (target.equalsIgnoreCase(MediaServiceIdentifier.GETPLAYLIST.value())) {
            currentPlayQueue(msg);
        } else if (target.equalsIgnoreCase(MediaServiceIdentifier.CURRENT_CHANGE.value())) {
            updateCurrentTrack((Double) msg.get("value"));
        } else if (target.equalsIgnoreCase(MediaServiceIdentifier.GETMULTIMEDIA.value())) {
            String child = (String) msg.get("identifiers");
            if (child != null) {
                MediaManager.invokeService(MediaServiceIdentifier.GETMEDIACHILD.value(), child);
            }
        } else if (target.equalsIgnoreCase(MediaServiceIdentifier.GETMEDIACHILD.value())) {
            buildmultimedia(msg);
        }
    }

    public Boolean checkConfigured() {
        if (!MediaManager.isRviConfigured()) {
            String msg = "Configure RVI in settings";
            Toast configureToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            configureToast.show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, Util.getMethodName());
        MediaManager.setListener(this);
        if (checkConfigured() && !mRviConnected) {
            MediaManager.start();
        }
        if (mRviConnected) {
            MediaManager.initializeUIState();
            if(currentIndex != null) {
                updateCurrentTrack(currentIndex.doubleValue());
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        //load font
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        mViewIdsToServiceIds = MainActivityUtil.initializeViewToServiceIdMap();
        mServiceIdsToViewIds = MainActivityUtil.initializeServiceToViewIdMap();
        mButtonStates        = MainActivityUtil.initializeButtonState();
        mSignalToViewIds     = MainActivityUtil.initializeSignaltoViewId(getApplicationContext());
        mSignalToInvokable   = MainActivityUtil.initializeSignaltoInvokable();
        mSignalToValue       = MainActivityUtil.initializeSignalToValue(getApplicationContext());

        //initialize button off images
        mButtonOffImages = MainActivityUtil.initializeButtonImages();
        for(Map.Entry<Integer, Integer> entry : mButtonOffImages.entrySet()){
            Button temp = (Button) findViewById(entry.getKey());
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressed(v);
                }
            });
            temp.setTypeface(font);
            temp.setText(entry.getValue());
        }

        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.settings_bar);
        setSupportActionBar(settingsToolbar);

        Button playlist = (Button) findViewById(R.id.playList);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayListPressed(v);
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
            case R.id.library_option:
                Log.d(TAG, "You selected library option");
                if (multiMediaList.getMultiMedia().size() == 0) {
                    multiMediaList.clearData();
                    MediaManager.invokeService(MediaServiceIdentifier.GETMULTIMEDIA.value(), null);
                }
                Intent listViewIntent = new Intent(this, MultimediaListActivity.class);
                startActivity(listViewIntent);
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

    public void onButtonPressed(View view) {
        if (mRviConnected) {
            if (!mInitialized) {
                MediaManager.initializeUIState();
                mInitialized = true;
            }
        }
        if (checkConfigured()) {
            if (mButtonStates.get(view.getId())) {
                //does this mean play = 0 is pause = 1??? or do I have to be explicit?
                MediaManager.invokeService(getServiceIdentifiersFromViewId(view.getId()), 0);
                if (mButtonStates.get(view.getId())) {
                    mButtonStates.put(view.getId(), false);
                    updateUI(view.getId());
                }
            } else {
                MediaManager.invokeService(getServiceIdentifiersFromViewId(view.getId()), 1);
                if (!mButtonStates.get(view.getId())) {
                    mButtonStates.put(view.getId(), true);
                    updateUI(view.getId());
                }
            }
        }
    }

    public void onPlayListPressed(View view) {
        if (songList.getSongs().size() == 0) {
            MediaManager.invokeService(MediaServiceIdentifier.GETPLAYLIST.value(), null);
        }
        Intent listViewIntent = new Intent(this, MediaListActivity.class);
        if (mediaList != null) {
            ArrayList<LinkedTreeMap> mSongList = new ArrayList<>();
            for(int index = 0; index < mediaList.size(); index++) {
                mSongList.add(mediaList.get(index));
            }
            songList.setSongs(mSongList);
        }
        startActivity(listViewIntent);
    }

    private void updateUI(Integer viewId) {
        Button temp = (Button) findViewById(viewId);
        switch(viewId) {
            case R.id.playPauseButton:
                if (mButtonStates.get(viewId)) {
                    temp.setText(R.string.icon_pause);
                } else {
                    temp.setText(R.string.icon_play);
                }
                temp.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case R.id.shuffle:
                if (mButtonStates.get(viewId)) {
                    temp.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    temp.setTextColor(getResources().getColor(R.color.colorIcons));
                }
                break;
            case R.id.repeat:
                if (mButtonStates.get(viewId)) {
                    temp.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    temp.setTextColor(getResources().getColor(R.color.colorIcons));
                }
                break;
            case R.id.volume:
                if (mButtonStates.get(viewId)) {
                    temp.setText(R.string.icon_volume);
                    temp.setTextColor(getResources().getColor(R.color.colorIcons));
                } else {
                    temp.setText(R.string.icon_no_volume);
                    temp.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                break;
            default:
                //do nothing
                break;
        }
    }

    public void currentPlayQueue(LinkedTreeMap params) {
        //update the current track view
        LinkedTreeMap track = (LinkedTreeMap) params.get("track");
        if (track != null) {
            Double index = (Double) params.get("index");
            mediaList.put(index.intValue(), track);
        }
    }

    public void updateCurrentTrack(Double index) {
        if (mediaList == null) {
            MediaManager.invokeService(MediaServiceIdentifier.GETPLAYLIST.value(), null);
            return;
        }
        LinkedTreeMap track = mediaList.get(index.intValue());
        if (track != null) {
            currentIndex = index.intValue();
            TextView cur_song_title = (TextView) findViewById(R.id.songTitleText);
            TextView cur_artist = (TextView) findViewById(R.id.artistText);
            String title = track.get("displayName").toString() + " - " + track.get("album").toString();
            String artist = track.get("artist").toString();
            cur_song_title.setText(title);
            cur_artist.setText(artist);
        }
    }

    public void buildmultimedia(LinkedTreeMap msg) {
        //things
        Log.d(TAG, "buildmultimedia::" + msg.get("children"));
        LinkedTreeMap child = (LinkedTreeMap) msg.get("children");
        if (child != null) {
            multiMediaList.addMultimedia(child);
        }
    }
}
