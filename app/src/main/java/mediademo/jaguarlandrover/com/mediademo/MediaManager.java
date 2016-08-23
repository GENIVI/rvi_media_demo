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

import com.jaguarlandrover.rvi.ServiceBundle;

public class MediaManager implements ServiceBundle.ServiceBundleListener {

    private MediaManagerListener mListener;

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle, String serviceIdentifier, Object parameters) {

    }

    public static void invokeService(String serviceId, String value) {
    }

    public interface MediaManagerListener {
        void onNodeConnected();
        void onNodeDisconnected();
        void onServiceInvoked(String serviceIdentifier, Object parameters);
    }
}
