package klokmose.me.wifimangr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //resources
    private WifiHelper wifiHelper;

    //xml widgets
    private ListView list;
    private Button updateButton;
    private Button clearButton;

    //lists
    private List<String> storedSSIDs = new ArrayList<>();
    private List<String> scanResults;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiHelper = new WifiHelper(this);

        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        setupListView();

        setupButtons();

        boolean ok = getSavedSSIDsFromPrefs();

    }

    private void setupButtons() {
        updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestLists();
                //listAdapter.update(scanResults, storedSSIDs);
                setTextViewWithSavedSSIDs();
            }
        });

        clearButton = (Button)findViewById(R.id.button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiHelper.removeAllSavedSSIDs(getBaseContext());
                setTextViewWithSavedSSIDs();
            }
        });
    }

    private void setupListView() {
        scanResults = wifiHelper.getResultFromScan();
        getSavedSSIDsFromPrefs();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, scanResults);
        list = (ListView)findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CLICK", "User clicked on list item at position: " + position);

                CheckedTextView checkedTextView = ((CheckedTextView) view);
                String text = checkedTextView.getText().toString();
                if (checkedTextView.isChecked()) {
                    //ADD
                    scanResults = wifiHelper.addSSIDtoSet
                            (text, WifiHelper.SAVED_SSID_SET);
                } else {
                    //REMOVE
                    scanResults = wifiHelper.removeSSIDfromSet
                            (text, WifiHelper.SAVED_SSID_SET);
                }
                setTextViewWithSavedSSIDs();
                flipCheckBox(checkedTextView);
            }
        });
        setCheckedItemsInListView(adapter, storedSSIDs, list);
    }

    private static void setCheckedItemsInListView(ArrayAdapter<String> adapter, List<String> storedSSIDs, ListView listView) {
        for (int i = 0; i  < adapter.getCount(); i++) {
            if(storedSSIDs.contains(adapter.getItem(i))){
                Log.d("CHECK", "SET CHECKED: " + adapter.getItem(i));
                listView.setItemChecked(i, true);
            }
        }
    }

    private void flipCheckBox(CheckedTextView checkedTextView) {
        checkedTextView.setChecked(!checkedTextView.isChecked());
    }

    private void setTextViewWithSavedSSIDs() {
        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        String s = Arrays.deepToString(storedSSIDs.toArray());
        TextView textView = (TextView)findViewById(R.id.text);
        textView.setText("Saved SSIDS\n" + s);
    }

    private boolean getSavedSSIDsFromPrefs() {
        storedSSIDs.clear();
        storedSSIDs.addAll(wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET));
        return true;
    }

    private boolean listContainsOneOfSetItems(List<String> ss, List<String>
            savedSet){
        for (String s: ss) {
            if(savedSet.contains(s)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        getLatestLists();
        setTextViewWithSavedSSIDs();
        super.onResume();
    }

    private void getLatestLists() {
        scanResults = wifiHelper.getResultFromScan();
        getSavedSSIDsFromPrefs();
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
            Intent intent = new Intent(this, StoredSSIDActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
