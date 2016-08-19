package jlr.demo.media.rvi.rvi_media_demo;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    MediaManager.java
 * Project: HVACDemo
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;
import com.google.gson.internal.LinkedTreeMap;
import com.jaguarlandrover.rvi.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class MediaManager implements ServiceBundle.ServiceBundleListener
{
    private final static String TAG = "MediaDemo:MediaManager";

    private final static String RVI_DOMAIN      = "genivi.org";
    private final static String RVI_BUNDLE_NAME = "media";

    private static Context applicationContext = RviMediaDemo.getContext();
    private static ServiceBundle mediaServiceBundle;

    private static HVACManager ourInstance = new HVACManager();

    private static RVINode node;

    private final static ArrayList<String> localServiceIdentifiers =
            new ArrayList<>(Arrays.asList(
                    HVACServiceIdentifier.HAZARD.value(),
                    HVACServiceIdentifier.TEMP_LEFT.value(),
                    HVACServiceIdentifier.TEMP_RIGHT.value(),
                    HVACServiceIdentifier.SEAT_HEAT_LEFT.value(),
                    HVACServiceIdentifier.SEAT_HEAT_RIGHT.value(),
                    HVACServiceIdentifier.FAN_SPEED.value(),
                    HVACServiceIdentifier.AIRFLOW_DIRECTION.value(),
                    HVACServiceIdentifier.DEFROST_REAR.value(),
                    HVACServiceIdentifier.DEFROST_FRONT.value(),
                    HVACServiceIdentifier.DEFROST_MAX.value(),
                    HVACServiceIdentifier.AIR_CIRC.value(),
                    HVACServiceIdentifier.AC.value(),
                    HVACServiceIdentifier.AUTO.value()//,
                    //HVACServiceIdentifier.SUBSCRIBE.value(),
                    //HVACServiceIdentifier.UNSUBSCRIBE.value()
            ));

    private HVACManagerListener mListener;

    public interface HVACManagerListener
    {
        void onNodeConnected();

        void onNodeDisconnected();

        void onServiceInvoked(String serviceIdentifier, Object parameters);
    }

    private HVACManager() {
        node = new RVINode(applicationContext);

        node.setListener(new RVINode.RVINodeListener()
        {
            @Override
            public void nodeDidConnect() {
                Log.d(TAG, "RVI node has successfully connected.");
                mListener.onNodeConnected();
                HVACManager.subscribeToHvacRvi();
            }

            @Override
            public void nodeDidFailToConnect(Throwable trigger) {
                Log.d(TAG, "RVI node failed to connect: " + ((trigger == null) ? "(null)" : trigger.getLocalizedMessage()));
                mListener.onNodeDisconnected();
            }

            @Override
            public void nodeDidDisconnect(Throwable trigger) {
                Log.d(TAG, "RVI node did disconnect: " + ((trigger == null) ? "(null)" : trigger.getLocalizedMessage()));
                mListener.onNodeDisconnected();
            }
        });
    }

    private static SharedPreferences getPrefs() {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.hvac_shared_prefs_string), MODE_PRIVATE);
    }

    private static String getStringFromPrefs(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    private static Integer getIntFromPrefs(String key, Integer defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    private static Boolean getBoolFromPrefs(String key, Boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    private static void putStringInPrefs(String key, String value) {
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void putIntInPrefs(String key, Integer value) {
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void putBoolInPrefs(String key, Boolean value) {
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

//    public static String getVin() {
//        return getStringFromPrefs(applicationContext.getResources().getString(R.string.vehicle_vin_prefs_string), "");
//    }
//
//    public static void setVin(String vin) {
//        putStringInPrefs(applicationContext.getString(R.string.vehicle_vin_prefs_string), vin);
//
//        if (hvacServiceBundle != null)
//            hvacServiceBundle.setRemotePrefix(vin);
//    }

    public static String getServerUrl() {
        return getStringFromPrefs(applicationContext.getResources().getString(R.string.server_url_prefs_string), "");
    }

    public static void setServerUrl(String serverUrl) {
        putStringInPrefs(applicationContext.getString(R.string.server_url_prefs_string), serverUrl);

        //RemoteConnectionManager.setServerUrl(serverUrl);
    }

    public static Integer getServerPort() {
        return getIntFromPrefs(applicationContext.getResources().getString(R.string.server_port_prefs_string), 0);
    }

    public static void setServerPort(Integer serverPort) {
        putIntInPrefs(applicationContext.getString(R.string.server_port_prefs_string), serverPort);

        //RemoteConnectionManager.setServerPort(serverPort);
    }

    public static String getProxyServerUrl() {
        return getStringFromPrefs(applicationContext.getResources()
                                                    .getString(R.string.proxy_server_url_prefs_string), "");
    }

    public static void setProxyServerUrl(String proxyUrl) {
        putStringInPrefs(applicationContext.getString(R.string.proxy_server_url_prefs_string), proxyUrl);

        //RemoteConnectionManager.setProxyServerUrl(proxyUrl);
    }

    public static Integer getProxyServerPort() {
        return getIntFromPrefs(applicationContext.getResources().getString(R.string.proxy_server_port_prefs_string), 0);
    }

    public static void setProxyServerPort(Integer proxyPort) {
        putIntInPrefs(applicationContext.getString(R.string.proxy_server_port_prefs_string), proxyPort);

        //RemoteConnectionManager.setProxyServerPort(proxyPort);
    }

    public static boolean getUsingProxyServer() {
        return getBoolFromPrefs(applicationContext.getResources()
                                                  .getString(R.string.using_proxy_server_prefs_string), false);
    }

    public static void setUsingProxyServer(boolean usingProxyServer) {
        putBoolInPrefs(applicationContext.getString(R.string.using_proxy_server_prefs_string), usingProxyServer);

        //RemoteConnectionManager.setUsingProxyServer(usingProxyServer);
    }

    public static boolean isRviConfigured() {
        //if (getVin()        == null || getVin().isEmpty())       return false;
        if (getServerUrl()  == null || getServerUrl().isEmpty()) return false;
        if (getServerPort() == 0)                                return false;

        if (getUsingProxyServer()) {
            if (getProxyServerUrl()  == null || getProxyServerUrl().isEmpty()) return false;
            if (getProxyServerPort() == 0)                                     return false;
        }

        return true;
    }

    private static KeyStore getKeyStore(String fileName, String type, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException { // type = "jks"?
        AssetManager assetManager = applicationContext.getAssets();
        InputStream fis = assetManager.open(fileName);

        KeyStore ks = KeyStore.getInstance(type);
        ks.load(fis, password.toCharArray());
        fis.close();

        return ks;
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
            node.setKeyStores(getKeyStore("server-certs", "BKS", "password"), getKeyStore("client.p12", "PKCS12", "password"), "password");
        } catch (Exception e) {
            e.printStackTrace();
        }

        node.addJWTCredentials("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyaWdodF90b19pbnZva2UiOlsiZ2VuaXZpLm9yZyJdLCJpc3MiOiJqbHIuY29tIiwiZGV2aWNlX2NlcnQiOiJNSUlCOHpDQ0FWd0NBUUV3RFFZSktvWklodmNOQVFFTEJRQXdRakVMTUFrR0ExVUVCaE1DVlZNeER6QU5CZ05WQkFnTUJrOXlaV2R2YmpFUk1BOEdBMVVFQnd3SVVHOXlkR3hoYm1ReER6QU5CZ05WQkFvTUJrZEZUa2xXU1RBZUZ3MHhOVEV4TWpjeU16RTBOVEphRncweE5qRXhNall5TXpFME5USmFNRUl4Q3pBSkJnTlZCQVlUQWxWVE1ROHdEUVlEVlFRSURBWlBjbVZuYjI0eEVUQVBCZ05WQkFjTUNGQnZjblJzWVc1a01ROHdEUVlEVlFRS0RBWkhSVTVKVmtrd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFKdHZpTThBUklyRnF1UGMwbXlCOUJ1RjlNZGtBLzJTYXRxYlpNV2VUT1VKSEdyakJERUVNTFE3ems4QXlCbWk3UnF1WVlaczY3U3lMaHlsVkdLaDZzSkFsZWN4YkhVd2o3Y1pTUzFibUtNamU2TDYxZ0t3eEJtMk5JRlUxY1ZsMmpKbFRhVTlWWWhNNHhrNTd5ajI4bmtOeFNZV1AxdmJGWDJORFgyaUg3YjVBZ01CQUFFd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ1lFQWhicVZyOUUvME03MjluYzZESStxZ3FzUlNNZm95dkEzQ21uL0VDeGwxeWJHa3V6TzdzQjhmR2pnTVE5enpjYjZxMXVQM3dHalBpb3FNeW1pWVlqVW1DVHZ6ZHZSQlorNlNEanJaZndVdVlleGlLcUk5QVA2WEthSGxBTDE0K3JLKzZITjR1SWtaY0l6UHdTTUhpaDFic1RScHlZNVozQ1VEY0RKa1l0VmJZcz0iLCJ2YWxpZGl0eSI6eyJzdGFydCI6MTQ1MjE5Mjc3Nywic3RvcCI6MTQ4MzcyODc3N30sInJpZ2h0X3RvX3JlZ2lzdGVyIjpbImdlbml2aS5vcmciXSwiY3JlYXRlX3RpbWVzdGFtcCI6MTQ1MjE5Mjc3NywiaWQiOiJpbnNlY3VyZV9jcmVkZW50aWFscyJ9.TBDUJFL1IQ039Lz7SIkcblhz62jO35STJ8OiclL_xlxEE_L_EjnELrDOGvkIh7zhhl8RMHkUJcTFQKF7P6WDJ5rUJejXJlkTRf-aVmHqEhpspRw6xD2u_2A9wmTWLJF94_wsEb7M7xWCXVrbexu_oik85zmuxRQgRE5wrTC7DDQ");

        if (hvacServiceBundle != null)
            node.removeBundle(hvacServiceBundle);

        hvacServiceBundle = new ServiceBundle(applicationContext, RVI_DOMAIN, RVI_BUNDLE_NAME, localServiceIdentifiers);
        hvacServiceBundle.setListener(ourInstance);

        node.addBundle(hvacServiceBundle);
        node.connect();
    }

    public static void restart() {
        node.disconnect();
        node.connect();
    }

    public static void subscribeToHvacRvi() {
        invokeService(HVACServiceIdentifier.SUBSCRIBE.value(),
                "{\"node\":\"" + RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/\"}");
    }

    public static void invokeService(String serviceIdentifier, String value) {
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("value", value);

        hvacServiceBundle.invokeService(serviceIdentifier, invokeParams, 360000);
    }

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle, String serviceIdentifier, Object parameters) {
        if (mListener != null) mListener.onServiceInvoked(serviceIdentifier, ((LinkedTreeMap) parameters).get("value"));
    }

    public static HVACManagerListener getListener() {
        return ourInstance.mListener;
    }

    public static void setListener(HVACManagerListener listener) {
        ourInstance.mListener = listener;
    }
}
