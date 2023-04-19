package com.example.wateryouwaitingfor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContaminationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContaminationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PieChart pieChart;

    public ContaminationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContaminationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContaminationFragment newInstance(String param1, String param2) {
        ContaminationFragment fragment = new ContaminationFragment();
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contamination, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        pieChart= view.findViewById(R.id.pieChart);


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