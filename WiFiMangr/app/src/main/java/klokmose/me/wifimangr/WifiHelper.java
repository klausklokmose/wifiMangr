package klokmose.me.wifimangr;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<String> latestScanResult;
    private List<ScanResult> search;


    public WifiHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("wifimangr", 0);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        latestScanResult = new ArrayList<>();
    }

    public boolean removeAllSavedSSIDs(Context context) {
        editor = sharedPreferences.edit();
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
        //return savedSet;
        return list;
    }

    public List getSSIDList(String setString){
        List list = new ArrayList();
        list.addAll(getSavedSSIDList(setString));
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
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
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

    public List<String> removeSSIDfromSet(String ssid, String setString) {
        if(ssid != null && setString != null) {
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
            List s = getSavedSSIDList(setString);
            s.remove(ssid);
            editor.putStringSet(setString, new HashSet<>(s));
            if(editor.commit()){
                Log.i("WifiHelper", "Removed ssid: " + ssid);
                return s;
            }else{
                Log.e("WifiHelper", "failed at removing "+ssid);
                return new ArrayList<>();
            }
        } else{
            return new ArrayList<>();
        }
    }

    public List<String> getResultFromScan() {
        if (wifiManager != null) {
            search = wifiManager.getScanResults();

            latestScanResult.clear();
            for (ScanResult sr: search) {
                if(!latestScanResult.contains(sr.SSID)){
                    latestScanResult.add(sr.SSID);
                }
            }
            return latestScanResult;
        } else {
            return latestScanResult;
        }
    }

    public List<String> getLatestScanResult() {
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