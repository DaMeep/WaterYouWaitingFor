package com.example.wateryouwaitingfor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class drinkListHandler {

        // variables for our coursename,
        // description, tracks and duration, id.
        private String time;
        private double amtConsumed;

        private String date;

        private double dailyTotals;
        private int id;

    // creating getter and setter methods
    public String getTime() { return time; }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getAmtConsumed()
    {
        return String.valueOf(amtConsumed);
    }

    public void setAmtConsumed(double amtConsumed)
    {
        this.amtConsumed = amtConsumed;
    }

    public String getDate() { return date; }

    public void setDate(String date)
    {
        this.date = date;
    }

    public double getDailyTotals()
    {
        return dailyTotals;
    }

    public void
    setDailyTotals(double dailyTotals)
    {
        this.dailyTotals = dailyTotals;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    // constructor
    public drinkListHandler(String time,
                       double amtConsumed)
    {
        this.time = time;
        this.amtConsumed = amtConsumed;
    }

    // constructor
    public drinkListHandler(double amtConsumed)
    {
        this.amtConsumed = amtConsumed;
    }
}
