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
        WifiHelper helper = new WifiHelper(context);

        switch (intent.getAction()){
            case MyBroadcastReceiver.ADD:
                helper.addSSIDtoSet(ssid, WifiHelper.SAVED_SSID_SET);
                Toast.makeText(context, "Will use SSID: " + ssid + " in the future", Toast.LENGTH_SHORT).show();
                break;
            case MyBroadcastReceiver.IGNORE:
                helper.addSSIDtoSet(ssid, WifiHelper.SAVED_SSID_SET_IGNORE);
                Toast.makeText(context, "Will ignore SSID: " + ssid + " in the future", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.e("AddSSIDReceiver", "default case. Nothing was added to " +
                        "anything. action was: \""+intent.getAction()+"\"");
                break;
        }
    }
}
