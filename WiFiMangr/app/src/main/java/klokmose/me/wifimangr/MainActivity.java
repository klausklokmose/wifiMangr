package klokmose.me.wifimangr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //resources
    private MyWifiHelper wifiHelper;
    //used for showing all saved ssids in a list (temporary)
    private HashSet<String> tmp_savedSet;

    //xml widgets
    private ListView list;
    private Button updateButton;
    private String[] scanResults = new String[0];
    private Set storedSSIDs = new HashSet(0);
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiHelper = new MyWifiHelper(this);

        tmp_savedSet = new HashSet<>();

        setupListView();

        updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestResources();
                listAdapter.update(scanResults, storedSSIDs);
                setTextViewWithSavedSSIDs();
            }
        });

        Button clearButton = (Button)findViewById(R.id.button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiHelper.removeAllSavedSSIDs(getBaseContext());
                setTextViewWithSavedSSIDs();
            }
        });

        boolean ok = getSavedSSIDsFromPrefs();
        if(ok) {
            //if current list contains an SSID from saved tmp_savedSet, then turn on WiFi
            if (listContainsOneOfSetItems(wifiHelper.getLatestScanResult(), tmp_savedSet)) {
                //TODO wifi on
                wifiHelper.enableWifi();
            } else {
                //TODO wifi off
                wifiHelper.disableWifi();
            }
        }
    }

    private void setupListView() {
        //views
        list = (ListView)findViewById(R.id.listView);
        list.setItemsCanFocus(true);
        //onclick
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder)v.getTag();
                holder.isChecked = !holder.isChecked; //switch

                String text = (String) holder.checkBox.getText();
                Log.i("Main", "isChecked: " + holder.isChecked);

                if (holder.checkBox.isChecked()) {
                    storedSSIDs = wifiHelper.addSSIDtoSet
                            (text, MyWifiHelper.SAVED_SSID_SET);
                } else {
                    storedSSIDs = wifiHelper.removeSSIDfromSet
                            (text, MyWifiHelper.SAVED_SSID_SET);
                }
                getSavedSSIDsFromPrefs();
                setTextViewWithSavedSSIDs();
            }
        };
        listAdapter = new ListAdapter(this, scanResults, storedSSIDs, clickListener);
        list.setAdapter(listAdapter);

    }

    private void setTextViewWithSavedSSIDs() {
        String s = Arrays.deepToString(tmp_savedSet.toArray());
        TextView textView = (TextView)findViewById(R.id.text);
        textView.setText("Saved SSIDS\n" + s);
    }

    private boolean getSavedSSIDsFromPrefs() {
        tmp_savedSet.clear();
        tmp_savedSet.addAll(wifiHelper.getSSIDset(MyWifiHelper.SAVED_SSID_SET));
        return true;
    }

    private boolean listContainsOneOfSetItems(String[] ss, Set<String> savedSet){
        if(ss != null){
            for(String id : ss){
                if(savedSet.contains(id))
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        getLatestResources();

        listAdapter.update(scanResults, storedSSIDs);

        setTextViewWithSavedSSIDs();

        super.onResume();
    }

    private void getLatestResources() {
        scanResults = wifiHelper.getResultFromScan();
        storedSSIDs = wifiHelper.getSSIDset(MyWifiHelper.SAVED_SSID_SET);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
