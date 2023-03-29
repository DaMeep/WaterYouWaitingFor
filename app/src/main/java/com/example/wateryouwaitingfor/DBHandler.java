package com.example.wateryouwaitingfor;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class DBHandler extends SQLiteOpenHelper {

        // creating a constant variables for our database.
        // below variable is for our database name.
        private static final String DB_NAME = "coursedb";

        // below int is our database version
        private static final int DB_VERSION = 1;

        // below variable is for our table name.
        private static final String TABLE_NAME = "mycourses";

        // below variable is for our id column.
        private static final String ID_COL = "id";

        // below variable is for our course name column
        private static final String NAME_COL = "name";

        // below variable id for our course duration column.
        private static final String DURATION_COL = "duration";

        // below variable for our course description column.
        private static final String DESCRIPTION_COL = "description";

        // below variable is for our course tracks column.
        private static final String TRACKS_COL = "tracks";

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
                    + NAME_COL + " TEXT,"
                    + DURATION_COL + " TEXT,"
                    + DESCRIPTION_COL + " TEXT,"
                    + TRACKS_COL + " TEXT)";

            // at last we are calling a exec sql
            // method to execute above sql query
            db.execSQL(query);
        }

        // this method is use to add new course to our sqlite database.
        public void addNewCourse(String courseName, String courseDuration, String courseDescription, String courseTracks) {

            // on below line we are creating a variable for
            // our sqlite database and calling writable method
            // as we are writing data in our database.
            SQLiteDatabase db = this.getWritableDatabase();

            // on below line we are creating a
            // variable for content values.
            ContentValues values = new ContentValues();

            // on below line we are passing all values
            // along with its key and value pair.
            values.put(NAME_COL, courseName);
            values.put(DURATION_COL, courseDuration);
            values.put(DESCRIPTION_COL, courseDescription);
            values.put(TRACKS_COL, courseTracks);

            // after adding all values we are passing
            // content values to our table.
            db.insert(TABLE_NAME, null, values);

            // at last we are closing our
            // database after adding database.
            db.close();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }



