package com.example.wateryouwaitingfor;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/*
    SQLite Database code was created with the help of:
    https://www.geeksforgeeks.org/how-to-read-data-from-sqlite-database-in-android/
    https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/
    https://stackoverflow.com/questions/17441927/android-sqlite-issue-table-has-no-column-named

    LocalDate help:
    https://stackoverflow.com/questions/28177370/how-to-format-localdate-to-string
    https://stackoverflow.com/questions/2735023/convert-string-to-java-util-date
 */

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "WaterConsumed";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // below int is our database version
    private static final int DB_VERSION = 4;

    // below variable is for our table name.
    private static final String TABLE_NAME = "WaterIntake";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our time of consumption column
    private static final String TIME_COL = "Time";

    // below variable is for our amount consumed column.
    private static final String AMOUNT_COL = "Amount";

    // below variable is for our date column.
    private static final String DATE_COL = "Date";

    // below variable is for our daily total column.
    private static final String DAYTOT_COL = "DailyTotals";


    //daily total ounces of water
    public static double dailyTot = 0;

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIME_COL + " TEXT,"
                + AMOUNT_COL + " TEXT,"
                + DATE_COL + " TEXT,"
                + DAYTOT_COL + " TEXT)";


        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);

    }

    /**
     * Adds a new drink into the SQLite Database
     * @param Consumed the amount of water consumed by the user
     */
    public void addNewDrink(double Consumed) {
        DecimalFormat decForm = new DecimalFormat("0.00");
        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.

        // meep truncates the amtConsumed string to two decimal places
        String consume = decForm.format(Consumed);
        // adds the value passed in as a double to the totals column.
        Consumed = Double.parseDouble(consume);
        dailyTot += Consumed;

        LocalDate ld = LocalDate.now();


        // inputting the local variables and the consumed parameter into our table
        values.put(TIME_COL, LocalTime.now().withNano(0).toString());
        values.put(AMOUNT_COL, Consumed);
        values.put(DATE_COL, ld.format(DATE_FORMATTER));
        values.put(DAYTOT_COL, dailyTot);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // closing the database

        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // we have created a new method for reading all the drink data in the database.
    @SuppressLint("Range")
    public ArrayList<drinkListHandler> drinkList() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to
        // read data from database.
        Cursor cursor
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<drinkListHandler> courseModalArrayList
                = new ArrayList<>();

        // moving our cursor to first position.
        if (cursor.moveToFirst()) {
            do {
                int timeind = cursor.getColumnIndex(TIME_COL);
                int amtConsumind = cursor.getColumnIndex(AMOUNT_COL);
                int dateind = cursor.getColumnIndex(DATE_COL);
                int totind = cursor.getColumnIndex(DAYTOT_COL);
                // on below line we are adding the data from
                // cursor to our array list.
                courseModalArrayList.add(new drinkListHandler(
                        cursor.getString(timeind),
                        cursor.getDouble(amtConsumind),
                        cursor.getString(dateind),
                        cursor.getDouble(totind)));
            } while (cursor.moveToNext());
            // moving our cursor to next.
        }

        // at last closing our cursor
        // and returning our array list.
        cursor.close();
        return courseModalArrayList;
    }

    /**
     * Gets the water total of the day
     * @return the current day's water total
     */
    @SuppressLint("Range")
    public double getDailyTot() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to
        // read data from database.
        Cursor cursor
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<drinkListHandler> waterLog
                = new ArrayList<>();

        int amtConsumind;
        int dateIndex;
        double amtConsumed = 0;

        String curDate = LocalDate.now().format(DATE_FORMATTER);

        // moving our cursor to the newest position.
        if (cursor.moveToLast()) {
            do {

                amtConsumind = cursor.getColumnIndex(AMOUNT_COL);
                dateIndex = cursor.getColumnIndex(DATE_COL);

                // if the current date equals the entry date
                if (curDate.equals(cursor.getString(dateIndex))){
                    amtConsumed = amtConsumed + cursor.getDouble(amtConsumind);
                    Log.i("Amount consumed: ", " " + amtConsumed);
                    // on below line we are adding the data from
                    // cursor to our array list.
                    waterLog.add(new drinkListHandler(
                            cursor.getDouble(amtConsumind)));
                }
                else{
                    break;
                }

            } while (cursor.moveToPrevious());
            // moving our cursor to the previous entry.
        }
        // at last closing our cursor
        // and returning our array list.
        cursor.close();
        return dailyTot;
    }

    /**
     * Gets the daily totals of the last week
     * @return a float array of the totals
     */
    public float[] getWeekData(){
        float[] weekData = new float[7];

        //SQL Database Reference
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        //Define variables
        int dailyTotIndex;
        int dateIndex;
        int counter = 6;
        String lastDate = "";

        //Move cursor backwards
        if (cursor.moveToLast()){
            do{
                //Get needed column positions
                dailyTotIndex = cursor.getColumnIndex(DAYTOT_COL);
                dateIndex = cursor.getColumnIndex(DATE_COL);

                String curDate = cursor.getString(dateIndex);
                if (!curDate.equals(lastDate)){ // If the date is different, add daily total to return
                    weekData[counter] = (float) cursor.getDouble(dailyTotIndex);
                    counter--;
                    lastDate = curDate;
                }

            } while(cursor.moveToPrevious() && counter >= 0); // while not at end of table or < 7 entries
        }

        // at last closing our cursor
        // and returning our array.
        cursor.close();
        return weekData;
    }
}



