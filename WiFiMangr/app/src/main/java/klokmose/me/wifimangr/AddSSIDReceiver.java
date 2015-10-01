package klokmose.me.wifimangr;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by KlausKlokmose on 28/09/15.
 */
public class AddSSIDReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int nId = intent.getExtras().getInt("nID");
        String action = intent.getAction();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(nId);


        String ssid = null;
        try {
            ssid = intent.getExtras().getString("SSID");
            Log.d("ADdSSIDReceiver", "received: " + ssid);
            if( ssid != null ) {

            }else{
                Log.e("AddSSIDReceiver", "tried to add null");
            }
        } catch (Exception e) {
        }
        MyWifiHelper helper = new MyWifiHelper(context);

        switch (action){
            case "Add":
                helper.addSSIDtoSet(ssid, MyWifiHelper.SAVED_SSID_SET);
                Toast.makeText(context, "Will use SSID: " + ssid + " in the future", Toast.LENGTH_SHORT).show();
                break;
            case "Ignore":
                helper.addSSIDtoSet(ssid, MyWifiHelper.SAVED_SSID_SET_IGNORE);
                Toast.makeText(context, "Will ignore SSID: " + ssid + " in the future", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
