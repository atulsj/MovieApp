package youtubeapidemo.examples.com.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

class MovieUtil implements Parcelable {
    private String imagePath;
    private int id;
    private double rating;
    private String title, overview, release_date;

    MovieUtil(String uri, int mov_id, String title, String overview, String release_date, double rating) {
        imagePath = uri;
        id = mov_id;
        this.title = title;
        this.overview = overview;
        this.release_date = release_date;
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeDouble(rating);
    }

    private MovieUtil(Parcel in) {
        imagePath = in.readString();
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        rating = in.readDouble();
    }

    public static final Creator<MovieUtil> CREATOR = new Creator<MovieUtil>() {
        @Override
        public MovieUtil createFromParcel(Parcel in) {
            return new MovieUtil(in);
        }

        @Override
        public MovieUtil[] newArray(int size) {
            return new MovieUtil[size];
        }
    };

    String getOverview() {
        return overview;
    }

    double getRating() {
        return rating;
    }

    String getTitle() {
        return title;
    }

    String getReleaseDate() {
        return release_date;
    }

    String getImagePath() {
        return imagePath;
    }

    int getId() {
        return id;
    }
}
