package com.example.wateryouwaitingfor;

public class drinkListHandler {

        // variables for our drink tracking,
        // description, tracks and duration, id.


    // below variable is used to store the time of the drink
        private String time;

        // below variable is used to store the amount of water consumed
        private double amtConsumed;
        // below variable is used to store the date of consumption
        private String date;
        // below variable is used to store the total amount of water consumed in a day
        private double dailyTotals;
        private int id;

    // creating getter and setter methods
    public String getTime() {return time; }

    public void setTime(String time)
    {
        this.time=time;
    }

    public String getAmtConsumed()
    {
        return String.valueOf(amtConsumed);
    }

    public String getDate() {return date; }

    public String getDailyTotals()
    {
        return Double.toString(dailyTotals);
    }

    public void setDailyTotals()
    {
        double amt = Double.parseDouble(getAmtConsumed());
        this.dailyTotals += amt;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    // constructor
    public drinkListHandler(String time, double amtConsumed, String date, double dailyTotals)
    {
        this.time=time;
        this.amtConsumed = amtConsumed;
        this.date=date;
        this.dailyTotals= dailyTotals;
        }

    // constructor
    public drinkListHandler(double amtConsumed)
    {
        this.amtConsumed = amtConsumed;
    }
}
