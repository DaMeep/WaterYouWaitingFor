package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class drinkRVAdapter extends ArrayAdapter<drinkListHandler> {
     // variable for our array list and context
    private ArrayList<drinkListHandler> drinkArrayList;
    int layoutResourceID;
    Activity activity;


    // constructor
    public drinkRVAdapter(Activity activity, int resource, ArrayList<drinkListHandler> drinkArrayList) {
        super(activity.getApplicationContext(), resource, drinkArrayList);

        this.drinkArrayList = drinkArrayList;
        layoutResourceID = resource;
        this.activity=activity;
    }

        @NonNull
        public View getView(int position, View convertView, ViewGroup parent){

        if (convertView == null) {

                LayoutInflater inflater =
                        (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutResourceID, parent, false);
            }
        drinkListHandler dlh = drinkArrayList.get(position);
        String time = dlh.getTime();
        String amount = dlh.getAmtConsumed();
        String totals = dlh.getDailyTotals();
        String date = dlh.getDate();;
        TextView tv = new TextView(getContext());

        tv = convertView.findViewById(R.id.timeTV);
        if (time != null && time.length()>0){
            tv.setText(dlh.getTime());
        }
        else {
            tv.setText("No Time");
        }

        tv = (TextView) convertView.findViewById(R.id.amtConsumedTV);
            if (amount != null && amount.length()>0){
                tv.setText(dlh.getAmtConsumed());
            }
            else {
                tv.setText("None Consumed");
            }
        tv = (TextView) convertView.findViewById(R.id.dailyTotalTV);
            if (totals != null && totals.length()>0){
                tv.setText(dlh.getDailyTotals());
            }
            else {
                tv.setText("No Total");
            }
        tv = (TextView) convertView.findViewById(R.id.dateTV);
            if (date != null && date.length()>0){
                tv.setText(dlh.getDate());
            }
            else {
                tv.setText("No Date");
            }
            return convertView;
    }
    }



