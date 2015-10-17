package klokmose.me.wifimangr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneFragment extends Fragment {

    //TODO my fields
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


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View myFragmentView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OneFragment newInstance(String param1, String param2) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //OLD
        wifiHelper = new WifiHelper(getActivity());

        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        scanResults = wifiHelper.getResultFromScan();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_one, container,
                false);
        setupListView();
        getLatestLists();
        adapter.notifyDataSetChanged();
        setTextViewWithSavedSSIDs();
        setCheckedItemsInListView(adapter, storedSSIDs, list);
        setupButtons();

        return myFragmentView;
    }

    @Override
    public void onResume() {
        getLatestLists();
        adapter.notifyDataSetChanged();
        setTextViewWithSavedSSIDs();
        setCheckedItemsInListView(adapter, storedSSIDs, list);
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setupButtons() {
        updateButton = (Button) myFragmentView.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestLists();
                //listAdapter.update(scanResults, storedSSIDs);
                setTextViewWithSavedSSIDs();
            }
        });

        clearButton = (Button)myFragmentView.findViewById(R.id.button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiHelper.removeAllSavedSSIDs(getActivity().getBaseContext());
                setTextViewWithSavedSSIDs();
            }
        });
    }

    private void setupListView() {
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_list_item_multiple_choice, scanResults);

        list = (ListView)myFragmentView.findViewById(R.id.listView);
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

    }

    private static void setCheckedItemsInListView(ArrayAdapter<String> adapter, List<String> storedSSIDs, ListView listView) {
        for (int i = 0; i  < adapter.getCount(); i++) {
            if(storedSSIDs.contains(adapter.getItem(i))){
                Log.d("CHECK", "SET CHECKED: " + adapter.getItem(i));
                listView.setItemChecked(i, true);
            }else{
                listView.setItemChecked(i, false);
            }
        }
    }

    private void flipCheckBox(CheckedTextView checkedTextView) {
        checkedTextView.setChecked(!checkedTextView.isChecked());
    }

    private void setTextViewWithSavedSSIDs() {
        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        String s = Arrays.deepToString(storedSSIDs.toArray());
        TextView textView = (TextView)myFragmentView.findViewById(R.id.text);
        textView.setText("Saved SSIDS\n" + s);
    }

    private void getSavedSSIDsFromPrefs() {
        storedSSIDs.clear();
        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
    }

    private void getLatestLists() {
        scanResults = wifiHelper.getResultFromScan();
        getSavedSSIDsFromPrefs();
    }

}

