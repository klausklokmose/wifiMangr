package klokmose.me.wifimangr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by KlausKlokmose on 22/09/15.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private MyWifiHelper wifiHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO do something
        wifiHelper = new MyWifiHelper(context);

        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            /**
                1. if network is NOT known by this system,
                    then save its information and ask the user (in a notification),
                    if he wants to save this SSID in the app for future use. He can also choose to put the SSID on the ignore list.
                2. else just save a log that the phone was connected at the given local time.
             */
            //
            if(networkInfo.isConnected()) {
                // Wifi is connected
                Log.d("Inetify", "Wifi is connected: " + String.valueOf(networkInfo));
                String latetsConnectedSSID = wifiHelper.getLatetsConnectedSSID();

                String connectedSSID = wifiHelper.getConnectedSSID(context).replaceAll("\"", "");
                if(connectedSSID == null){
                    Log.d("BroadcastReceiver", "found null SSID");
                }//am I already connected to this one?
                else if(latetsConnectedSSID == null
                        || (latetsConnectedSSID != null && !latetsConnectedSSID.equals(connectedSSID))){
                    Log.d("SSID connected", connectedSSID);
                    wifiHelper.setConnectedSSID(connectedSSID);

                    if(isSSIDNotKnown(connectedSSID)){ //@avoid and @saved
                        String message = "found new SSID: " + connectedSSID + "\nDo you want to add it to saved hotspots?";

                        //TODO built it and handle it
                        buildNotifictation(context, message, connectedSSID);
                    }

                }
            }else{
                //Wifi is not connected
                Log.d("Inetify", "Wifi is not connected: " + String.valueOf(networkInfo));
                wifiHelper.setConnectedSSID(null);
                // 1. whenever a connection is lost, scan for results to see of there is nothing it should connect to after a 5 second delay.
                //TODO maybe a bad idea, because the user might WANT to disable wifi
               // Thread t = new Thread(new Runnable() {
               //     @Override
               //     public void run() {
               //         try {
               //             Thread.sleep(5000); //waits 5 seconds until it scans again
               //         } catch (InterruptedException e) {
               //             e.printStackTrace();
               //         }
               //         //TODO scan and decide
               //         // 1.1. if there is - keep wifi on
               //         if(shouldTurnOnWifi()){
               //             wifiHelper.enableWifi();
               //         }
               //     }
               // });
               // t.start();

               // 1.2 if there isn't - disable wifi and start timer for next control
                //wifiHelper.disableWifi();
            }
        }
        else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                    ! networkInfo.isConnected()) {
                // Wifi is disconnected
                Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(networkInfo));
                wifiHelper.setConnectedSSID(null);
            }
        }
    }

    private boolean isSSIDNotOnAvoidList(String connectedSSID) {
        //TODO get avoid list from shared prefs
        String[] avoid = new String[]{"<unknown ssid>"};

        for (int i = 0; i < avoid.length; i++) {
            if(connectedSSID.equals(avoid[i])){
               return false;
            }
        }
        return true;
    }

    private boolean isSSIDNotKnown(String ssid) {
        return !setContainsString(wifiHelper.getSSIDset(MyWifiHelper.SAVED_SSID_SET),
                ssid) && isSSIDNotOnAvoidList(ssid);
    }

    private boolean shouldTurnOnWifi() {
        Set set = wifiHelper.getSSIDset(MyWifiHelper.SAVED_SSID_SET);
        for (String str : wifiHelper.getResultFromScan()){
            if(set.contains(str)){
                return true;
            }
        }
        return false;
    }

    private int nextID(String ssid){
        return ssid.hashCode();
    }


    private void buildNotifictation(Context context, String message, String SSID){
        int notificationID = nextID(SSID);

        if(SSID != null) {
            Intent resultIntent = new Intent(context, AddSSIDReceiver.class)
                .putExtra("SSID", SSID)
                .putExtra("nID", notificationID)
                    .setAction(SSID)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                    context, notificationID, resultIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            //Intent fakeIntent = new Intent(context, AddSSIDReceiver.class);
            //fakeIntent.putExtra("nID", notificationID);

            //PendingIntent fakePendingIntent = PendingIntent.getBroadcast(
            //        context, notificationID, fakeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            Notification notification = mBuilder.setSmallIcon(R.drawable.notification_template_icon_bg)
                    .setTicker("MY APP").setWhen(0)
                    .setContentTitle("My app")
                    //.setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .addAction(R.drawable.abc_tab_indicator_mtrl_alpha, "Add", resultPendingIntent)
                    .addAction(R.drawable.abc_tab_indicator_material, "Ignore", resultPendingIntent).build();

            notification.flags |= Notification.FLAG_NO_CLEAR;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, notification);
        }
    }

    private boolean setContainsString(Set<String> set, String str){
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            String s = (String) iterator.next();
            //Log.d("COMPARE", s + " AND " + str);
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }
}
