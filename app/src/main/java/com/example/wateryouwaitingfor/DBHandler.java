package com.example.wateryouwaitingfor;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class DBHandler extends SQLiteOpenHelper {

        // creating a constant variables for our database.
        // below variable is for our database name.
        private static final String DB_NAME = "waterconsumedb";

        // below int is our database version
        private static final int DB_VERSION = 1;

        // below variable is for our table name.
        private static final String TABLE_NAME = "dailywater";
        // below variable is for our second table name.
        private static final String SECONDTABLE_NAME = "overalldata";

        // below variable is for our id column.
        private static final String ID_COL = "id";

        // below variable is for our time of consumption column
        private static final String TIME_COL = "time";

        // below variable is for our amount consumed column.
        private static final String AMOUNT_COL = "amount";

        // below variable is for our date column for table 2.
        private static final String DATE_COL = "date";

        // below variable is for our daily total column for Table 2.
        private static final String DAYTOT_COL = "daily totals";
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
                    + AMOUNT_COL + " REAL )";

            String query2 = "CREATE TABLE "+ SECONDTABLE_NAME + " ("
            + DATE_COL + "TEXT, "
            + DAYTOT_COL+ "REAL )";




            // at last we are calling a exec sql
            // method to execute above sql query
            db.execSQL(query);
            db.execSQL(query2);
        }

        // this method is use to add new drinks to our sqlite database.
        public void addNewDrink(String time, String Consumed) {

            // on below line we are creating a variable for
            // our sqlite database and calling writable method
            // as we are writing data in our database.
            SQLiteDatabase db = this.getWritableDatabase();

            // on below line we are creating a
            // variable for content values.
            ContentValues values = new ContentValues();

            // on below line we are passing all values
            // along with its key and value pair.
            values.put(TIME_COL, time);
            values.put(AMOUNT_COL, Consumed);

            // after adding all values we are passing
            // content values to our table.
            db.insert(TABLE_NAME, null, values);

            // at last we are closing our
            // database after adding database.

            addNewDayTot(time, Consumed);
        }

        // this method is use to add new drinks to our sqlite database.
        public void addNewDayTot(String date, String Totals) {

            // on below line we are creating a variable for
            // our sqlite database and calling writable method
            // as we are writing data in our database.
            SQLiteDatabase datab = this.getWritableDatabase();

            // on below line we are creating a
            // variable for content values.
            ContentValues values2 = new ContentValues();

            // on below line we are passing all values
            // along with its key and value pair.
            values2.put(DATE_COL, date);
            values2.put(DAYTOT_COL, Totals);

            // after adding all values we are passing
            // content values to our table.
            datab.insert(SECONDTABLE_NAME, null, values2);


            // at last we are closing our
            // database after adding database.

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }



