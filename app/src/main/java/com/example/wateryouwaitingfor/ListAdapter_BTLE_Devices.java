package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter_BTLE_Devices extends ArrayAdapter<BTLE_Device> {

    final Activity activity;
    final int layoutResourceID;
    final ArrayList<BTLE_Device> devices;

    public ListAdapter_BTLE_Devices(Activity activity, int resource, ArrayList<BTLE_Device> objects) {
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        devices = objects;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        // Get information about the BTLE_Device

        BTLE_Device device = devices.get(position);
        String name = device.getName();
        String address = device.getAddress();
        int rssi = device.getRSSI();

        // Populate the list item with the device's data

        TextView tv;
        Resources res = activity.getResources();

        tv = convertView.findViewById(R.id.tv_name);
        if (name != null && name.length() > 0) {
            tv.setText(device.getName());
        }
        else {
            tv.setText(res.getString(R.string.noName));
        }

        tv = convertView.findViewById(R.id.tv_rssi);
        tv.setText(String.format(res.getString(R.string.rssi), rssi));

        tv = convertView.findViewById(R.id.tv_macaddr);
        if (address != null && address.length() > 0) {
            tv.setText(device.getAddress());
        }
        else {
            tv.setText(res.getString(R.string.noAddress));
        }

        return convertView;
    }
}
