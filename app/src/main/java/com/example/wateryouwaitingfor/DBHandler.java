package com.example.wateryouwaitingfor;

import static com.example.wateryouwaitingfor.StatsFragment.waterTot;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

        // creating a constant variables for our database.
        // below variable is for our database name.
        private static final String DB_NAME = "WaterConsumed";

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

        private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

        private double dataStore=0;

        public LocalDate localDate;

        public int dayOfWeek=0;


    public static double dailyTot = 0;
        // creating a constructor for our database handler.
        public DBHandler(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public static Integer totWater = 5;



        // below method is for creating a database by running a sqlite query
        @Override
        public void onCreate(SQLiteDatabase db) {
            // on below line we are creating
            // an sqlite query and we are
            // setting our column names
            // along with their data types.
            String query = "CREATE TABLE " + TABLE_NAME + " ("

                    + ID_COL+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TIME_COL+ " TEXT,"
                    + AMOUNT_COL+ " TEXT,"
                    + DATE_COL+ " TEXT,"
                    + DAYTOT_COL+ " TEXT)";

            // at last we are calling a exec sql
            // method to execute above sql query
            db.execSQL(query);

        }

        // this method is used to add new drinks to our sqlite database.
        public void addNewDrink(double Consumed) {
            DecimalFormat meep = new DecimalFormat("0.00");
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
            String consume = meep.format(Consumed);
            // adds the value passed in as a double to the totals column.
            Consumed= Double.parseDouble(consume);
            dailyTot += Consumed;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ld = LocalDate.now();


            // inputting the local variables and the consumed parameter into our table
            values.put(TIME_COL, LocalTime.now().toString());
            values.put(AMOUNT_COL, Consumed);
            values.put(DATE_COL, ld.format(dtf));
            values.put(DAYTOT_COL, dailyTot);

            // after adding all values we are passing
            // content values to our table.
            db.insert(TABLE_NAME, null, values);
            // method call for updating the bar chart
            //setDataToDates();

            // closing the database

            db.close();
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    // below method returns the day of the week as an integer (Sunday = 0, Monday = 1, etc)
    // intakes a LocalDate parameter
//    public int getDayNumberNew(LocalDate date) {
//            Log.e("Day of week is: ", ""+ date.getDayOfWeek().getValue());
//        DayOfWeek day = date.getDayOfWeek();
//        return day.getValue();
//    }
//    public int getDayOfWeek(){
//            return dayOfWeek;
//    }
//
//    // below method returns the number of existing rows in the database
//    // intakes a Database "db" and the name of the table within the database
//    public static long getRowNum(SQLiteDatabase db, String tableName){
//        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//    }
//    // below method to set totals to a certain date range for our bar chart
//    public void setDataToDates() {
//        //getting a readable version of our database to obtain the values from it
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        // variable initialization
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//
//        //indices for the cursor
//        int totind = cursor.getColumnIndex(DAYTOT_COL);
//        int dateind = cursor.getColumnIndex(DATE_COL);
//        int amtind = cursor.getColumnIndex(AMOUNT_COL);
//
//
//        //ensures that the row number is greater than zero to avoid an OutOfBounds error
//        if (getRowNum(db, TABLE_NAME) > 1){
//            Log.d("Number of rows", " " + getRowNum(db, TABLE_NAME));
//            cursor.moveToLast();
//            String dateCheck = cursor.getString(dateind);
//            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//            localDate = LocalDate.parse(dateCheck, format);
//            getDayNumberNew(localDate);
//            cursor.moveToPrevious();
//            if(cursor.getString(dateind).equals(dateCheck)){
//                Log.d("Date stays: ", " "+ dateCheck);
//
//
//                if (dailyTot==0) {
//                    dailyTot += cursor.getDouble(amtind);
//                }
//                else
//                    dailyTot+= cursor.getDouble(totind);
//            }
//            else {
//                Log.d("Date changed", " "+dateCheck);
//                dataStore = cursor.getDouble(totind);
//                dailyTot=0;
//
//            }
//        }
//        else {
//            cursor.moveToFirst();
//            dataStore=cursor.getDouble(totind);
//        }

//
//    // stores the variable for the daily totals of a date for the bar chart in the stats fragment
//    public double getDataStore(){
//            return dataStore;
//    }

        // we have created a new method for reading all the drink data in the database.
        @SuppressLint("Range")
        public ArrayList<drinkListHandler> drinkList()
        {
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

        // Get total consumed per day
        @SuppressLint("Range")
        public double getDailyTot()
        {
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
            double amtConsumed = 0;

            // moving our cursor to first position.
            if (cursor.moveToFirst()) {
                do {

                    amtConsumind =  cursor.getColumnIndex(AMOUNT_COL);
                    amtConsumed = amtConsumed + cursor.getDouble(amtConsumind);
                    Log.i("Amount consumed: ", " " + amtConsumed);
                    // on below line we are adding the data from
                    // cursor to our array list.
                    waterLog.add(new drinkListHandler(
                            cursor.getDouble(amtConsumind)));
                    // cursor.getDouble(3)));
                } while (cursor.moveToNext());
                // moving our cursor to next.
            }
            // at last closing our cursor
            // and returning our array list.
            cursor.close();
            return amtConsumed;
        }


    public double getNewDailyTotal () {

            return totWater;

    }


    }



