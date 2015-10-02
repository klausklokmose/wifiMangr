package klokmose.me.wifimangr;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by KlausKlokmose on 03/07/15.
 */
public class ListAdapter extends BaseAdapter {
    public static final String settingsString = "SSIDS";
    private WifiManager wifi;
    private View.OnClickListener onClickListener;
    //private final MyWifiHelper wifiHelper;
    private Set<String> checkedItems;
    private String[] list;
    private LayoutInflater layoutInflator;

    public ListAdapter(Context context, String[] list, Set checkedItems,
                       View.OnClickListener onClickListener) {
        if (list == null || checkedItems == null) {
            throw new RuntimeException("something went wrong.");
        }
        this.onClickListener = onClickListener;
        this.list = list;
        this.checkedItems = checkedItems;

        layoutInflator = LayoutInflater.from(context);
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);


        update(list, checkedItems);
    }

    public void update(String[] list, Set checkedItems) {
        this.list = list;
        this.checkedItems = checkedItems;
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
       final ViewHolder holder = new ViewHolder();

        convertView = layoutInflator.inflate(R.layout.list_item, null);
        holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        convertView.setTag(holder);

        holder.checkBox.setText(list[position]);
        if (setContainsString(checkedItems, list[position])) {
            holder.checkBox.setChecked(true);
        }
        final View finalConvertView = convertView;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onClickListener.onClick(finalConvertView);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private boolean setContainsString(Set<String> set, String str) {
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }

}
