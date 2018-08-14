package com.example.jdocter.lendme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.model.Message;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    List<Message> mMessage;
    Context context;
    
    public MessageAdapter(List<Message> message){
        mMessage=message;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View messageView = inflater.inflate(R.layout.item_message_preview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(messageView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message = mMessage.get(i);

        //populate the views according to this data
        try {
            String str="@"+message.getSender().fetchIfNeeded().getUsername();
            viewHolder.tvSenderUsername.setText(str);
        } catch (ParseException e) {

        }
        viewHolder.tvMessageContent.setText(message.getMessageKey());


        String profileUrl = null;
        try {
            profileUrl = message.getSender().fetchIfNeeded().getParseFile("profileImage").getUrl();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context)
                .load(profileUrl)
                .apply(new RequestOptions().transform(new CircleTransform(context)))
                .into(viewHolder.ivSenderProfileImage);

        //Prints date that the message was created
        viewHolder.tvDate.setText(getRelativeTimeAgo(message.getCreatedKey().toString()));
        //viewHolder.tvDate.setText(getRelativeTimeAgo(message.getUpdatedKey().toString()));
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvSenderUsername;
        TextView tvMessageContent;
        TextView tvDate;
        ImageView ivSenderProfileImage;

        public ViewHolder(View messageView){
            super(messageView);
            tvSenderUsername= messageView.findViewById(R.id.tvSenderUsername);
            tvMessageContent=messageView.findViewById(R.id.tvMessageContent);
            tvDate=messageView.findViewById(R.id.tvDate);
            ivSenderProfileImage=messageView.findViewById(R.id.ivSenderProfileImage);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Clicked on a profile", Toast.LENGTH_LONG).show();
        }
    }
}
