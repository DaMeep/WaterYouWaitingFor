package com.example.wateryouwaitingfor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wateryouwaitingfor.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {


        // creating variables for our edittext, button and dbhandler
        private EditText courseNameEdt, courseTracksEdt, courseDurationEdt, courseDescriptionEdt;
        private Button addCourseBtn;
        private DBHandler dbHandler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment((new HomeFragment()));

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                switch(item.getItemId()){

                    case R.id.contamination:
                        replaceFragment(new ContaminationFragment());
                        break;
                    case R.id.stats:
                        replaceFragment(new StatsFragment());
                        break;
                    case R.id.home:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.friends:
                        replaceFragment(new FriendsFragment());
                        break;
                    case R.id.settings:
                        replaceFragment(new SettingsFragment());
                        break;

                }

                return true;
            });

            // initializing all our variables.
            courseNameEdt = findViewById(R.id.idEdtCourseName);
            courseTracksEdt = findViewById(R.id.idEdtCourseTracks);
            courseDurationEdt = findViewById(R.id.idEdtCourseDuration);
            courseDescriptionEdt = findViewById(R.id.idEdtCourseDescription);
            addCourseBtn = findViewById(R.id.idBtnAddCourse);

            // creating a new dbhandler class
            // and passing our context to it.
            dbHandler = new DBHandler(MainActivity.this);

            // below line is to add on click listener for our add course button.
            addCourseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // below line is to get data from all edit text fields.
                    String courseName = courseNameEdt.getText().toString();
                    String courseTracks = courseTracksEdt.getText().toString();
                    String courseDuration = courseDurationEdt.getText().toString();
                    String courseDescription = courseDescriptionEdt.getText().toString();

                    // validating if the text fields are empty or not.
                    if (courseName.isEmpty() && courseTracks.isEmpty() && courseDuration.isEmpty() && courseDescription.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // on below line we are calling a method to add new
                    // course to sqlite data and pass all our values to it.
                    dbHandler.addNewCourse(courseName, courseDuration, courseDescription, courseTracks);

                    // after adding the data we are displaying a toast message.
                    Toast.makeText(MainActivity.this, "Course has been added.", Toast.LENGTH_SHORT).show();
                    courseNameEdt.setText("");
                    courseDurationEdt.setText("");
                    courseTracksEdt.setText("");
                    courseDescriptionEdt.setText("");
                }
            });
        }


    ActivityMainBinding binding;



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}