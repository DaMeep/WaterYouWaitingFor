package com.example.wateryouwaitingfor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.Random;

public class NavigationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton, skipButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    Spinner dropDown;

    private EditText readNameEditText;

    private EditText readWeightEditText;

    private SwitchCompat notificationSwitch;

    private final int length = 5;

    private final String[] activityLevels = {"None", "Low", "Medium", "High"};
    private final String[] notificationIntervals = {"15 Minutes", "30 Minutes", "45 Minutes", "1 Hour"};

    private int curDropdownLevel =0;


    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {

            setDotIndicator(position);

            SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();


            switch (position) {

                case 0:
                    backButton.setVisibility(View.INVISIBLE);
                    readNameEditText.setVisibility(View.VISIBLE);
                    readWeightEditText.setVisibility(View.INVISIBLE);
                    dropDown.setVisibility(View.INVISIBLE);
                    notificationSwitch.setVisibility(View.INVISIBLE);

                    editor.putString("userWeight", readWeightEditText.getText().toString());

                    readNameEditText.setText(sharedPref.getString("username", "User"));
                    nextButton.setText("Next");
                    break;

                case 1:

                    backButton.setVisibility(View.VISIBLE);
                    readNameEditText.setVisibility(View.INVISIBLE);
                    readWeightEditText.setVisibility(View.VISIBLE);
                    dropDown.setVisibility(View.INVISIBLE);

                    editor.putString("username", readNameEditText.getText().toString());
                    editor.putInt("activityLevel", curDropdownLevel);

                    readWeightEditText.setText(sharedPref.getString("userWeight", "0"));


                    break;

                case 2:

                    readNameEditText.setVisibility(View.INVISIBLE);
                    readWeightEditText.setVisibility(View.INVISIBLE);
                    dropDown.setVisibility(View.VISIBLE);
                    notificationSwitch.setVisibility(View.INVISIBLE);

                    editor.putString("userWeight", readWeightEditText.getText().toString());
                    editor.putInt("notificationInterval", curDropdownLevel);
                    editor.putBoolean("notificationsEnabled", notificationSwitch.isChecked());

                    ArrayAdapter<String> activityAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,activityLevels);

                    dropDown.setAdapter(activityAdapter);
                    dropDown.setSelection(sharedPref.getInt("activityLevel",0));



                    break;

                case 3:
                    notificationSwitch.setVisibility(View.VISIBLE);

                    editor.putInt("activityLevel", curDropdownLevel);
                    ArrayAdapter<String> notifAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,notificationIntervals);
                    dropDown.setAdapter(notifAdapter);
                    dropDown.setSelection(sharedPref.getInt("notificationInterval",0));

                    notificationSwitch.setChecked(sharedPref.getBoolean("notificationsEnabled", false));
                    dropDown.setVisibility(notificationSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);

                    nextButton.setText("Next");

                    break;

                case 4:
                    dropDown.setVisibility(View.INVISIBLE);
                    notificationSwitch.setVisibility(View.INVISIBLE);

                    editor.putInt("notificationInterval", curDropdownLevel);
                    editor.putBoolean("notificationsEnabled", notificationSwitch.isChecked());

                    nextButton.setText("Finish");

                    break;




                default:
                    break;
            }

            editor.apply();



        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);
        readNameEditText = findViewById(R.id.readNameEditText);
        readWeightEditText = findViewById(R.id.readWeightEditText);
        dropDown = findViewById(R.id.spinnerIntroDropDown);

        readWeightEditText.setVisibility(View.INVISIBLE);
        dropDown.setVisibility(View.INVISIBLE);

        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                curDropdownLevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        notificationSwitch = findViewById(R.id.activateNotificationSwitch);
        notificationSwitch.setChecked(false);
        notificationSwitch.setVisibility(View.INVISIBLE);

        notificationSwitch.setOnCheckedChangeListener(this);




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0) > 0) {
                    slideViewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0) < length-1)
                    slideViewPager.setCurrentItem(getItem(1), true);
                else {
                    SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("firstStart", false);
                    editor.putString("userID",generateUserId());
                    editor.apply();

                    Log.e("UserID", preferences.getString("userID","x"));

                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }




        );

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);

        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);

        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }



    public void setDotIndicator(int position) {

        dots = new TextView[length];
        dotIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme()));
            dotIndicator.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.blue, getApplicationContext().getTheme()));
    }

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }

    private String generateUserId () {

            String userId = "";

            Random gen = new Random ();

            for (int i = 0; i<10; i++) {
                int result = gen.nextInt(36);

                if (result <10) {
                    userId+= String.valueOf(result);

                }

                else {
                    char c = (char) (result + 55);
                    userId += c;
                }

            }

            return userId;

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked){
            dropDown.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                String[] perms = {Manifest.permission.POST_NOTIFICATIONS};
                if (!MainActivity.hasPermissions(getApplicationContext(), perms)){
                    ActivityCompat.requestPermissions(this, perms, 1001);
                }
            }
        }
        else{
            dropDown.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    notificationSwitch.setChecked(false);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}