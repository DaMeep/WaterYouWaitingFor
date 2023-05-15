package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.time.LocalTime;



/**
 * A {@link Fragment} subclass for the homepage
 */
public class HomeFragment extends Fragment implements Serializable {

    private WaterIntakeHandler waterIntakeHandler; // Water consumption handler

    private SharedPreferences sharedpreferences; // Shared Preferences Reference

    private int progress = 0;
    Button buttonIncrement;
    Button buttonDecrement;
    ProgressBar progressBar;
    TextView textView;

    private DBHandler dbHandler;


    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_Scan = (Button) view.findViewById(R.id.btn_scan);
        waterIntakeHandler = ((MainActivity) ((getActivity()))).getIntakeHandler();
        waterIntakeHandler.setActivitylevel(sharedpreferences.getInt("activityLevel", 0));
        waterIntakeHandler.setWeight(Double.parseDouble(sharedpreferences.getString("userWeight", "100")));
        waterIntakeHandler.updateIdealIntake();

        StatsFragment sm = new StatsFragment();


        btn_Scan.setOnClickListener((MainActivity)getActivity());

        dbHandler = new DBHandler(getContext());
        Resources res = getResources();

        TextView userNameDisplay = view.findViewById(R.id.welcomeText);
        userNameDisplay.setText(String.format(res.getString(R.string.welcome), sharedpreferences.getString("username", "User")));

        TextView watDisplay = view.findViewById(R.id.waterTotDisplay);
        watDisplay.setText(String.format(res.getString(R.string.totalAmountConsumed), dbHandler.getDailyTot()));

        TextView waterGoal = view.findViewById(R.id.waterGoal);
        waterGoal.setText(String.format(res.getString(R.string.goalText), waterIntakeHandler.getIdealIntake()));

        progressBar =  view.findViewById(R.id.progress_bar);
        textView =  view.findViewById(R.id.text_view_progress);

        updateProgressBar();



        TextView watUpdate = view.findViewById(R.id.updateText);
        watUpdate.setText(String.format(res.getString(R.string.lastUpdatedText), LocalTime.now().withNano(0)));


    }

    // updateProgressBar() method sets
    // the progress of ProgressBar in text
    private void updateProgressBar() {

        int value = (int)(dbHandler.getDailyTot()/waterIntakeHandler.getIdealIntake()*100);

        if (value >0) {
            progressBar.setProgress(value);
            textView.setText(String.valueOf(value + "%"));

        }

        else {
            progressBar.setProgress(0);
            textView.setText(String.valueOf(0 + "%"));

        }


    }
}
