package klokmose.me.wifimangr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class FragmentSavedList extends Fragment{

    //TODO my fields
    //resources
    private WifiHelper wifiHelper;

    //xml widgets
    private ListView list;

    //lists
    private ArrayAdapter<String> adapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View myFragmentView;
    private OnFragmentInteractionListener mListener;
    private volatile List<String> storedSSIDs;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearchAndSelect.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSavedList newInstance(String param1, String param2) {
        FragmentSavedList fragment = new FragmentSavedList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSavedList() {
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

        Log.d("FRAGMENT", "TWO");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_two, container,
                false);
        //getSavedSSIDsFromPrefs();
        setupListView();
        //adapter.notifyDataSetChanged();
        return myFragmentView;
    }

    @Override
    public void onResume() {
        list.deferNotifyDataSetChanged();
        //adapter.notifyDataSetInvalidated();
        //setCheckedItemsInListView(adapter, storedSSIDs, list);
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

    private void setupListView() {
        list = (ListView)myFragmentView.findViewById(R.id.listView2);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_list_item_1, storedSSIDs);

        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String text = storedSSIDs.get
                        (position);
                storedSSIDs = wifiHelper.removeSSIDfromSet(text, WifiHelper
                        .SAVED_SSID_SET);
                adapter.remove(text);
                adapter.notifyDataSetChanged();

                //TODO notify the data set in FragmentSearchAndSelect!
                mListener.uncheckItem(text);
                return false;
            }
        });
    }

    public void updateStoredSSIDs(boolean add, String text){
        if(add){
            storedSSIDs.add(text);
            adapter.add(text);
        }else{
            storedSSIDs.remove(text);
            adapter.remove(text);
        }
        adapter.notifyDataSetChanged();
        Log.d("Update list", "update...." + storedSSIDs.size());
    }
/*
    private void getSavedSSIDsFromPrefs() {
        storedSSIDs.clear();
        storedSSIDs = wifiHelper.getSavedSSIDList(WifiHelper.SAVED_SSID_SET);
        StringBuilder sb = new StringBuilder();
        for(String s : storedSSIDs){
            sb.append(s + ", ");
        }
        Log.d("storedSSIDs", sb.toString());
    }
    */

}

