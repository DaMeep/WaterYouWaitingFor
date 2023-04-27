package com.example.wateryouwaitingfor;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.LocalTime;
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

            // on below line we are creating a variable for
            // our sqlite database and calling writable method
            // as we are writing data in our database.
            SQLiteDatabase db = this.getWritableDatabase();

            // on below line we are creating a
            // variable for content values.
            ContentValues values = new ContentValues();

            // on below line we are passing all values
            // along with its key and value pair.

            //String time = LocalTime.now().toString();
            //String date = LocalDate.now().toString();
             dailyTot += Consumed;

            values.put(TIME_COL, LocalTime.now().toString());
            values.put(AMOUNT_COL, Consumed);
            values.put(DATE_COL, LocalDate.now().toString());
            values.put(DAYTOT_COL, dailyTot);
            // after adding all values we are passing
            // content values to our table.
            db.insert(TABLE_NAME, null, values);

            // at last we are closing our
            // database after adding database.

            db.close();
        }

//        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
        // we have created a new method for reading all the courses.
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


    }



