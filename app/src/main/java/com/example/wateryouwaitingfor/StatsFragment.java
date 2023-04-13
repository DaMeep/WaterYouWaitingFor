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
public class StatsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String time ;
    private String consumed;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Button waterButton;
    private EditText timeEditText;
    private EditText consumedEditText;

    private DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Ashwina", "In StatsFragment: onCreate" );

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Ashwina", "In StatsFragment: onCreateView" );
        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        waterButton = (Button) myView.findViewById(R.id.btnAddWater);
        waterButton.setOnClickListener(this);
        timeEditText = myView.findViewById(R.id.timeEdt);
        consumedEditText = myView.findViewById(R.id.AmtConsumedEdt);

        return myView;
    }

    @Override
    public void onClick(View view) {
        Log.d("Ashwina", "In StatsFragment: onClick");

        switch (view.getId()) {
            case R.id.btnAddWater:
                Log.d("Ashwina", "In StatsFragment: onClick: btnAddWater");

                // below line is to get data from all edit text fields.
                time = timeEditText.getText().toString();
                consumed = consumedEditText.getText().toString();

                // validating if the text fields are empty or not.
                if (time.isEmpty() && consumed.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                db.addNewDrink(time, consumed);

                // after adding the data we are displaying a toast message.
                Toast.makeText(getContext(), "Course has been added.", Toast.LENGTH_SHORT).show();
                timeEditText.setText("");
                consumedEditText.setText("");

                break;
        }
    }
}