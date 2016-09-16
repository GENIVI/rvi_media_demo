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
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.jaguarlandrover.rvi.RVINode;
import com.jaguarlandrover.rvi.ServiceBundle;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class MediaManager implements ServiceBundle.ServiceBundleListener {

    private MediaManagerListener mListener;
    private static Context applicationContext = MediaApplication.getContext();
    private static ServiceBundle mediaServiceBundle;
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

    public static void invokeService(String target, String value) {
        Log.d(TAG, "Invoke " + target);
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("target", target);
        invokeParams.put("value", value);

        mediaServiceBundle.invokeService(SERVICE_ID, invokeParams, 360000);
    }

    public static void subscribeToMediaRvi() {
        //invokeService(MediaServiceIdentifier.SUBSCRIBE.value(),
        //        "{\"node\":\"" + RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/\"}");
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
        node.addJWTCredentials("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyaWdodF90b19pbnZva2UiOlsiZ2VuaXZpLm9yZy8iXSwiaXNzIjoiRGVtbyBDQSIsImRldmljZV9jZXJ0IjoiTUlJRDlqQ0NBdDZnQXdJQkFnSUJBakFOQmdrcWhraUc5dzBCQVFzRkFEQ0JoREVRTUE0R0ExVUVBd3dIUkdWdGJ5QkRRVEVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUXN3Q1FZRFZRUUdFd0pWVXpFck1Da0dDU3FHU0liM0RRRUpBUlljZEdwaGJXbHpiMjVBYW1GbmRXRnliR0Z1WkhKdmRtVnlMbU52YlRFbE1DTUdBMVVFQ2d3Y1JHVnRieUJEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVUQWVGdzB4TmpBNE1qSXlNalE0TXpoYUZ3MHhOekE0TWpJeU1qUTRNemhhTUhjeEZEQVNCZ05WQkFNTUMwUmxiVzhnUTJ4cFpXNTBNUTh3RFFZRFZRUUlEQVpQY21WbmIyNHhDekFKQmdOVkJBWVRBbFZUTVNzd0tRWUpLb1pJaHZjTkFRa0JGaHgwYW1GdGFYTnZia0JxWVdkMVlYSnNZVzVrY205MlpYSXVZMjl0TVJRd0VnWURWUVFLREF0RVpXMXZJRU5zYVdWdWREQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUFtSTJoTUFpb1JPWXR3ci9sTVlEaFdqY3RzV2poaS9DdnhjYmVRak1tSjBFQjNRRGtUZytCNnQ3OWloZ1JVOFREeDZJSGFYTldsL1RrZnhaS3doMy9leTR5b3RqTjRvUGNRVXdXdEFIUzdMby9CTkVqWW1IcTFKVkhjM2xwdmlYVHp5NUkybGhuZnBhWDdCelJNRDN3MkNBVnhRY2Jhc3pwQ1p3ZTAvaXoyTEVDQXdFQUFhT0NBUUV3Z2Y0d0NRWURWUjBUQkFJd0FEQXNCZ2xnaGtnQmh2aENBUTBFSHhZZFQzQmxibE5UVENCSFpXNWxjbUYwWldRZ1EyVnlkR2xtYVdOaGRHVXdIUVlEVlIwT0JCWUVGTVEwbGZTUGJEWFVjNXlTWjNaa1JvdjZGMUlkTUlHakJnTlZIU01FZ1pzd2daaWhnWXFrZ1ljd2dZUXhFREFPQmdOVkJBTU1CMFJsYlc4Z1EwRXhEekFOQmdOVkJBZ01Cazl5WldkdmJqRUxNQWtHQTFVRUJoTUNWVk14S3pBcEJna3Foa2lHOXcwQkNRRVdISFJxWVcxcGMyOXVRR3BoWjNWaGNteGhibVJ5YjNabGNpNWpiMjB4SlRBakJnTlZCQW9NSEVSbGJXOGdRMlZ5ZEdsbWFXTmhkR2x2YmlCQmRYUm9iM0pwZEhtQ0NRRE5iZTVMdjJJTU9EQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFXZmhpcHVHbGhmYWdEVnpjYmtQTmtLNjFrY043eW1hMXNXdFVEUjh5bHRuekVNS0pMZ3VhclhOeXJHMjB3Nm1iRk5ZOEZKSC9MQW9pNURjSzlxU0pZejNBWWtGR1V3bG9YMnNWa2p4b2o2MzF6OFFpMGx4SDlHRFFKdjdXOFBCeVZEUVpyeFZKbXpCZ0xZOEdaQWNMUDUxZmM0aWNxL3hqQ0YyWWc5WE1wekxUZC91QXFOR1N4K1Jad1dhbHR4d3BDcklYcXJFM2dYbFdCSFVCT3pKU1VSdUFhWlZhN1BEZ1VaS2tJU09zZlIvaGhORXMxMkdmbVRadDlraHF6VUxWeVNKZGxNVEQ4ekZDMTd3SmdFQXNTTXZ6Ulg4L0hsOTc5NERJRkxXM1dqbEdzVUcxOUlFejQ5MFJWL1Z6Y2k3cWhHNXMwYkpYQ2cwWXY3UDJHcytRQ2c9PSIsInZhbGlkaXR5Ijp7InN0YXJ0IjoxNDcxOTA2MjE4LCJzdG9wIjoxNTAzNDQyMjE4fSwicmlnaHRfdG9fcmVjZWl2ZSI6WyJnZW5pdmkub3JnLyJdLCJjcmVhdGVfdGltZXN0YW1wIjoxNDcxOTA2MjE4LCJpZCI6ImRlbW8tY2xpZW50In0.ZVhHpQhsFRff8tcOWzxparSVENqNnvdsE5tBVZbOU-uY2kYinkeXmwbF_KHQON77kItq1KLanRXiy0smR_7ZeV6-UMOlXePTJYmsz_gsrUbK3NglIgD0_46m1yXusedboyr847fU47f_LB92J8sqCrjulanLO0M4jcfhRxnzgxo23Kvo_6Y_XcuNU-A1hwXrPurzzrq7F6sksGDFUq4qfo9IgsmKCsbCFbW5V5i5weZXxxJitLBDjgjoEgr3Qieg-hN49VJG9RkMai3xpK4jcdjzssnM3bjlAPOnAmfEo2gumS29nfQaVE08Hg6nYotQUX1Ntid7Eqf2Ums_wg69eA");
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
        return PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }
}


