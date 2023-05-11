package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A {@link Fragment} subclass for displaying
 * scanned Bluetooth Devices
 */
public class DeviceListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedpreferences; // Shared Preferences Reference
    private TextView currentDeviceName; // Name of the Connected Device
    private TextView currentDeviceAddress; // Address of the Connected Device

    public DeviceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceListFragment newInstance(String param1, String param2) {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(R.id.deviceListView);
        listView.setAdapter(((MainActivity)getActivity()).getAdapter());
        listView.setOnItemClickListener(this);

        currentDeviceName = (TextView) view.findViewById(R.id.curDeviceNameText);
        currentDeviceAddress = (TextView) view.findViewById(R.id.curDeviceAddressText);

        currentDeviceName.setText("Current Device: " + sharedpreferences.getString("currentDeviceName", ((MainActivity)getActivity()).getDeviceName()));
        currentDeviceAddress.setText("Device Address: " + sharedpreferences.getString("currentDeviceAddress", ((MainActivity)getActivity()).getDeviceAddress()));

        ((Button) view.findViewById(R.id.backToHomeButton)).setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ((MainActivity) getActivity()).onItemClick(adapterView, view, i, l);

        currentDeviceName.setText("Current Device: " + sharedpreferences.getString("currentDeviceName", ((MainActivity)getActivity()).getDeviceName()));
        currentDeviceAddress.setText("Device Address: " + sharedpreferences.getString("currentDeviceAddress", ((MainActivity)getActivity()).getDeviceAddress()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backToHomeButton: // Swap fragment for HomeFragment
                MainActivity ma = ((MainActivity)getActivity());
                ma.stopScan();
                ma.replaceFragment(new HomeFragment());
                break;
            default:
                break;
        }
    }
}