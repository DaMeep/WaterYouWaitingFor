package com.example.wateryouwaitingfor;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

    public class ViewDrinks extends AppCompatActivity {

        // creating variables for our array list,
        // dbhandler, adapter and recycler view.
        private ArrayList<drinkListHandler> courseModalArrayList;
        private DBHandler dbHandler;
        private drinkRVAdapter courseRVAdapter;
        private ListView coursesRV;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_drinks);

            // initializing our all variables.
            courseModalArrayList = new ArrayList<>();
            dbHandler = new DBHandler(ViewDrinks.this);

            // getting our course array
            // list from db handler class.
            courseModalArrayList = dbHandler.drinkList();

            double tot = dbHandler.getDailyTot();
            Log.i("Total::::::::::::: ", " " + tot);

            // on below line passing our array list to our adapter class.
            courseRVAdapter = new drinkRVAdapter(ViewDrinks.this, R.layout.drink_recyclerview_item, courseModalArrayList);
            coursesRV = findViewById(R.id.idRVDrinks);

            // setting layout manager for our recycler view.
           // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewDrinks.this, RecyclerView.VERTICAL, false);
            //coursesRV.setLayoutManager(linearLayoutManager);

            // setting our adapter to recycler view.
            coursesRV.setAdapter(courseRVAdapter);
        }
    }
