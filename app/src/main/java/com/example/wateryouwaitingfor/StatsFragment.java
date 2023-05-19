package com.example.wateryouwaitingfor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/*
    Bar Chart library: https://github.com/PhilJay/MPAndroidChart
 */

/**
 * A {@link Fragment} to display Water
 * Consumption statistics
 */
public class StatsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<BarEntry> barArrayList;

    public static double waterTot;


    public StatsFragment() {
        // Required empty public constructor
    }

    private EditText consumedEditText;

    private DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ashwina", "In StatsFragment: onCreate");

        db = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Ashwina", "In StatsFragment: onCreateView");
        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        Button waterButton = myView.findViewById(R.id.btnAddWater);
        waterButton.setOnClickListener(this);
        consumedEditText = myView.findViewById(R.id.AmtConsumedEdt);
        Button readDrinkButton = myView.findViewById(R.id.btnReadDrink);
        readDrinkButton.setOnClickListener(this);

        return myView;
    }
    
    private void getData() {

        float[] weekData = db.getWeekData();

        //Create a bar entry for the totals in the last week
        barArrayList = new ArrayList<>();
        for(int i=0; i<7; i++){
            barArrayList.add(new BarEntry(i, weekData[i]));
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        BarChart barChart = view.findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Bar Chart");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false); // disable grid lines for the XAxis
        barChart.getAxisLeft().setDrawGridLines(false); // disable grid lines for the left YAxis
        barChart.getAxisRight().setDrawGridLines(false); // disable grid lines for the right YAxis

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
    }


    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Log.d("Ashwina", "In StatsFragment: onClick");


        switch (view.getId()) {
            case R.id.btnAddWater:
                Log.d("Ashwina", "In StatsFragment: onClick: btnAddWater");

                // below line is to get data from all edit text fields.
                String consumed = consumedEditText.getText().toString();

                //reset edit text field
                consumedEditText.setText("");

                // validating if the text fields are positive numbers.
                if (consumed.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all the data", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!MainActivity.isValidDouble(consumed)){
                    Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(Double.parseDouble(consumed) <= 0){
                    Toast.makeText(getContext(), "Please enter a positive value", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                db.addNewDrink(Double.parseDouble(consumed));

                // after adding the data we are displaying a toast message.
                Toast.makeText(getContext(), "Drink has been added.", Toast.LENGTH_SHORT).show();

                Toast.makeText(getContext(), "Total Water Intake is: " + db.getDailyTot(), Toast.LENGTH_SHORT).show();

                waterTot = db.getDailyTot();

                SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
                ((MainActivity)getActivity()).getUserReference().child(sharedpreferences.getString("userID", "User ID")).child("points").setValue((int) (waterTot));

                break;
            case R.id.btnReadDrink:
                Log.d("Ashwina", "InStatsFrag: readdrink");
                Intent i = new Intent(getContext(), ViewDrinks.class);
                startActivity(i);

                break;
        }

    }

}

