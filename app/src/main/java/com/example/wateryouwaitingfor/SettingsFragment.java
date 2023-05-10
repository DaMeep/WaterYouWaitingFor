package com.example.wateryouwaitingfor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedpreferences;

    private ToggleButton unitsButton;

    private TextView weightSettingsText;
    private EditText userNameText;
    private EditText userWeightText;

    private Spinner activityDropdown;
    private Spinner notificationDropdown;
    private static final String[] activityLevels = {"None", "Low", "Medium", "High"};

    private static final String[] notificationIntervals = {"15 Minutes", "30 Minutes", "45 Minutes", "1 Hour"};
    private int curActivityLevel;
    private int curNotificationInterval;

    public static final int NOTIFICATION_REQUEST_CODE = 101;


    private SwitchCompat notificationSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //General
        unitsButton = view.findViewById(R.id.unitToggleButton);
        unitsButton.setActivated(sharedpreferences.getBoolean("metricUnits", false));

        //Personal
        userNameText = view.findViewById(R.id.editUserNameText);
        userNameText.setText(sharedpreferences.getString("username", "User"));

        weightSettingsText = view.findViewById(R.id.weightSettingsText);
        userWeightText = view.findViewById(R.id.editWeightText);
        userWeightText.setText(sharedpreferences.getString("userWeight", "100"));

        activityDropdown = view.findViewById(R.id.settingsActivityDropdown);
        ArrayAdapter<String> activityAdapter = new ArrayAdapter(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityDropdown.setAdapter(activityAdapter);
        activityDropdown.setOnItemSelectedListener(this);
        curActivityLevel = sharedpreferences.getInt("activityLevel", 0);
        activityDropdown.setSelection(curActivityLevel);

        //Notifications
        notificationSwitch = (SwitchCompat) view.findViewById(R.id.notificationSwitch);
        notificationSwitch.setChecked(sharedpreferences.getBoolean("notificationsEnabled", false));


        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    String[] perms = {android.Manifest.permission.POST_NOTIFICATIONS};
                    if (!MainActivity.hasPermissions(getContext(), perms) && isChecked){
                        ActivityCompat.requestPermissions(getActivity(), perms, NOTIFICATION_REQUEST_CODE);
                    }
                }

                notificationDropdown.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });



        notificationDropdown = view.findViewById(R.id.settingsNotificationTimerDropdown);
        ArrayAdapter<String> notificationAdapter = new ArrayAdapter(view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,notificationIntervals);
        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationDropdown.setAdapter(notificationAdapter);
        notificationDropdown.setOnItemSelectedListener(this);
        curNotificationInterval = sharedpreferences.getInt("notificationInterval", 0);
        notificationDropdown.setSelection(curNotificationInterval);

    }
    @Override
    public void onDestroyView() {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //General
        editor.putBoolean("metricUnits", unitsButton.isChecked());

        //Personal
        editor.putString("username", userNameText.getText().toString());
        editor.putString("userWeight", userWeightText.getText().toString());
        editor.putInt("activityLevel", curActivityLevel);

        //Notifications
        editor.putBoolean("notificationsEnabled", notificationSwitch.isChecked());
        editor.putInt("notificationInterval", curNotificationInterval);


        editor.apply();

        String userID = sharedpreferences.getString("userID", "null");

        DatabaseReference mUsersReference = ((MainActivity)getActivity()).getUserReference();
        mUsersReference.child(userID).child("username").setValue(userNameText.getText().toString());

        super.onDestroyView();
    }

    public void onClick(View v) {
        if (unitsButton.isActivated()){
            weightSettingsText.setText(R.string.weightSettingsTextMetric);
        }
        else{
            weightSettingsText.setText(R.string.weightSettingsTextImperial);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (v.getId()){
            case R.id.settingsActivityDropdown:
                curActivityLevel = position;
                break;
            case R.id.settingsNotificationTimerDropdown:
                curNotificationInterval = position;
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the Notification Switch State
     * @param isActive the state of the switch
     */
    public void setNotificationSwitch(boolean isActive){
        notificationSwitch.setChecked(isActive);
    }

}