package com.example.jdocter.lendme.TransactionFragments;

import com.example.jdocter.lendme.TransactionAdapters.TransactionAdapter;
import com.example.jdocter.lendme.TransactionAdapters.UpcomingAdapter;
import com.example.jdocter.lendme.model.Post;
import com.parse.ParseQuery;

/**
 * subclass of HistoryUpcomingFragment specialized for future transactions
 */
public class UpcomingFragment extends HistoryUpcomingFragment {
    @Override
    public TransactionAdapter getAdapter() {
        return new UpcomingAdapter(userTransactions);
    }

    @Override
    public ParseQuery getQuery(Post post) {
        return post.getFutureTransactionQuery();
    }
}
