package klokmose.me.wifimangr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
    private String[] scanResults;
    private Set storedSSIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiHelper = new MyWifiHelper(this);

        tmp_savedSet = new HashSet<>();

        scanResults = wifiHelper.getResultFromScan();
        storedSSIDs = wifiHelper.getSSIDset(MyWifiHelper.SAVED_SSID_SET);
        list = (ListView)findViewById(R.id.listView);
        final ListAdapter adapter = new ListAdapter(this, scanResults, storedSSIDs);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
                if (checkBox.isChecked()) {
                    wifiHelper.addSSIDtoSet(scanResults[position], MyWifiHelper.SAVED_SSID_SET);
                } else {
                    wifiHelper.removeSSIDfromSet(scanResults[position], MyWifiHelper.SAVED_SSID_SET);
                }
            }
        });

        updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.update();
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
        setTextViewWithSavedSSIDs();
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
