package com.example.wateryouwaitingfor;

import android.os.Bundle;
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
        private RecyclerView coursesRV;

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

            // on below line passing our array list to our adapter class.
            courseRVAdapter = new drinkRVAdapter(courseModalArrayList, ViewDrinks.this);
            coursesRV = findViewById(R.id.idRVDrinks);

            // setting layout manager for our recycler view.
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewDrinks.this, RecyclerView.VERTICAL, false);
            coursesRV.setLayoutManager(linearLayoutManager);

            // setting our adapter to recycler view.
            coursesRV.setAdapter(courseRVAdapter);
        }
    }
