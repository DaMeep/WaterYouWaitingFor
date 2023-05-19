package com.example.wateryouwaitingfor;

public class drinkListHandler {

    // below variable is used to store the time of the drink
        private String time;

        // below variable is used to store the amount of water consumed
        private double amtConsumed;
        // below variable is used to store the date of consumption
        private String date;
        // below variable is used to store the total amount of water consumed in a day
        private double dailyTotals;
    // below variable is used to store the id of the entry in the database
        private int id;

    // creating getter and setter methods

    /**
     * Gets the time for the water consumption database entry
     * @return the time of the entry
     */
    public String getTime() {return time; }

    /**
     * Sets the time for the water consumption database entry
     * @param time the time of the entry
     */
    public void setTime(String time)
    {
        this.time=time;
    }

    /**
     * Gets the amount consumed for the water consumption database entry
     * @return the water amount for the entry
     */
    public String getAmtConsumed()
    {
        return String.valueOf(amtConsumed);
    }

    /**
     * Gets the date for the water consumption database entry
     * @return the date of the entry
     */
    public String getDate() {return date; }

    /**
     * Gets the daily water total at the time of the entry
     * @return the daily water total
     */
    public String getDailyTotals()
    {
        return Double.toString(dailyTotals);
    }

    /**
     * Adds the entry's water consumption to the daily water total
     */
    public void setDailyTotals()
    {
        double amt = Double.parseDouble(getAmtConsumed());
        this.dailyTotals += amt;
    }

    /**
     * Gets the Database ID of the entry
     * @return the entry's ID
     */
    public int getId() { return id; }

    /**
     * Sets the Database ID of the entry
     * @param id the wanted ID
     */
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
