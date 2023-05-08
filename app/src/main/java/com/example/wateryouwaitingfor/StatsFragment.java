package com.example.wateryouwaitingfor;

import android.content.Context;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList barArrayList;
    private BarChart barChart;

    private String time ;

    private String consumed;

    private Button total;

    public static double waterTot;



    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button waterButton;
    private Button readDrinkButton;
    private EditText timeEditText;
    private EditText consumedEditText;

    private DBHandler db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ashwina", "In StatsFragment: onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Ashwina", "In StatsFragment: onCreateView");
        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        waterButton = (Button) myView.findViewById(R.id.btnAddWater);
        waterButton.setOnClickListener(this);
        consumedEditText = myView.findViewById(R.id.AmtConsumedEdt);
        readDrinkButton = myView.findViewById(R.id.btnReadDrink);
        readDrinkButton.setOnClickListener(this);

        total = myView.findViewById(R.id.btnTotal);
        total.setOnClickListener(this);


        return myView;
    }
    
    private void getData() {

        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(0, 0));
        barArrayList.add(new BarEntry(1, 0));
        barArrayList.add(new BarEntry(2, 0));
        barArrayList.add(new BarEntry(3, 0));
        barArrayList.add(new BarEntry(4, 0));
        barArrayList.add(new BarEntry(5, 0));
        barArrayList.add(new BarEntry(6, 0));
        barArrayList.add(new BarEntry(7, 0));
    }

    //DBHandler dbHandler = new DBHandler(getContext());

    // updates values to bar chart
//    private void updateData(){
//
//        double store = dbHandler.getDataStore();
//        Float valFor = Float.parseFloat(String.valueOf(store));
//        int num = db.getDayOfWeek();
//        String pass = Integer.toString(num);
//        Float dayNum = Float.parseFloat(pass);
//        Log.e("Ashwina", "day of the week:" + dayNum);
//       barArrayList.add(new BarEntry(dayNum, valFor));
//    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        barChart = view.findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Bar Chart");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
        barChart.getXAxis().setDrawGridLines(false); // disable grid lines for the XAxis
        barChart.getAxisLeft().setDrawGridLines(false); // disable grid lines for the left YAxis
        barChart.getAxisRight().setDrawGridLines(false); // disable grid lines for the right YAxis

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
    }


    public void onClick(View view) {
        Log.d("Ashwina", "In StatsFragment: onClick");


        switch (view.getId()) {
            case R.id.btnAddWater:
                Log.d("Ashwina", "In StatsFragment: onClick: btnAddWater");

                // below line is to get data from all edit text fields.
               // time = timeEditText.getText().toString();
                consumed = consumedEditText.getText().toString();

                // validating if the text fields are empty or not.
                if (consumed.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                db.addNewDrink(Double.parseDouble(consumed));
              //  updateData();

                // after adding the data we are displaying a toast message.
                Toast.makeText(getContext(), "Drink has been added.", Toast.LENGTH_SHORT).show();
              //  timeEditText.setText("");
                consumedEditText.setText("");

                break;
            case R.id.btnReadDrink:
                Log.d("Ashwina", "InStatsFrag: readdrink");
                Intent i = new Intent(getContext(), ViewDrinks.class);
                startActivity(i);
                break;
        }

        switch (view.getId()) {
            case R.id.btnTotal:
                Log.d("Naga", "In StatsFragment: onClick: btnTotal!!!!!!!!!!!!!!" + db.getDailyTot());

                Toast.makeText(getContext(), "Total Water Intake is: " + db.getDailyTot(), Toast.LENGTH_SHORT).show();

                waterTot = db.getDailyTot();
//            case R.id.btnBarChart:
//                Log.d("Ashwina", "I pressed the button ");
//                updateData();
        }


    }



}
