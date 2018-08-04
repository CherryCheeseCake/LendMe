package com.example.jdocter.lendme.TransactionAdapters;

import android.support.annotation.NonNull;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.example.jdocter.lendme.model.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * subclass of TransactionAdapter, specifically handles past transactions
 */
public class HistoryAdapter extends TransactionAdapter {

    public HistoryAdapter(List<Transaction> transactions) {
        super(transactions);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        final Transaction transaction = (Transaction) mTransactions.get(i);
        Post post = (Post) transaction.getItemPost();
        final String cost = Double.toString(transaction.getCost());
        final String item = post.getItem();

        // different input depending on borrow vs lent
        if (transaction.getLender() != transaction.getBorrower()) {
            if (post.getUser() == ParseUser.getCurrentUser()) {
                try {
                    final String borrower = ((User) transaction.getBorrower().fetch()).getFullName();
                    final String blurb = "Lent " + item + " to " + borrower;
                    final String costWithSign = "+ $" + cost;

                    viewHolder.tvBlurb.setText(blurb);
                    viewHolder.tvPrice.setText(costWithSign);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    final User user = (User) transaction.getLender().fetch();
                    final String lender = user.getFullName();
                    final String blurb = "Borrowed " + item + " from " + lender;
                    final String costWithSign = "- $" + cost;

                    viewHolder.tvBlurb.setText(blurb);
                    viewHolder.tvPrice.setText(costWithSign);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
