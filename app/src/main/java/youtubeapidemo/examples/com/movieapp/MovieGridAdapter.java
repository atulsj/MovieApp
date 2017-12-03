package youtubeapidemo.examples.com.movieapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


class MovieGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MovieUtil> movie;

    MovieGridAdapter(Context context, ArrayList<MovieUtil> data) {
        mContext = context;
        movie = data;
    }

    void swapData(ArrayList<MovieUtil> list) {
        movie = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movie.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        String uri = movie.get(position).getImagePath();
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + uri).placeholder(R.drawable.movie_placeholder).error(R.drawable.error_placeholder).into(imageView);
        //Picasso.with(mContext).setIndicatorsEnabled(true);
        return imageView;
    }
}
