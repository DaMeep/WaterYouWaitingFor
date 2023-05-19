package com.example.wateryouwaitingfor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;

/**
 * A {@link Fragment} subclass for displaying
 * local Water Contamination
 */
public class ContaminationFragment extends Fragment {

    public ContaminationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contamination, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        PieChart pieChart = view.findViewById(R.id.pieChart);


        ArrayList <PieEntry> contaminants = new ArrayList<>();
        contaminants.add(new PieEntry(200,"Lead"));
        contaminants.add(new PieEntry(400,"Copper"));
        contaminants.add(new PieEntry(650,"Carcinogen"));
        contaminants.add(new PieEntry(600,"Calcium"));
        contaminants.add(new PieEntry(750,"Steel"));

        PieDataSet pieDataSet = new PieDataSet(contaminants, "Contaminants");
        pieDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Contaminants");
        pieChart.animate();

    }
}