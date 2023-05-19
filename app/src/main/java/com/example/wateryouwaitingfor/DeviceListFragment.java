package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A {@link Fragment} subclass for displaying
 * scanned Bluetooth Devices
 */
public class DeviceListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private SharedPreferences sharedpreferences; // Shared Preferences Reference
    private TextView currentDeviceName; // Name of the Connected Device
    private TextView currentDeviceAddress; // Address of the Connected Device

    private Resources res; // Reference to Application Resources

    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        res = getResources();

        ListView listView = view.findViewById(R.id.deviceListView);
        listView.setAdapter(((MainActivity) getActivity()).getAdapter());
        listView.setOnItemClickListener(this);

        currentDeviceName = view.findViewById(R.id.curDeviceNameText);
        currentDeviceAddress = view.findViewById(R.id.curDeviceAddressText);

        currentDeviceName.setText(String.format(res.getString(R.string.currentDeviceNameText), sharedpreferences.getString("currentDeviceName", ((MainActivity)getActivity()).getDeviceName())));
        currentDeviceAddress.setText(String.format(res.getString(R.string.currentDeviceAddressText), sharedpreferences.getString("currentDeviceAddress", ((MainActivity)getActivity()).getDeviceAddress())));



        view.findViewById(R.id.backToHomeButton).setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ((MainActivity) getActivity()).onItemClick(adapterView, view, i, l);

        currentDeviceName.setText(String.format(res.getString(R.string.currentDeviceNameText), sharedpreferences.getString("currentDeviceName", ((MainActivity)getActivity()).getDeviceName())));
        currentDeviceAddress.setText(String.format(res.getString(R.string.currentDeviceAddressText), sharedpreferences.getString("currentDeviceAddress", ((MainActivity)getActivity()).getDeviceAddress())));
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