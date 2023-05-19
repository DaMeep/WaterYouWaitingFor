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

    private WaterIntakeHandler waterIntakeHandler; // Water Consumption Handler

    private SharedPreferences sharedpreferences; // Shared Preferences Reference

    private ProgressBar progressBar; // daily water progress bar
    private TextView currentProgressTextView; // percent of daily goal fulfilled

    private DBHandler dbHandler; // reference to Water Database Handler

    Resources res; // reference to the application resources


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

        // Initialize BLE Scan button & Assign on click
        Button btn_Scan = view.findViewById(R.id.btn_scan);
        btn_Scan.setOnClickListener((MainActivity)getActivity());

        // Initialize Water Consumption Handler
        waterIntakeHandler = ((MainActivity) ((getActivity()))).getIntakeHandler();
        waterIntakeHandler.setActivitylevel(sharedpreferences.getInt("activityLevel", 0));
        waterIntakeHandler.setWeight(Double.parseDouble(sharedpreferences.getString("userWeight", "100")));
        waterIntakeHandler.updateIdealIntake();

        // Initialize Water Database Handler
        dbHandler = new DBHandler(getContext());

        res = getResources();

        // Set text accordingly
        TextView userNameDisplay = view.findViewById(R.id.welcomeText);
        userNameDisplay.setText(String.format(res.getString(R.string.welcome), sharedpreferences.getString("username", res.getString(R.string.usernameDefault))));

        TextView watDisplay = view.findViewById(R.id.waterTotDisplay);
        watDisplay.setText(String.format(res.getString(R.string.totalAmountConsumed), dbHandler.getDailyTot()));

        TextView waterGoal = view.findViewById(R.id.waterGoal);
        waterGoal.setText(String.format(res.getString(R.string.goalText), waterIntakeHandler.getIdealIntake()));

        TextView watUpdate = view.findViewById(R.id.updateText);
        watUpdate.setText(String.format(res.getString(R.string.lastUpdatedText), LocalTime.now().withNano(0)));

        // Update water progress bar
        progressBar =  view.findViewById(R.id.progress_bar);
        currentProgressTextView =  view.findViewById(R.id.text_view_progress);

        updateProgressBar();
    }

    /**
     * Updates the progress bar and the percentage text
     * to the current progress towards the daily goal
     */
    private void updateProgressBar() {

        // find the current percentage towards the water goal
        int value = Math.max((int)(dbHandler.getDailyTot() / waterIntakeHandler.getIdealIntake() * 100), 0);

        // set the bar and text to said percent
        progressBar.setProgress(value);
        currentProgressTextView.setText(String.format(res.getString(R.string.percentage), value));

    }
}
