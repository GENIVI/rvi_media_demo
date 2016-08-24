/*
 * Copyright (c) 2016. Jaguar Land Rover.
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one
 * at https://mozilla.org/MPL/2.0/.
 * File: MediaManager.java
 * Project: MediaDemo
 * Created by aren on 8/22/16 3:09 PM
 */

package mediademo.jaguarlandrover.com.mediademo;

import android.content.Context;

import com.jaguarlandrover.rvi.RVINode;
import com.jaguarlandrover.rvi.ServiceBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MediaManager implements ServiceBundle.ServiceBundleListener {

    private MediaManagerListener mListener;
    private static Context applicationContext = MediaApplication.getContext();
    private static ServiceBundle mediaServiceBundle;
    private static MediaManager ourInstance = new MediaManager();
    private final static String RVI_DOMAIN = "genivi.org";
    private final static String RVI_BUNDLE_NAME = "something";
    private final static ArrayList<String> localServiceIdentifiers =
            new ArrayList<>(Arrays.asList(
                    MediaServiceIdentifier.PLAY_PAUSE.value(),
                    MediaServiceIdentifier.PLAY.value()
            ));

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle, String serviceIdentifier, Object parameters) {

    }

    public static void start() {
        //stuff
        mediaServiceBundle = new ServiceBundle(applicationContext,
                RVI_DOMAIN,
                RVI_BUNDLE_NAME,
                localServiceIdentifiers);
        mediaServiceBundle.setListener(ourInstance);
    }

    public static void invokeService(String serviceId, String value) {
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("value", value);

        mediaServiceBundle.invokeService(serviceId, invokeParams, 360000);
    }

    public interface MediaManagerListener {
        void onNodeConnected();
        void onNodeDisconnected();
        void onServiceInvoked(String serviceIdentifier, Object parameters);
    }
}
