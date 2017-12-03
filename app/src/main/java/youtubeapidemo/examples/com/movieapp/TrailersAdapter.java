package youtubeapidemo.examples.com.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerHolder> {


    private Context context;
    private List<Trailer> trailers;

    TrailersAdapter(Context context, List<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);
        return new TrailerHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/BerkshireSwash-Regular.ttf");
        holder.textView.setText(trailers.get(position).getName());
        holder.textView.setTypeface(typeFace);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    void swapData(List<Trailer> data) {
        trailers = data;
        notifyDataSetChanged();
    }

    public class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer)
        TextView textView;

        TrailerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String key = trailers.get(getAdapterPosition()).getKey();
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key));
            // if youtube isn't installed on phone then use open it on the website
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }
        }
    }
}