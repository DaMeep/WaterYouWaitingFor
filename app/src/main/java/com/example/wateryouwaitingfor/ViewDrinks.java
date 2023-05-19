package com.example.wateryouwaitingfor;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class ViewDrinks extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_drinks);

            // initializing our all variables.
            // creating variables for our array list,
            // dbhandler, adapter and recycler view.
            ArrayList<drinkListHandler> drinkModalArrayList;
            DBHandler dbHandler = new DBHandler(ViewDrinks.this);

            // getting our drink array
            // list from db handler class.
            drinkModalArrayList = (ArrayList<drinkListHandler>) dbHandler.drinkList().clone();
            Collections.reverse(drinkModalArrayList);


            // removes previous days' data
            LocalDate date = LocalDate.now();
            String currentDate = date.format(DBHandler.DATE_FORMATTER);

            for (drinkListHandler handler : drinkModalArrayList){
                if (!currentDate.equals(handler.getDate())){
                    drinkModalArrayList.remove(handler);
                }
            }

            double tot = dbHandler.getDailyTot();
            Log.i("Total::::::::::::: ", " " + tot);

            // on below line passing our array list to our adapter class.
            drinkRVAdapter courseRVAdapter = new drinkRVAdapter(ViewDrinks.this, R.layout.drink_recyclerview_item, drinkModalArrayList);
            ListView coursesRV = findViewById(R.id.idRVDrinks);

            // setting our adapter to recycler view.
            coursesRV.setAdapter(courseRVAdapter);
        }
    }
