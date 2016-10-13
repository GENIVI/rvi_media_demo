package mediademo.jaguarlandrover.com.mediademo;

import android.content.Context;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.jaguarlandrover.rvi.RVINode;
import com.jaguarlandrover.rvi.ServiceBundle;
import com.jaguarlandrover.rvi.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.R.attr.value;

/**
 * Created by aren on 10/11/16.
 */

public class VolumeManager implements ServiceBundle.ServiceBundleListener {
    private static Context applicationContext = MediaApplication.getContext();
    private static VolumeManager ourInstance = new VolumeManager();
    private static ServiceBundle mediaServiceBundle;

    private final static String TAG = "MediaDemo:VolumeManager";
    private final static String RVI_DOMAIN = "genivi.org";
    private final static String RVI_BUNDLE_NAME = "media";
    private final static String SERVICE_ID = "mediacontrol";

    private final static ArrayList<String> localServiceIdentifiers =
            new ArrayList<>(Arrays.asList(
                    MediaServiceIdentifier.SUBSCRIBE.value()
            ));

    @Override
    public void onServiceInvoked(ServiceBundle serviceBundle, String serviceIdentifier, Object parameters) {
        Log.d(TAG, Util.getMethodName() + "::" + serviceIdentifier + "::" + parameters.toString());
        LinkedTreeMap msg = (LinkedTreeMap) parameters;
        String target;
        if (msg.get("target") != null) {
            target = msg.get("target").toString().toLowerCase();
        } else {
            target = msg.get("signalName").toString().toLowerCase();
        }
        try {
            Object value = msg.get("value");
            Method method = this.getClass().getMethod(target, Object.class);
            method.invoke(this, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void invokeService(String target, Object value) {
        HashMap<String, Object> invokeParams = new HashMap<>(2);

        invokeParams.put("sending_node", RVI_DOMAIN + "/" + RVINode.getLocalNodeIdentifier(applicationContext) + "/");
        invokeParams.put("target", target);
        invokeParams.put("requestedValue", value);
        Log.d(TAG, "Invoke " + target + "::" + invokeParams);
        mediaServiceBundle.invokeService(SERVICE_ID, invokeParams, 360000);
    }

    public static void subscribeToMediaVolume() {
        invokeService("SUBSCRIBE", "genivi.org/android/OxCjNX1oSZqZCm8f2ObEqA/media/SUBSCRIBE");
    }

    public static void initialize() {
        mediaServiceBundle = new ServiceBundle(applicationContext,
                RVI_DOMAIN,
                RVI_BUNDLE_NAME,
                localServiceIdentifiers);
        mediaServiceBundle.setListener(ourInstance);
    }
}
