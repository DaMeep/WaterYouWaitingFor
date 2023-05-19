package com.example.wateryouwaitingfor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class NavigationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private ViewPager slideViewPager; // contains information about each onboarding page
    private LinearLayout dotIndicator; // current page indicator
    private Button backButton, nextButton; // navigation buttons

    private Spinner dropDown; // dropdown for activity level & notification interval selection

    private EditText readNameEditText; // name display

    private EditText readWeightEditText; // weight display

    private SwitchCompat notificationSwitch; // notification toggle

    private final int length = 5; // number of pages

    private final String[] activityLevels = {"None", "Low", "Medium", "High"}; // list of activity levels
    private final String[] notificationIntervals = {"15 Minutes", "30 Minutes", "45 Minutes", "1 Hour"}; // list of notification intervals

    private int curDropdownLevel = 0; // stores the selected dropdown item

    private Resources res; // reference to application resources


    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {

            // set the page indicator to the current page
            setDotIndicator(position);

            // access shared preferences for settings storage
            SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            // Store and display data based on current page

            switch (position) {
                case 0:
                    backButton.setVisibility(View.INVISIBLE);
                    readNameEditText.setVisibility(View.VISIBLE);
                    readWeightEditText.setVisibility(View.INVISIBLE);
                    dropDown.setVisibility(View.INVISIBLE);
                    notificationSwitch.setVisibility(View.INVISIBLE);

                    editor.putString("userWeight", readWeightEditText.getText().toString());

                    readNameEditText.setText(sharedPref.getString("username", "User"));
                    nextButton.setText(res.getString(R.string.nextText));
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

                    ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,activityLevels);

                    dropDown.setAdapter(activityAdapter);
                    dropDown.setSelection(sharedPref.getInt("activityLevel",0));

                    break;

                case 3:
                    notificationSwitch.setVisibility(View.VISIBLE);

                    editor.putInt("activityLevel", curDropdownLevel);
                    ArrayAdapter<String> notifAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,notificationIntervals);
                    dropDown.setAdapter(notifAdapter);
                    dropDown.setSelection(sharedPref.getInt("notificationInterval",0));

                    notificationSwitch.setChecked(sharedPref.getBoolean("notificationsEnabled", false));
                    dropDown.setVisibility(notificationSwitch.isChecked() ? View.VISIBLE : View.INVISIBLE);

                    nextButton.setText(res.getString(R.string.nextText));

                    break;

                case 4:
                    dropDown.setVisibility(View.INVISIBLE);
                    notificationSwitch.setVisibility(View.INVISIBLE);

                    editor.putInt("notificationInterval", curDropdownLevel);
                    editor.putBoolean("notificationsEnabled", notificationSwitch.isChecked());

                    nextButton.setText(res.getString(R.string.finishText));

                    break;

                default:
                    break;
            }

            // apply modified settings
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
        res = getResources();

        // initialize views & set OnClick actions/listeners
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
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

                int curPage = getItem(0);

                // if it is after the second page or the second page is active and the weight is valid
                if (curPage > 1 || (curPage == 1 && checkValidity(readWeightEditText.getText().toString()))) {
                    slideViewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int curPage = getItem(0);

                if (curPage < length-1) { // if before the last page
                    // if on first page with a valid name, or second page with a valid weight
                    if (curPage > 1 || (curPage == 0 && checkValidity(readNameEditText.getText().toString())) || ((curPage == 1 && checkValidity(readWeightEditText.getText().toString())))) {
                        slideViewPager.setCurrentItem(getItem(1), true);
                    }
                }
                else { // if on the last page
                    String userID = generateUserId();

                    SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    // User has completed onboarding page
                    editor.putBoolean("firstStart", false);

                    // give User an ID
                    editor.putString("userID", userID);
                    editor.apply();

                    // put User in Firebase
                    User user = new User(preferences.getString("username", "User"));
                    DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
                    mUsersReference.child(userID).setValue(user);

                    // Start MainActivity
                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);

        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }

    /**
     * Sets the progress bar at the bottom of
     * the startup page
     * @param position the highlighted "dot"
     */
    public void setDotIndicator(int position) {

        TextView[] dots = new TextView[length];
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

    /**
     * Returns the index of the current startup
     * page + i
     * @param i the page offset
     * @return the page index
     */
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }

    /**
     * Generates a random 10-digit alphanumeric User ID for Firebase
     * @return the generated User ID
     */
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

    /**
     * Checks whether the given name or weight is valid
     * @param value the value to check
     * @return the validity of the value
     */
    public boolean checkValidity(String value){

        boolean isValid;

        switch(getItem(0)){
            case 0: // if name is not empty
                if (!value.isEmpty()){
                    isValid = true;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                break;
            case 1: // if weight is a number > 0
                if ((MainActivity.isValidDouble(value) && Double.parseDouble(value) > 0)){
                    isValid = true;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                break;
            default:
                isValid = true;
                break;
        }
        return isValid;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked){ // if set enabled check notification perms
            dropDown.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                String[] perms = {Manifest.permission.POST_NOTIFICATIONS};
                if (!MainActivity.hasPermissions(getApplicationContext(), perms)){
                    ActivityCompat.requestPermissions(this, perms, 1001);
                }
                else {
                    if (MainActivity.notificationIntent != null){
                        stopService(MainActivity.notificationIntent);
                    }
                    MainActivity.notificationIntent = new Intent( getApplicationContext(), NotificationService. class ) ;
                    startService(MainActivity.notificationIntent);
                }
            }
        }
        else {
            if (MainActivity.notificationIntent != null){
                stopService(MainActivity.notificationIntent);
                MainActivity.notificationIntent = null;
            }
            dropDown.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    MainActivity.notificationIntent =  new Intent( this, NotificationService. class );
                    startService(MainActivity.notificationIntent);
                }  else {
                    // if denied, disable notifications
                    notificationSwitch.setChecked(false);
                    stopService(MainActivity.notificationIntent);
                    MainActivity.notificationIntent = null;
                }
                break;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}