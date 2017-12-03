package youtubeapidemo.examples.com.movieapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import youtubeapidemo.examples.com.movieapp.data.MovieContract;
import youtubeapidemo.examples.com.movieapp.settings.SettingsActivity;

import static youtubeapidemo.examples.com.movieapp.BuildConfig.TMDB_DEVELOPER_KEY;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        android.app.LoaderManager.LoaderCallbacks<ArrayList<MovieUtil>> {

    @BindView(R.id.images_grid_view)
    GridView gridView;
    @BindView(R.id.internet_status)
    TextView textView;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private String POPULAR_MOVIE_URL;
    private MovieGridAdapter mAdapter;
    public static final int INT_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        boolean status = QueryUtils.isNetworkOnline(this);
        if (status) {
            textView.setVisibility(View.GONE);
            setupSharedPreferences();
            mAdapter = new MovieGridAdapter(this, new ArrayList<MovieUtil>());
            gridView.setAdapter(mAdapter);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
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


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        POPULAR_MOVIE_URL = "https://api.themoviedb.org/3" + sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_popular_value)) + "?api_key=" + TMDB_DEVELOPER_KEY;
        getLoaderManager().initLoader(INT_LOADER, null, this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            if (sharedPreferences.getString(key, getString(R.string.pref_favourite_value)).equals("/movie/")) {
                LoaderManager.LoaderCallbacks<ArrayList<MovieUtil>> ReviewerResultLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<MovieUtil>>() {

                    @Override
                    public Loader<ArrayList<MovieUtil>> onCreateLoader(int id, Bundle args) {
                        loadingIndicator.setVisibility(View.VISIBLE);
                        return new AsyncTaskLoader<ArrayList<MovieUtil>>(MainActivity.this) {
                            @Override
                            public ArrayList<MovieUtil> loadInBackground() {
                                ArrayList<MovieUtil> arrayList = new ArrayList<>();
                                Cursor cursor = null;
                                try {
                                    cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, MovieContract.MovieEntry.COLUMN_RATING + " ASC");
                                    while (cursor != null && cursor.moveToNext()) {
                                        int titleColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME);
                                        int overviewColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
                                        int releaseDateColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                                        int ratingColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
                                        int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_M_ID);
                                        int imagePathColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH);
                                        String title = cursor.getString(titleColumnIndex);
                                        String overview = cursor.getString(overviewColumnIndex);
                                        String release_Date = cursor.getString(releaseDateColumnIndex);
                                        int id = cursor.getInt(idColumnIndex);
                                        double rating = cursor.getDouble(ratingColumnIndex);
                                        String image_path = cursor.getString(imagePathColumnIndex);
                                        arrayList.add(new MovieUtil(image_path, id, title, overview, release_Date, rating));
                                    }
                                } finally {
                                    if (cursor != null)
                                        cursor.close();
                                }
                                return arrayList;
                            }

                            @Override
                            protected void onStartLoading() {
                                super.onStartLoading();
                                forceLoad();
                            }
                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<ArrayList<MovieUtil>> loader, final ArrayList<MovieUtil> data) {
                        loadingIndicator.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        if (data.isEmpty()) {
                            Toast.makeText(MainActivity.this, getString(R.string.no_fav_movie), Toast.LENGTH_LONG).show();
                        }
                        if(data==null)
                            return;
                        mAdapter.swapData(data);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                                Bundle b = new Bundle();
                                b.putInt(getString(R.string.POSITION_KEY), position);
                                b.putParcelableArrayList(getString(R.string.LIST_KEY), data);
                                intent.putExtras(b);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onLoaderReset(Loader<ArrayList<MovieUtil>> loader) {
                        gridView.setAdapter(null);
                    }

                };
                getLoaderManager().restartLoader(INT_LOADER, null, ReviewerResultLoaderListener);
            } else {
                POPULAR_MOVIE_URL = "https://api.themoviedb.org/3" + sharedPreferences.getString(key, getString(R.string.pref_popular_value)) + "?api_key=" + TMDB_DEVELOPER_KEY;
                getLoaderManager().restartLoader(INT_LOADER, null, this);
            }
        }
    }

    @Override
    public Loader<ArrayList<MovieUtil>> onCreateLoader(final int id, Bundle args) {
        loadingIndicator.setVisibility(View.VISIBLE);
        return new AsyncTaskLoader<ArrayList<MovieUtil>>(MainActivity.this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public ArrayList<MovieUtil> loadInBackground() {
                return QueryUtils.fetchMovie(POPULAR_MOVIE_URL);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieUtil>> loader, final ArrayList<MovieUtil> data) {
        loadingIndicator.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        if (data == null)
            return;
        mAdapter.swapData(data);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt(getString(R.string.POSITION_KEY), position);
                b.putParcelableArrayList(getString(R.string.LIST_KEY), data);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieUtil>> loader) {
        gridView.setAdapter(null);
    }
}
