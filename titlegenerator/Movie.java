package samad.titlegenerator;

/**
 * Created by Sam on 13/08/2018.
 */

public class Movie {
    private int _id;
    private String movieTitle;
    private String releaseYear;
    private String poster;

    public Movie() {

    }

    public Movie(int mId, String mTitle, String mRelease, String mPoster) {
        this._id = mId;
        this.movieTitle = mTitle;
        this.releaseYear = mRelease;
        this.poster = mPoster;
    }

    public void set_id(int mID_) {
        this._id = mID_;
    }

    public void setMovieTitle(String movieTitle_) {
        this.movieTitle = movieTitle_;
    }

    public void setReleaseYear(String releaseYear_) {
        this.releaseYear = releaseYear_;
    }

    public void setPoster(String poster_) {
        this.poster = poster_;
    }

    public int get_id() {
        return _id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getPoster() {
        return poster;
    }

}
