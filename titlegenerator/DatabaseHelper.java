package samad.titlegenerator;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 13/08/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/samad.titlegenerator/databases/";

    private static String DB_NAME = "title.db";

    private SQLiteDatabase myDatabase;

    private final Context myContext;

    private static final String MOVIE_TABLE = "movie";
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RELEASE = "release";
    private static final String KEY_POSTER = "poster";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /** Create Database **/
    public void createDatabase() throws IOException {
        boolean DB_EXIST = checkDatabase();

        if (DB_EXIST) {
            //Database exists, carry on...
        } else {
            //Database Does not exist
            this.getReadableDatabase();
            try {

                copyDatabase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }


    /** Check if database exists **/
    private boolean checkDatabase() {
        //SQLiteDatabase dbCheck = null;
        boolean dbCheck = false;

        try {
            String thePath = DB_PATH + DB_NAME;
            //dbCheck = SQLiteDatabase.openDatabase(thePath, null,SQLiteDatabase.OPEN_READONLY);
            File dbFile = new File(thePath);
            dbCheck = dbFile.exists();
        } catch (SQLiteException e) {
            //Database Doesnt Exist
            throw e;
        }

       /* if (dbCheck != null) {
            dbCheck.close();
        }

        return dbCheck != null ? true : false;*/

        return dbCheck;
    }


    /** Copies database from local assets folder to a usable database for android **/
    private void copyDatabase() throws IOException {
        InputStream input = myContext.getAssets().open("title.db");
        String outFileName = DB_PATH + DB_NAME;

        OutputStream output = new FileOutputStream(outFileName);

        System.out.println("pooh");

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer))>0){
            output.write(buffer, 0, length);
        }



        //Close the streams
        output.flush();
        output.close();
        input.close();
    }

    public void openDatabase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDatabase != null)
            myDatabase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<Movie> getAll() {
        List<Movie> movieList = new ArrayList<Movie>();

        String select_query = "SELECT * FROM movie";

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.query("movie", new String[] {KEY_ID, KEY_TITLE, KEY_RELEASE, KEY_POSTER}, null, null, null, null, null); //DB.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.set_id(Integer.parseInt(cursor.getString(0)));
                movie.setMovieTitle(cursor.getString(1));
                movie.setReleaseYear(cursor.getString(2));
                movie.setPoster(cursor.getString(3));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return movieList;
    }

    public List<Movie> getRandom(int num) {
        List<Movie> movieList = new ArrayList<Movie>();

        String select_query = "SELECT * FROM movie ORDER BY RANDOM() LIMIT " + num;

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.query(MOVIE_TABLE, new String[] {KEY_ID, KEY_TITLE, KEY_RELEASE, KEY_POSTER}, null, null, null, null, "RANDOM()", String.valueOf(num));

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.set_id(Integer.parseInt(cursor.getString(0)));
                movie.setMovieTitle(cursor.getString(1));
                movie.setReleaseYear(cursor.getString(2));
                movie.setPoster(cursor.getString(3));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return movieList;
    }


}
