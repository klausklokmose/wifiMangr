package klokmose.me.wifimangr;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by KlausKlokmose on 26/09/15.
 */
public class MyWifiHelper {
    //Constants
    public static final String SAVED_SSID_SET = "SSIDS";
    public static final String SAVED_SSID_SET_IGNORE = "SSIDS_IGNORE";
    private static final String CONNECTED_SSID = "CONNECTED_SSID";

    //fields
    private final WifiManager wifiManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String[] latestScanResult;


    public MyWifiHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("wifimangr", 0);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean removeAllSavedSSIDs(Context context) {
        editor = sharedPreferences.edit();
        Set<String> emptySet = new HashSet<>();
        editor.putStringSet(ListAdapter.settingsString, emptySet);
        Toast.makeText(context, "Removed all saved SSIDs", Toast
                .LENGTH_SHORT).show();
        return editor.commit();
    }

    public Set getSSIDset(String setString) {
        Set savedSet = new HashSet();
        Set<String> tempSet = sharedPreferences.getStringSet(setString, savedSet);
        for (String s : tempSet) {
            savedSet.add(s);
        }
        return savedSet;
    }

    public boolean enableWifi() {
        Log.d("WifiHelper", "ENABLE WIFI");
        return wifiManager.setWifiEnabled(true);
    }

    public boolean disableWifi() {
        Log.d("WifiHelper", "DISABLE WIFI");
        return wifiManager.setWifiEnabled(false);
    }

    public Set addSSIDtoSet(String ssid, String setString) {
        if (ssid != null) {
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
            Set s = getSSIDset(setString);
            s.add(ssid);
            editor.putStringSet(setString, s);
            boolean commited = editor.commit();
            if (commited) {
                Log.i("WifiHelper", "Added ssid: " + ssid);
                return s;
            } else {
                Log.e("WifiHelper", "Did not add: " + ssid + "because of some unkown reason");
                return new HashSet(0);
            }
        } else {
            Log.e("WifiHelper", "Did not add an ssid, because ssid was null");
           return new HashSet(0);
        }
    }

    public void removeSSIDfromSet(String str, String setString) {
        if(str != null || setString != null) {
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
            Set s = getSSIDset(setString);
            s.remove(str);
            editor.putStringSet(setString, s);
            editor.commit();
        }
    }

    public String[] getResultFromScan() {
        if (wifiManager != null) {
            List<ScanResult> search = wifiManager.getScanResults();
            String[] scanResults = new String[search.size()];

            for (int i = 0; i < search.size(); i++) {
                scanResults[i] = search.get(i).SSID;
            }
            latestScanResult = scanResults.clone();

            //removes duplicates for better user experience
            return new HashSet<>(Arrays.asList(scanResults)).toArray(new String[0]);
        } else {
            return new String[]{};
        }
    }

    public String[] getLatestScanResult() {
        return latestScanResult;
    }

    public String getConnectedSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    public String getLatetsConnectedSSID() {
        return sharedPreferences.getString(CONNECTED_SSID, null);
    }

    public void setConnectedSSID(String ssid) {
        if (ssid != null) {
            sharedPreferences.edit().putString(CONNECTED_SSID, ssid).commit();
        } else {
            sharedPreferences.edit().putString(CONNECTED_SSID, null).commit();
        }
    }
}