package klokmose.me.wifimangr;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by KlausKlokmose on 26/09/15.
 */
public class WifiHelper {
    //Constants
    public static final String SAVED_SSID_SET = "SSIDS";
    public static final String SAVED_SSID_SET_IGNORE = "SSIDS_IGNORE";
    private static final String CONNECTED_SSID = "CONNECTED_SSID";

    //fields
    private final WifiManager wifiManager;
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public WifiHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("wifimangr", 0);
        editor = sharedPreferences.edit();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean removeAllSavedSSIDs(Context context) {
        Set<String> emptySet = new HashSet<>();
        editor.putStringSet(ListAdapter.settingsString, emptySet);
        Toast.makeText(context, "Removed all saved SSIDs", Toast
                .LENGTH_SHORT).show();
        return editor.commit();
    }

    public List<String> getSavedSSIDList(String setString) {
        Set<String> savedSet = new HashSet<String>();
        Set<String> tempSet = sharedPreferences.getStringSet(setString, savedSet);
        for (String s : tempSet) {
            savedSet.add(s);
        }
        List list = new ArrayList();
        list.addAll(tempSet);
        return list;
    }

    public boolean enableWifi() {
        Log.d("WifiHelper", "ENABLE WIFI");
        return wifiManager.setWifiEnabled(true);
    }

    public boolean disableWifi() {
        Log.d("WifiHelper", "DISABLE WIFI");
        return wifiManager.setWifiEnabled(false);
    }

    public List<String> addSSIDtoSet(String ssid, String setString) {
        if (ssid != null && setString != null) {
            List s = getSavedSSIDList(setString);
            s.add(ssid);
            editor.putStringSet(setString, new HashSet<>(s));
            if (editor.commit()) {
                Log.i("WifiHelper", "Added ssid: " + ssid);
                return s;
            } else {
                Log.e("WifiHelper", "Did not add: " + ssid + "because of some unkown reason");
                return new ArrayList();
            }
        } else {
            Log.e("WifiHelper", "Did not add an ssid, because ssid was null");
           return new ArrayList();
        }
    }

    public List<String> removeSSIDfromSet(String ssid, String set) {
        if(ssid != null && set != null) {
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
            List s = getSavedSSIDList(set);
            s.remove(ssid);
            editor.putStringSet(set, new HashSet<>(s));
            if(editor.commit()){
                Log.i("WifiHelper", "Removed ssid: " + ssid);
                return s;
            }else{
                Log.e("WifiHelper", "failed at removing "+ssid);
                return new ArrayList<>();
            }
        } else{
            Log.e("WifiHelper", "SSID was "+ssid+ " and setString was "+set);
            return new ArrayList<>();
        }
    }

    public List<String> getResultFromScan() {
        List<String> scanResult = new ArrayList<>();
        List<ScanResult> search = wifiManager.getScanResults();
        for (ScanResult sr: search)
            if (!scanResult.contains(sr.SSID))
                scanResult.add(sr.SSID);
        return scanResult;
    }

    public String getConnectedSSID() {
        return wifiManager.getConnectionInfo().getSSID();
    }

    public String getLatestConnectedSSID() {
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