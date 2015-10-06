package klokmose.me.wifimangr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by KlausKlokmose on 22/09/15.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private WifiHelper wifiHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO do something
        wifiHelper = new WifiHelper(context);

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
        return !wifiHelper.getSavedSSIDList(WifiHelper
                        .SAVED_SSID_SET).contains(ssid) &&
                isSSIDNotOnAvoidList(ssid);
    }

    private boolean shouldTurnOnWifi() {
        List set = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
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

    public static final String ADD = "ADD";
    public static final String IGNORE = "IGNORE";

    private void buildNotifictation(Context context, String message, String SSID){
        int notificationID = nextID(SSID);

        if(SSID != null) {
            Intent addIntent = new Intent(context, AddSSIDReceiver.class)
                .putExtra("SSID", SSID)
                .putExtra("nID", notificationID)
                    .setAction(ADD)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntentADD = PendingIntent.getBroadcast(
                    context, notificationID, addIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Intent ignoreIntent = new Intent(context, AddSSIDReceiver.class)
                    .putExtra("SSID", SSID)
                    .putExtra("nID", notificationID)
                    .setAction(IGNORE)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntentIgnore = PendingIntent.getBroadcast
                    (context, notificationID, ignoreIntent, PendingIntent
                            .FLAG_CANCEL_CURRENT);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] vibrate = { 0, 100, 200, 300 };

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            Notification notification = mBuilder.setSmallIcon(R.drawable.notification_template_icon_bg)
                    .setTicker("MY APP").setWhen(0)
                    .setContentTitle("My app")
                    .setSound(alarmSound)
                    .setLights(Color.BLACK, 500, 500)
                    .setVibrate(vibrate)
                    //.setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .addAction(R.drawable.abc_tab_indicator_mtrl_alpha, ADD,
                            pendingIntentADD)
                    .addAction(R.drawable.abc_tab_indicator_material, IGNORE,
                            pendingIntentIgnore).build();

            notification.flags |= Notification.FLAG_NO_CLEAR;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, notification);
        }
    }
}
