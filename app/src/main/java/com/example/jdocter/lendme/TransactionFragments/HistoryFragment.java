package com.example.jdocter.lendme.TransactionFragments;

import com.example.jdocter.lendme.TransactionAdapters.HistoryAdapter;
import com.example.jdocter.lendme.TransactionAdapters.TransactionAdapter;
import com.example.jdocter.lendme.model.Post;
import com.parse.ParseQuery;

/**
 * subclass of HistoryUpcomingFragment specialized for past transactions
 */
public class HistoryFragment extends HistoryUpcomingFragment {

    @Override
    public TransactionAdapter getAdapter() {
        return new HistoryAdapter(userTransactions);
    }

    @Override
    public ParseQuery getQuery(Post post) {
        return post.getPastTransactionQuery();
    }
}
