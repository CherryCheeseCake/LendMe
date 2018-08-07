package com.example.jdocter.lendme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jdocter.lendme.model.Post;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {


    List<Post> mPosts;
    Context context;

    public TrendingAdapter(List<Post> posts){
        mPosts=posts;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post_borrow, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Post post = (Post) mPosts.get(position);

        //get and set post prices
        double amount =post.getPrice();
        Locale locale=new Locale("en", "US");
        NumberFormat currencyFormatter= NumberFormat.getCurrencyInstance(locale);


        viewHolder.tvBorrowPrice.setText(currencyFormatter.format(amount));
        viewHolder.ivBorrowImage.layout(0,0,0,0);
        String imageUrl = post.getImage().getUrl();
        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .into(viewHolder.ivBorrowImage);


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView ivBorrowImage;
        public TextView tvBorrowPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            ivBorrowImage= (ImageView) itemView.findViewById(R.id.ivBorrowImage);
            tvBorrowPrice= (TextView) itemView.findViewById(R.id.tvBorrowPrice);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Post post=mPosts.get(getAdapterPosition());
            Intent intent=new Intent(context, DetailPostActivity.class);
            intent.putExtra("objectId",post.getObjectId());
            context.startActivity(intent);
        }
    }
}
