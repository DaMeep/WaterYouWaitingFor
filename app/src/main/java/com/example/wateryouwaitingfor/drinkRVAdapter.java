package com.example.wateryouwaitingfor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class drinkRVAdapter extends RecyclerView.Adapter<drinkRVAdapter.ViewHolder> {
     // variable for our array list and context
    private ArrayList<drinkListHandler> drinkArrayList;
    private Context context;

    // constructor
    public drinkRVAdapter(ArrayList<drinkListHandler> drinkArrayList, Context context) {
        this.drinkArrayList = drinkArrayList;
        this.context = context;
    }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // on below line we are inflating our layout
            // file for our recycler view items.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drink_recyclerview_item, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        drinkListHandler modal= drinkArrayList.get(position);
        holder.timeTV.setText(modal.getTime());
        holder.amtConsumedTV.setText(modal.getAmtConsumed());
    }


        @Override
        public int getItemCount() {
            // returning the size of our array list
            return drinkArrayList.size();
        }
    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView timeTV, amtConsumedTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            timeTV = itemView.findViewById(R.id.timeTV);
            amtConsumedTV = itemView.findViewById(R.id.amtConsumedTV);
        }

        }
    }



