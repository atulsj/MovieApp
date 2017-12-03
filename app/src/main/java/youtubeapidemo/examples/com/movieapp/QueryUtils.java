package youtubeapidemo.examples.com.movieapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    private static ArrayList<MovieUtil> extractMovieFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        ArrayList<MovieUtil> images = new ArrayList<>();
        String overview, release_Dt, title;
        double rating;
        try {
            JSONObject root = new JSONObject(movieJSON);
            Log.e("CHECKING", "extract featurefromjson called");
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String poster_path = movie.getString("poster_path");
                overview = movie.getString("overview");
                title = movie.getString("original_title");
                rating = movie.getDouble("vote_average");
                release_Dt = movie.getString("release_date");
                int id = movie.getInt("id");
                images.add(new MovieUtil(poster_path, id, title, overview, release_Dt, rating));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.i(LOG_TAG, "Problem building the URL ");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            Log.e(LOG_TAG, "null url");
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            Log.e(LOG_TAG, "inside try");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Movie JSON results." + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        Log.e("CHECKING", "makehttp request called");
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.e("CHECKING", "read from stream called");
        return output.toString();
    }

    static boolean isNetworkOnline(Context context) {
        boolean isConnected;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isConnected;

    }

    static ArrayList<MovieUtil> fetchMovie(String requestUrl) {
        String jsonResponse = helper(requestUrl);
        Log.e("CHECKING", requestUrl);
        return extractMovieFromJson(jsonResponse);
    }

    private static String helper(String requestUrl) {
        Log.e("CHECKING", "fetchMovie called");
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return jsonResponse;
    }

    static ArrayList<Trailer> fetchMovieTrailer(String requestUrl) {
        String jsonResponse = helper(requestUrl);
        Log.e("CHECKING", requestUrl);
        return extractTrailerListFromJson(jsonResponse);
    }

    private static ArrayList<Trailer> extractTrailerListFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<Trailer> trailer = new ArrayList<>();
        String name, key;
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                name = movie.getString("name");
                key = movie.getString("key");
                trailer.add(new Trailer(name, key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailer;
    }

    static ArrayList<Reviewer> fetchMovieReviewer(String requestUrl) {
        String jsonResponse = helper(requestUrl);
        Log.e("atul!!!", requestUrl);
        return extractReviewerListFromJson(jsonResponse);
    }

    private static ArrayList<Reviewer> extractReviewerListFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<Reviewer> reviewer = new ArrayList<>();
        String author, content;
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                author = movie.getString("author");
                content = movie.getString("content");
                reviewer.add(new Reviewer(content, author));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewer;
    }

}
