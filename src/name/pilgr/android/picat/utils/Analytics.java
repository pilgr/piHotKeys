package name.pilgr.android.picat.utils;

import android.content.Context;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

public class Analytics {

    public static void startSession(Context ctx) {
        FlurryAgent.onStartSession(ctx, PrivateKeys.FLURRY_API_KEY);
    }

    public static void endSession(Context ctx) {
        FlurryAgent.onEndSession(ctx);
    }

    public static void trackOpenMainActivity() {
        FlurryAgent.onEvent("Main. Open");
    }

    public static void trackIsConnected(boolean isConnected) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("isConnected", Boolean.toString(isConnected));
        FlurryAgent.logEvent("Is there a connection at the end of the session?", map);
    }

    public static void trackSupportedApp(String appName) {
        if (appName == null || appName.length() == 0) return;

        Map<String, String> map = new HashMap<String, String>();
        map.put("app", appName);
        FlurryAgent.logEvent("Activates supported pc apps", map);
    }

    public static void trackReducedProcName(String procName) {
        if (procName == null) return;
        Map<String, String> map = new HashMap<String, String>();

        //Track only the last 25 symbols
        procName = procName.length() < 25 ? procName : procName.substring(procName.length() - 25);
        map.put("procName", procName);
        FlurryAgent.logEvent("All app headers", map);
    }

    public static void trackOpenDonateActivity() {
        FlurryAgent.onEvent("Donate. Open");
    }

    public static void trackClickDonateButton(String amount) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.clear();
        params.put("amount", amount);
        FlurryAgent.onEvent("Donate. Make", params);
    }

    public static void trackPurchaseStateChanges(String itemId, String state) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.clear();
        params.put("itemID", itemId);
        params.put("state", state);
        params.put("purchase", itemId + "|" + state);
        FlurryAgent.onEvent("Donate. Finish", params);
    }


    public static void trackConnected(String connType) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("connectionType", connType);
        FlurryAgent.logEvent("Connected", map);
    }

    public void trackShowRateDialog(boolean rated) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.clear();
        params.put("Agreed to rate", String.valueOf(rated));
        FlurryAgent.onEvent("Rate app. Dialog", params);
    }

}
