package klokmose.me.wifimangr;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by KlausKlokmose on 03/07/15.
 */
public class ListAdapter extends BaseAdapter {
    public static final String settingsString = "SSIDS";
    private final WifiManager wifi;
    //private final MyWifiHelper wifiHelper;
    private Set<String> checkedItems;
    private String[] list;
    private final LayoutInflater layoutInflator;

    public ListAdapter(Context context, String[] list, Set checkedItems) {
        if(list==null || checkedItems == null){
            throw new RuntimeException("something went wrong.");
        }

        //wifiHelper = new MyWifiHelper(context);

        this.list = list;
        this.checkedItems = checkedItems;

        layoutInflator = LayoutInflater.from(context);
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);


        update();
    }

    public void update(){
        //this.list = wifiHelper.getResultFromScan();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = layoutInflator.inflate(R.layout.list_item, null);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkBox.setText(list[position]);

        if (setContainsString(checkedItems, list[position])){
            checkBox.setChecked(true);
        }

       // checkBox.setOnCheckedChangeListener(new CompoundButton
       //       .OnCheckedChangeListener() {
       //     @Override
       //     public void onCheckedChanged(CompoundButton buttonView, boolean
       //           isChecked) {
       //         if (isChecked) {
       //             wifiHelper.addSSIDtoSet(list[position],
       //                     MyWifiHelper.SAVED_SSID_SET);
       //         } else {
       //             wifiHelper.removeSSIDfromSet(list[position],
       //                     MyWifiHelper.SAVED_SSID_SET);
       //         }
       //     }
       // });
        return convertView;
    }

    private boolean setContainsString(Set<String> set, String str){
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            String s = (String) iterator.next();
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }

}
