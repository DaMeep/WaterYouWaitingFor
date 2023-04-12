package com.example.wateryouwaitingfor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String time ;
    private String Consumed;

    public StatsFragment() {
        // Required empty public constructor
    }
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Button waterButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ashwina", "inOnCreate" );

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Ashwina", "inOnCreateView");
        DBHandler db = new DBHandler(getContext());
        View v = getView();
            waterButton = (Button)v.findViewById(R.id.btnAddWater);
            waterButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.d("Ashwina", "inOnClick");
                    // Inflate the layout for this fragment
                    // below line is to get data from all edit text fields.
                    time = ((EditText) view.findViewById(R.id.timeEdt)).getText().toString();
                    Consumed = ((EditText) view.findViewById(R.id.AmtConsumedEdt)).getText().toString();

                    // validating if the text fields are empty or not.
                    if (time.isEmpty() && Consumed.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter all the data...", Toast.LENGTH_SHORT).show();
                        System.out.println(inflater.inflate(R.layout.fragment_stats, container, false));
                    }

                    // on below line we are calling a method to add the new
                    // drink to sqlite data and pass all our values to it.
                    db.addNewDrink(time, Consumed);

                    // after adding the data we are displaying a toast message.
                    Toast.makeText(getContext(), "Drink has been added.", Toast.LENGTH_SHORT).show();
                    ((EditText) view.findViewById(R.id.timeEdt)).setText("");
                    ((EditText) view.findViewById(R.id.AmtConsumedEdt)).setText("");
                }
            });
        return inflater.inflate(R.layout.fragment_stats, container, false);

        }

}