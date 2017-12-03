package youtubeapidemo.examples.com.movieapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
    private List<Reviewer> reviewers;
    private Context context;

    ReviewsAdapter(Context context, ArrayList<Reviewer> reviewers) {
        this.context = context;
        this.reviewers = reviewers;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Reviewer reviewer=reviewers.get(position);
        holder.auhtor.setText(reviewer.getAuthor());
        holder.content.setText(reviewer.getContent());
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/BerkshireSwash-Regular.ttf");
        holder.auhtor.setTypeface(typeFace);
        holder.content.setTypeface(typeFace);
    }

    @Override
    public int getItemCount() {
        return reviewers.size();
    }

    void swapData(List<Reviewer> data) {
        reviewers=data;
        notifyDataSetChanged();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.author)
        TextView auhtor;

        public ReviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
