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
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jaguarlandrover.rvi.RVINode;
import com.jaguarlandrover.rvi.ServiceBundle;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MediaManager implements ServiceBundle.ServiceBundleListener {

    private MediaManagerListener mListener;
    private static ServiceBundle mediaServiceBundle;
    private static Context applicationContext = MediaApplication.getContext();
    private static MediaManager ourInstance = new MediaManager();
    private static RVINode node;
    private final static String TAG = "MediaDemo:MediaManager";
    private final static String RVI_DOMAIN = "genivi.org";
    private final static String RVI_BUNDLE_NAME = "media";
    private final static String SERVICE_ID = "mediacontrol";
    private final static ArrayList<String> localServiceIdentifiers =
            new ArrayList<>(Arrays.asList(
                    MediaServiceIdentifier.SUBSCRIBE.value()
            ));

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle,
                                 String serviceIdentifier,
                                 Object parameters) {
        if (mListener != null) mListener.onServiceInvoked(serviceIdentifier, parameters);
    }

    public static void setListener(MediaManagerListener listener) {
        ourInstance.mListener = listener;
    }

    public static boolean getUsingProxyServer() {
        Integer index = R.string.using_proxy_server_prefs_string;
        return getBoolFromPrefs(applicationContext.getResources().getString(index), false);
    }

    public static boolean getBoolFromPrefs(String key, Boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    public static boolean isRviConfigured() {
        if (getServerUrl() == null || getServerUrl().isEmpty()) {
            Log.d(TAG, "No Server URL");
            return false;
        }
        if (getServerPort() == 0) {
            Log.d(TAG, "No Server Port");
            return false;
        }
        if (getUsingProxyServer()) {
            if (getProxyServerUrl() == null || getProxyServerUrl().isEmpty()) return false;
            if (getProxyServerPort() == 0) return false;
        }
        return true;
    }

    public static Integer getIntFromPrefs(String key, Integer defaultValue) {
        String value = getPrefs().getString(key, null);
        if (null != value) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    public static String getStringFromPrefs(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    public static String getProxyServerUrl() {
        Integer index = R.string.proxy_server_url_prefs_string;
        String proxy_prefs = applicationContext.getResources().getString(index);
        return getStringFromPrefs(proxy_prefs, "");
    }

    public static String getServerUrl() {
        Integer index = R.string.server_url_prefs_string;
        return getStringFromPrefs(applicationContext.getResources().getString(index), null);
    }

    public static Integer getServerPort() {
        Integer index = R.string.server_port_prefs_string;
        return getIntFromPrefs(applicationContext.getResources().getString(index), 0);
    }

    public static Integer getProxyServerPort() {
        Integer index = R.string.proxy_server_port_prefs_string;
        return getIntFromPrefs(applicationContext.getResources().getString(index), 0);
    }

    public static KeyStore getKeyStore(String filename, String type, String password) throws
            IOException,
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException {
        AssetManager assetManager = applicationContext.getAssets();
        InputStream fileInputStream = assetManager.open(filename);
        KeyStore key_store = KeyStore.getInstance(type);
        key_store.load(fileInputStream, password.toCharArray());
        fileInputStream.close();
        return key_store;
    }

    public static void invokeService(String target, Object value) {
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("target", target);
        invokeParams.put("requestedValue", value);
        invokeParams.put("path", value);
        invokeParams.put("uri", value);
        invokeParams.put("pos", value);
        Log.d(TAG, "Invoke " + target + "::" + invokeParams);
        mediaServiceBundle.invokeService(SERVICE_ID, invokeParams, 360000);
    }

    public static void subscribeToMediaRvi() {
        invokeService(MediaServiceIdentifier.SUBSCRIBE.value(), "genivi.org/android/OxCjNX1oSZqZCm8f2ObEqA/media/SUBSCRIBE");
    }

    public static void start() {
        if (getUsingProxyServer()) {
            node.setServerUrl(getProxyServerUrl());
            node.setServerPort(getProxyServerPort());
        } else {
            node.setServerUrl(getServerUrl());
            node.setServerPort(getServerPort());
        }
        try {
            node.setKeyStores(getKeyStore("server-certs", "BKS", "password"),
                    getKeyStore("client.p12", "PKCS12", "password"), "password");
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        node.addJWTCredentials("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyaWdodF90b19pbnZva2UiOlsiZ2VuaXZpLm9yZyJdLCJpc3MiOiJEZW1vIiwiZGV2aWNlX2NlcnQiOiJNSUlEQnpDQ0FuQ2dBd0lCQWdJSkFJWUdBOEpIM3RiTU1BMEdDU3FHU0liM0RRRUJDd1VBTUlHQ01Rc3dDUVlEVlFRR0V3SlZVekVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUkV3RHdZRFZRUUhEQWhRYjNKMGJHRnVaREVRTUE0R0ExVUVDZ3dIUkdWdGJ5QkRRVEVRTUE0R0ExVUVBd3dIUkdWdGJ5QkRRVEVyTUNrR0NTcUdTSWIzRFFFSkFSWWNkR3BoYldsemIyNUFhbUZuZFdGeWJHRnVaSEp2ZG1WeUxtTnZiVEFlRncweE5qQTVNakl4TmpBek1qZGFGdzB4TmpFd01qSXhOakF6TWpkYU1JR0tNUXN3Q1FZRFZRUUdFd0pWVXpFUE1BMEdBMVVFQ0F3R1QzSmxaMjl1TVJFd0R3WURWUVFIREFoUWIzSjBiR0Z1WkRFVU1CSUdBMVVFQ2d3TFJHVnRieUJEYkdsbGJuUXhGREFTQmdOVkJBTU1DMFJsYlc4Z1EyeHBaVzUwTVNzd0tRWUpLb1pJaHZjTkFRa0JGaHgwYW1GdGFYTnZia0JxWVdkMVlYSnNZVzVrY205MlpYSXVZMjl0TUlHZk1BMEdDU3FHU0liM0RRRUJBUVVBQTRHTkFEQ0JpUUtCZ1FDOUFmSmdNSElEazJEanIxeW4xOUNyVEJuN0hIWWpvcjA1YjZNSWpUdFh4MEJmZ0hQUTBzTVJ0T0liS0txMnVDdDlVbHpCSU1qSHY3WjNvODRaSkdySVpWemtuL2xCd1hFWXVhSko0dkRiK0Y4SGIvRGN3aUp1WnQrRjhsZHhIUVJlMmZPckZqNnB4cmd6eXdKREhrZ0o2aHpOUXZZN3Q2NGlHLzY3RHJGYkxRSURBUUFCbzNzd2VUQUpCZ05WSFJNRUFqQUFNQ3dHQ1dDR1NBR0crRUlCRFFRZkZoMVBjR1Z1VTFOTUlFZGxibVZ5WVhSbFpDQkRaWEowYVdacFkyRjBaVEFkQmdOVkhRNEVGZ1FVTDU3VWtWK0pOUHRXU3pMMWo4TXlibHErQ0xVd0h3WURWUjBqQkJnd0ZvQVVzTXFxVll3UzBCaWwxNXZsbC9EdVN3S294NFV3RFFZSktvWklodmNOQVFFTEJRQURnWUVBRzk2WWUwQTdXdUxNQVNTbkdLQzB5aThCODdzSHBabVRNS1JwY1JJbzZYbGpVa2lyOHVwV2NkSWE0WUFXTVY0cCt0aVlGSllYRmlaOG9LU2NOajRIcnlXbWxrelh3dERUNzdiN0hhNFQxbzZxZzk2M3M0YW9OWXlkZ3Z2c2YvZVpKdzNWVmh1THN0QldFNHUvenJJdDBpRnpVN2xnSURZeEMweWVLbDhkeTJvPSIsInZhbGlkaXR5Ijp7InN0YXJ0IjoxNDc0NTYwMzEzLCJzdG9wIjoxNTA2MDk2MzEzfSwicmlnaHRfdG9fcmVjZWl2ZSI6WyJnZW5pdmkub3JnIl0sImNyZWF0ZV90aW1lc3RhbXAiOjE0NzQ1NjAzMTMsImlkIjoiZjZlMzc4MDItODkxYy00MDI2LTk1ZWMtNTg5YmE5MmUxMTBjIn0.G4fHSE1nWyqhefHfET54jaMDDPmxfA9CnpGGSqV1323MvbLnsd6P2fNQOvFudNkYHGk1vN4IthYWAaM_KFlcTnZ6wXbsO8ZA0mQyWvZSD5YFtL3yXtCgAgbyzAeLwi-BsjyaE8kT5sGPK60Y6dZGI2gGe93V-SJcNMQZbc8T2xA");
        if (null != mediaServiceBundle) {
            node.removeBundle(mediaServiceBundle);
        }
        mediaServiceBundle = new ServiceBundle(applicationContext,
                RVI_DOMAIN,
                RVI_BUNDLE_NAME,
                localServiceIdentifiers);
        mediaServiceBundle.setListener(ourInstance);
        node.addBundle(mediaServiceBundle);
        node.connect();

    }

    public interface MediaManagerListener {
        void onNodeConnected();

        void onNodeDisconnected();

        void onServiceInvoked(String serviceIdentifier, Object parameters);
    }

    private MediaManager() {
        node = new RVINode(applicationContext);

        node.setListener(new RVINode.RVINodeListener() {
            @Override
            public void nodeDidConnect() {
                Log.d(TAG, "RVI node has successfully connected");
                mListener.onNodeConnected();
            }

            @Override
            public void nodeDidFailToConnect(Throwable trigger) {
                Log.d(TAG, "RVI node has failed to connect");
                mListener.onNodeDisconnected();
            }

            @Override
            public void nodeDidDisconnect(Throwable trigger) {
                Log.d(TAG, "RVI node has disconnected");
                mListener.onNodeDisconnected();
            }
        });
    }

    public static void initializeUIState() {
        for (String get_service: MainActivityUtil.getServices) {
            MediaManager.invokeService(get_service, null);
        }
    }

    private static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }
}


