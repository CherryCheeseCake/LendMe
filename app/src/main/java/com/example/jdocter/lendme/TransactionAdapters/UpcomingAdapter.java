package com.example.jdocter.lendme.TransactionAdapters;

import android.support.annotation.NonNull;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * subclass of TransactionAdapter, specifically handles current and future transactions
 */
public class UpcomingAdapter extends TransactionAdapter {

    public UpcomingAdapter(List<Transaction> transactions) {
        super(transactions);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        final Transaction transaction = (Transaction) mTransactions.get(i);
        Post post = (Post) transaction.getItemPost();
        final String item = post.getItem();
        viewHolder.tvPrice.setText("pending...");


        // different input depending on borrow vs lent
        if (post.getUser() == ParseUser.getCurrentUser()) {
            try {
                final String borrower = transaction.getBorrower().fetch().getString(fullNameKey);
                final String blurb = "Lending " + item + " to " + borrower;
                viewHolder.tvBlurb.setText(blurb);
            } catch (ParseException e) { e.printStackTrace(); }

        } else {
            try {
                final String lender = transaction.getLender().fetch().getString(fullNameKey);
                final String blurb = "Borrowing " + item + " from " + lender;
                viewHolder.tvBlurb.setText(blurb);
            } catch (ParseException e) { e.printStackTrace(); }
        }
    }
}
