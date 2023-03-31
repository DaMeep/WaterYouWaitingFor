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
        private EditText timeEdt, amtConsumeEdt;
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
            timeEdt = findViewById(R.id.idEdtTime);
            amtConsumeEdt = findViewById(R.id.idEdtAmtConsumed);
            Button addWaterBtn = findViewById(R.id.idBtnAddWater);

            // creating a new dbhandler class
            // and passing our context to it.
            dbHandler = new DBHandler(MainActivity.this);

            // below line is to add on click listener for our add drink button.
            addWaterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // below line is to get data from all edit text fields.
                    String time = timeEdt.getText().toString();
                    String Consumed = amtConsumeEdt.getText().toString();


                    // validating if the text fields are empty or not.
                    if (time.isEmpty() && Consumed.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // on below line we are calling a method to add new
                    // course to sqlite data and pass all our values to it.
                    dbHandler.addNewDrink(time, Consumed);
                    dbHandler.addNewDayTot(time, Consumed);

                    // after adding the data we are displaying a toast message.
                    Toast.makeText(MainActivity.this, "Drink has been added.", Toast.LENGTH_SHORT).show();
                    timeEdt.setText("");
                    amtConsumeEdt.setText("");
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