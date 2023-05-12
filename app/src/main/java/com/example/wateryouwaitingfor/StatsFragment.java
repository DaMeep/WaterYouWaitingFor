package com.example.wateryouwaitingfor;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link StatsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class StatsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList barArrayList;

    private String time ;

    public static double waterTot;


    public StatsFragment() {
        // Required empty public constructor
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment StatsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static StatsFragment newInstance(String param1, String param2) {
//        StatsFragment fragment = new StatsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    private EditText consumedEditText;

    private DBHandler db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ashwina", "In StatsFragment: onCreate");

        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Ashwina", "In StatsFragment: onCreateView");
        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        Button waterButton = (Button) myView.findViewById(R.id.btnAddWater);
        waterButton.setOnClickListener(this);
        consumedEditText = myView.findViewById(R.id.AmtConsumedEdt);
        Button readDrinkButton = myView.findViewById(R.id.btnReadDrink);
        readDrinkButton.setOnClickListener(this);

        Button total = myView.findViewById(R.id.btnTotal);
        total.setOnClickListener(this);

        Button updateButton = myView.findViewById(R.id.btnBarChart);
        updateButton.setOnClickListener(this);


        return myView;
    }
    
    private void getData() {

        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(0, 17));
        barArrayList.add(new BarEntry(1, 16));
        barArrayList.add(new BarEntry(2, 18));
        barArrayList.add(new BarEntry(3, 14));
        barArrayList.add(new BarEntry(4, 15));
        barArrayList.add(new BarEntry(5, 17));
        barArrayList.add(new BarEntry(6, 12));
    }

   final DBHandler dbHandler = new DBHandler(getContext());

    // updates values to bar chart
//    private void updateData(){
//
//        double store = dbHandler.getDataStore();
//        LocalDate ld = LocalDate.now();
//        int num = db.getDayNumberNew(ld);
//        db.setDataToDates();
//
//        Log.e("Ashwina", "stored: " + store);
//       barArrayList.set(num, store);
//    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        BarChart barChart = view.findViewById(R.id.barchart);
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


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Log.d("Ashwina", "In StatsFragment: onClick");


        switch (view.getId()) {
            case R.id.btnAddWater:
                Log.d("Ashwina", "In StatsFragment: onClick: btnAddWater");

                // below line is to get data from all edit text fields.
               // time = timeEditText.getText().toString();
                String consumed = consumedEditText.getText().toString();

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
            case R.id.btnBarChart:
                Log.d("Ashwina", "I pressed the button ");
             //   updateData();
                break;

            case R.id.btnTotal:
                Log.d("Naga", "In StatsFragment: onClick: btnTotal!!!!!!!!!!!!!!" + db.getDailyTot());

                Toast.makeText(getContext(), "Total Water Intake is: " + db.getDailyTot(), Toast.LENGTH_SHORT).show();

                LocalDateTime lastUpdate = LocalDateTime.now();
                Log.d("Val Updated", " " + lastUpdate);
                waterTot = db.getDailyTot();
                break;
        }

    }


}
