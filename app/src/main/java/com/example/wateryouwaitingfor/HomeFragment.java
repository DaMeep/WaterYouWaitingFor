package com.example.wateryouwaitingfor;


import static com.example.wateryouwaitingfor.StatsFragment.waterTot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements Serializable {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn_Scan;
    private WaterIntakeHandler waterIntakeHandler;


    private SharedPreferences sharedpreferences;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_Scan = (Button) view.findViewById(R.id.btn_scan);

       waterIntakeHandler = ((MainActivity) getActivity()).getIntakeHandler();
       waterIntakeHandler.setActivitylevel(sharedpreferences.getInt("activityLevel", 0));
       waterIntakeHandler.setWeight(Double.parseDouble(sharedpreferences.getString("userWeight", "100")));
       waterIntakeHandler.updateIdealIntake();


        btn_Scan.setOnClickListener((MainActivity)getActivity());


        TextView userNameDisplay = view.findViewById(R.id.welcomeText);
        String welcomeback = "Welcome back,";
        userNameDisplay.setText(welcomeback + " " + sharedpreferences.getString("username", "User")+ "!");

        TextView watDisplay = view.findViewById(R.id.waterTotDisplay);
        watDisplay.setText("Total Amount Consumed: " + waterTot);

        TextView waterGoal = view.findViewById(R.id.waterGoal);
        waterGoal.setText("Goal: "+ waterIntakeHandler.getIdealIntake());





    }

}



