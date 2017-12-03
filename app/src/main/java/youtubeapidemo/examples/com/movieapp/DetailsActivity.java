package youtubeapidemo.examples.com.movieapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import youtubeapidemo.examples.com.movieapp.data.MovieContract.MovieEntry;
import youtubeapidemo.examples.com.movieapp.settings.SettingsActivity;

import static youtubeapidemo.examples.com.movieapp.BuildConfig.TMDB_DEVELOPER_KEY;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Trailer>> {

    @BindView(R.id.movie_thumbnail)
    ImageView movie_img;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.overView)
    TextView overView;
    @BindView(R.id.date)
    TextView releaseDate;
    @BindView(R.id.head_overview)
    TextView headTitle;
    @BindView(R.id.head_date)
    TextView headDate;
    @BindView(R.id.head_rating)
    TextView headRating;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView trailerRecyclerView;
    @BindView(R.id.review_recycler_view)
    RecyclerView reviewRecyclerView;
    @BindView(R.id.head_trailer)
    TextView headTrailer;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;
    @BindView(R.id.loading_indicator2)
    ProgressBar loadingIndicator2;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.no_reviewer)
    TextView noReview;
    @BindView(R.id.no_trailer)
    TextView noTrailer;
    @BindView(R.id.head_review)
    TextView headReview;


    private String trailer_url, review_url;
    private Typeface typeFace;
    private TrailersAdapter trailersAdapter;
    private static final int TRAILER_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;
    private ReviewsAdapter reviewsAdapter;
    private int id;
    private MovieUtil movieUtil;

    private LoaderManager.LoaderCallbacks<List<Reviewer>> ReviewerResultLoaderListener =
            new LoaderManager.LoaderCallbacks<List<Reviewer>>() {
        @Override
        public Loader<List<Reviewer>> onCreateLoader(int id, Bundle args) {
            loadingIndicator2.setVisibility(View.VISIBLE);
            return new AsyncTaskLoader<List<Reviewer>>(DetailsActivity.this) {
                @Override
                public List<Reviewer> loadInBackground() {
                    return QueryUtils.fetchMovieReviewer(review_url);
                }

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Reviewer>> loader, List<Reviewer> data) {
            loadingIndicator2.setVisibility(View.GONE);
            reviewRecyclerView.setVisibility(View.VISIBLE);
            if (data != null && !data.isEmpty()) {
                reviewsAdapter.swapData(data);
            } else {
                noReview.setVisibility(View.VISIBLE);
                noReview.setTypeface(typeFace);
                noReview.setText(getString(R.string.no_review_available));
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Reviewer>> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        // You can be pretty confident that the intent will not be null here.
        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            ArrayList<MovieUtil> arrayList = bundle.getParcelableArrayList(getString(R.string.LIST_KEY));
            int position = bundle.getInt(getString(R.string.POSITION_KEY), getResources().getInteger(R.integer.POSITION_DEFAULT));
            if (arrayList != null) {
                movieUtil = arrayList.get(position);
            }
        }

        id = movieUtil.getId();
        trailer_url = "https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" +
                TMDB_DEVELOPER_KEY;
        review_url = "https://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" +
                TMDB_DEVELOPER_KEY;

        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movieUtil.getImagePath())
                .into(movie_img);
        title.setText(movieUtil.getTitle());
        rating.setText(String.valueOf(movieUtil.getRating()));
        overView.setText(movieUtil.getOverview());
        releaseDate.setText(movieUtil.getReleaseDate());

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/BerkshireSwash-Regular.ttf");
        title.setTypeface(typeFace);
        rating.setTypeface(typeFace);
        releaseDate.setTypeface(typeFace);
        overView.setTypeface(typeFace);
        headTitle.setTypeface(typeFace);
        headDate.setTypeface(typeFace);
        headRating.setTypeface(typeFace);
        headTrailer.setTypeface(typeFace);
        headReview.setTypeface(typeFace);
        LinearLayoutManager trailerLayoutManager, reviewLayoutManager;

        trailerLayoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);
        trailerRecyclerView.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter(this, new ArrayList<Trailer>());
        trailerRecyclerView.setAdapter(trailersAdapter);
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);

        reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewsAdapter = new ReviewsAdapter(this, new ArrayList<Reviewer>());
        reviewRecyclerView.setAdapter(reviewsAdapter);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, ReviewerResultLoaderListener);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = null;
                try {
                    Uri currentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI,
                            movieUtil.getId());
                    cursor = getContentResolver().query(currentMovieUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        getContentResolver().delete(currentMovieUri, null, null);
                        Toast.makeText(DetailsActivity.this, getString(R.string.unmark_favourite),
                                Toast.LENGTH_SHORT).show();

                        floatingActionButton.setImageResource(R.drawable.ic_star_border_black_24dp);

                    } else {
                        Toast.makeText(DetailsActivity.this, getString(R.string.mark_favourite),
                                Toast.LENGTH_SHORT).show();
                        floatingActionButton.setImageResource(R.drawable.ic_star_black_24dp);
                        new MyAsyncTask().execute();
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
        });
        setUpFab();
    }

    private void setUpFab() {
        Uri currentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(currentMovieUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                floatingActionButton.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                floatingActionButton.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
        loadingIndicator.setVisibility(View.VISIBLE);
        return new AsyncTaskLoader<List<Trailer>>(this) {
            @Override
            public List<Trailer> loadInBackground() {
                return QueryUtils.fetchMovieTrailer(trailer_url);
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
        loadingIndicator.setVisibility(View.GONE);
        trailerRecyclerView.setVisibility(View.VISIBLE);
        if (data != null && !data.isEmpty()) {
            trailersAdapter.swapData(data);
        } else {
            noTrailer.setVisibility(View.VISIBLE);
            noTrailer.setTypeface(typeFace);
            noTrailer.setText(getString(R.string.no_trailer_available));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Trailer>> loader) {
        reviewRecyclerView.setAdapter(null);
        trailerRecyclerView.setAdapter(null);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Uri> {
        @Override
        protected Uri doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_M_ID, movieUtil.getId());
            values.put(MovieEntry.COLUMN_NAME, movieUtil.getTitle());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, movieUtil.getReleaseDate());
            values.put(MovieEntry.COLUMN_OVERVIEW, movieUtil.getOverview());
            values.put(MovieEntry.COLUMN_RATING, movieUtil.getRating());
            values.put(MovieEntry.COLUMN_IMAGE_PATH, movieUtil.getImagePath());
            return getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        }
    }

}


