package com.example.jdocter.lendme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.model.Post;

import java.util.List;

public class LendAdapter extends RecyclerView.Adapter<LendAdapter.ViewHolder> {
    Context context;
    List<Post> mLends;

    public LendAdapter(List<Post> lends) {
        mLends = lends;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Post post = (Post) mLends.get(i);
        String imageUrl = post.getImage().getUrl();
        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions().transform(new RoundedCorners(15)))
                .into(viewHolder.ivLendImage);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lendView = inflater.inflate(R.layout.item_post_lend, parent, false);
        ViewHolder viewHolder = new ViewHolder(lendView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mLends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivLendImage;

        public ViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            ivLendImage = (ImageView) itemView.findViewById(R.id.ivLendImage);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mLends.get(position);
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.putExtra("objectId", post.getObjectId());
                // show the activity
                context.startActivity(intent);
            }

        }
    }
}
