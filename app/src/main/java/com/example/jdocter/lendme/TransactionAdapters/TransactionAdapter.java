package com.example.jdocter.lendme.TransactionAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        Context context;
        List<Transaction> mTransactions;
        final public static String fullNameKey = "fullname";

        public TransactionAdapter(List<Transaction> transactions) {
            mTransactions = transactions;
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final Transaction transaction = (Transaction) mTransactions.get(i);
            Post post = (Post) transaction.getItemPost();
            String imageUrl = null;
            try {
                imageUrl = ((Post) post.fetch()).getImage().getUrl();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // load image using glide
            Glide.with(context)
                    .load(imageUrl)
                    .into(viewHolder.ivTransactionImage);
            viewHolder.tvStartDate.setText(simpleDate(transaction.getStartDate()));
            viewHolder.tvEndDate.setText(simpleDate(transaction.getEndDate()));
            
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View transactionView = inflater.inflate(R.layout.item_transaction, parent, false);
            ViewHolder viewHolder = new ViewHolder(transactionView);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return mTransactions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView ivTransactionImage;
            public TextView tvStartDate;
            public TextView tvEndDate;
            public TextView tvPrice;
            public TextView tvBlurb;

            public ViewHolder(View itemView) {
                super(itemView);
                // perform findViewById lookups
                ivTransactionImage = itemView.findViewById(R.id.ivTransactionImage);
                tvStartDate = itemView.findViewById(R.id.tvDateStart);
                tvEndDate = itemView.findViewById(R.id.tvDateEnd);
                tvPrice = itemView.findViewById(R.id.tvCost);
                tvBlurb = itemView.findViewById(R.id.tvBlurb);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                int position=getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // TODO user clicks on transaction?
                }

            }
        }

        public String simpleDate(Date date) {
            return new SimpleDateFormat("MM/dd/yyyy").format(date);
        }

    }
