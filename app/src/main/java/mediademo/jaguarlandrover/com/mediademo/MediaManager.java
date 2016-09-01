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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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
    private static Context applicationContext = MediaApplication.getContext();
    private static ServiceBundle mediaServiceBundle;
    private static MediaManager ourInstance = new MediaManager();
    private static RVINode node;
    private final static String TAG = "MediaDemo:MediaManager";
    private final static String RVI_DOMAIN = "genivi.org";
    private final static String RVI_BUNDLE_NAME = "something";
    private final static ArrayList<String> localServiceIdentifiers =
            new ArrayList<>(Arrays.asList(
                    MediaServiceIdentifier.PLAY_PAUSE.value(),
                    MediaServiceIdentifier.PLAY.value()
            ));

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle,
                                 String serviceIdentifier,
                                 Object parameters) {

    }

    public static void setListener(Activity listener) {
        //ourInstance.mListener = listener;
    }

    public static boolean getUsingProxyServer(){
        Integer index = R.string.using_proxy_server_prefs_string;
        return getBoolFromPrefs(applicationContext.getResources().getString(index), false);
    }

    public static boolean getBoolFromPrefs(String key, Boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    public static boolean isRviConfigured() {
        if (getServerUrl() == null || getServerUrl().isEmpty()) return false;
        if (getServerPort() == 0) return false;
        if (getUsingProxyServer()) {
            if (getProxyServerUrl() == null || getProxyServerUrl().isEmpty()) return false;
            if (getProxyServerPort() == 0) return false;
        }
        return true;
    }

    public static Integer getIntFromPrefs(String key, Integer defaultValue) {
        return getPrefs().getInt(key, defaultValue);
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
    };

    public static void invokeService(String serviceId, String value) {
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("value", value);

        mediaServiceBundle.invokeService(serviceId, invokeParams, 360000);
    }

    public static void subscribeToMediaRvi() {
        invokeService(MediaServiceIdentifier.SUBSCRIBE.value(),
                "{\"node\":\"" + RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/\"}");
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
        node.addJWTCredentials("stuff");
        //stuff
        mediaServiceBundle = new ServiceBundle(applicationContext,
                RVI_DOMAIN,
                RVI_BUNDLE_NAME,
                localServiceIdentifiers);
        mediaServiceBundle.setListener(ourInstance);
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
                MediaManager.subscribeToMediaRvi();
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

    private static SharedPreferences getPrefs() {
        Integer index = R.string.media_shared_prefs_string;
        return applicationContext.getSharedPreferences(applicationContext.getString(index),
                Context.MODE_PRIVATE);
    }
}
