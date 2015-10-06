package klokmose.me.wifimangr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class StoredSSIDActivity extends AppCompatActivity {

    //Resources
    private WifiHelper wifiHelper;

    private ListView listView;
    private ArrayAdapter adapter;

    private volatile List<String> storedSSIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_ssid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        wifiHelper = new WifiHelper(this);

        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        Collections.sort(storedSSIDs);

        adapter = new ArrayAdapter(this, android.R.layout
                .simple_list_item_1, storedSSIDs);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener
        () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                Log.d("Item", storedSSIDs.get(position));
                Toast.makeText(StoredSSIDActivity.this, "Hold press to delete",
                        Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String ssid = storedSSIDs.get(position);
                Log.d("Item", ssid);
                AlertDialog alertDialog = AskOption(ssid);
                alertDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private AlertDialog AskOption(final String ssid)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete: "+ssid+" from the stored " +
                        "list?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        adapter.remove(ssid);
                        storedSSIDs = wifiHelper.removeSSIDfromSet
                                (ssid, WifiHelper.SAVED_SSID_SET);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
