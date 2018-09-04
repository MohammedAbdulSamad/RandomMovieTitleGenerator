package samad.titlegenerator;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sam on 13/08/2018.
 */

public class MainActivity extends AppCompatActivity {

    TextView exampleText;
    ImageView exampleImage;
    CheckBox exampleCheck;
    Button generateButton;
    TableLayout resultsTable;
    Spinner spinner;

    DatabaseHelper DB;

    private List<Movie> theMovieList = new ArrayList<Movie>();
    private Random random = new Random();
    private List<Integer> nonDuplicateRandom;
    private List<Integer> currentRandom = new ArrayList<Integer>();
    private boolean tableGenerated = false;

    private List<Movie> currentMovieList;
    private List<Movie> moviesCompleted;
    private List<Integer> moviesCompletedID;
    AlertDialog alertDialog;

    private int moviesRemaining = 0;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_layout);

        moviesCompleted = new ArrayList<Movie>();
        moviesCompletedID = new ArrayList<Integer>();
        currentMovieList = new ArrayList<Movie>();

        DB = new DatabaseHelper(this);
        try {
            DB.createDatabase();
        } catch (IOException e) {
            throw new Error("Unable to create Database");
        }

        try {
            DB.openDatabase();
        } catch (SQLiteException sqle) {
            throw sqle;
        }

        theMovieList = DB.getAll();
        moviesRemaining = theMovieList.size();

        exampleText = (TextView) findViewById(R.id.exampleText);
        exampleImage = (ImageView) findViewById(R.id.exampleImage);
        exampleCheck = (CheckBox) findViewById(R.id.exampleCheckBox);
        generateButton = (Button) findViewById(R.id.generateButton);
        resultsTable = (TableLayout) findViewById(R.id.resultsTable);
        spinner = (Spinner) findViewById(R.id.spinner);

        //movieListView.setText("TEST NE \n W LINE.");

       /* for (Movie movie : theMovieList) {
            System.out.println(movie.getMovieTitle());
        }*/

        /*Movie testMovie = theMovieList.get(0);
        movieListView.setText(testMovie.getMovieTitle());
        String imageLink = testMovie.getPoster();
        new DownloadImageTask(posterImage).execute(imageLink);*/

        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Current Movie List Empty");
        alertDialog.setMessage("Movie List will now reset.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                resetMovieList();
                dialogInterface.dismiss();
            }
        });


        DB.close();


    }

    public void generateMovieList(View view) {
        if (theMovieList.isEmpty()) {
            //List Empty
            alertDialog.show();
        } else {
            if (tableGenerated) {
                resultsTable.removeAllViews();
            }

            int numberOfResults = Integer.parseInt(String.valueOf(spinner.getSelectedItem()));

            if (!currentMovieList.isEmpty()) {
                for (Movie addM : currentMovieList) {
                    theMovieList.add(addM);
                }
                currentMovieList.clear();
                System.out.println("List Cleared.");
            }

            //createRandomMovieList(numberOfResults);

            for (int i = 0; i < numberOfResults; i++) {
                Movie movie = getRandomMov(); //currentMovieList.get(i);
                currentMovieList.add(movie);

                final TableRow row = new TableRow(this);
                //TableRow row2 = new TableRow(this);
                TextView mTitleView = new TextView(this);
                ImageView mPosterView = new ImageView(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                //row2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
               /* mTitleView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                mPosterView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));*/

                mTitleView.setLayoutParams(exampleText.getLayoutParams());
                mTitleView.setTextColor(Color.WHITE);
                mTitleView.setWidth(800);
                mPosterView.setLayoutParams(exampleImage.getLayoutParams());

                mTitleView.setText(movie.getMovieTitle() + " (" + movie.getReleaseYear() + ")");
                new DownloadImageTask(mPosterView).execute(movie.getPoster());

                CheckBox mChecked = new CheckBox(this);
                mChecked.setLayoutParams(exampleCheck.getLayoutParams());
                mChecked.setId(movie.get_id());
                mChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) v).isChecked()) {
                            for (int i = 0; i < currentMovieList.size(); i++) {
                                Movie movie1 = currentMovieList.get(i);
                                if (movie1.get_id()  == ((CheckBox) v).getId()) {
                                    moviesCompleted.add(movie1);
                                    currentMovieList.remove(i);
                                    row.setBackgroundColor(0x0757575);
                                }
                            }
                        }
                        if (!(((CheckBox) v).isChecked())) {
                            for (int i = 0; i < moviesCompleted.size(); i++) {
                                Movie movie1 = moviesCompleted.get(i);
                                if (movie1.get_id()  == ((CheckBox) v).getId()) {
                                    moviesCompleted.remove(i);
                                }
                            }
                        }
                    }
                });

                row.addView(mPosterView);
                row.addView(mTitleView);
                row.addView(mChecked);
                resultsTable.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                //resultsTable.addView(row2, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                tableGenerated = true;
                System.out.println(moviesRemaining + " Movies Remaining.");
            }
        }
    }

    public void resetMovieList() {
        theMovieList = DB.getAll();
    }

    public Movie getRandomMov() {
        int index = random.nextInt(theMovieList.size());
        Movie selectedMovie = theMovieList.get(index);
        theMovieList.remove(index);
        return selectedMovie;
    }

    public void createRandomMovieList(int results) {
        currentMovieList = DB.getRandom(results);
        for (Movie m : currentMovieList) {
            System.out.println(m.getMovieTitle());
        }
    }

    public void printCompletedMovies(View view) {
        for (Movie m : moviesCompleted) {
            System.out.println(m.getMovieTitle());
        }
    }
}
