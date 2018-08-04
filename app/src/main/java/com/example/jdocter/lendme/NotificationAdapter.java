package com.example.jdocter.lendme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jdocter.lendme.model.Transaction;

import java.util.List;


/**
 *  Notification Adapter:
 *  Handles the different types of notifications and implements the correct
 *  swipe to reveal depending on the status of the transaction.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Transaction> mTransactions;
    private static final float buttonWidth = 400;
    public static final int ITEM_HOLD = 0; // 0 this is a "transaction" that represents a user putting their own item on hold
    public static final int BORROWER_INITIATED = 1;
    public static final int LENDER_ACCEPTED = 2;
    public static final int CONFIRMED_DELIVERY = 3;
    public static final int CONFIRMED_RETURN = 4;
    public static final int CANCELED_BORROWER = 5;
    public static final int CANCELED_LENDER = 6;
    public static final int COMPLETE = 7;



    public NotificationAdapter(List<Transaction> transactions) {
        mTransactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//        final MyView itemView = (MyView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        MyView itemView = new MyView(parent.getContext());
        itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // get status of transaction
        final Transaction transaction = (Transaction) mTransactions.get(i);
        int status = transaction.getStatusCode();

        // get correct viewholder depending on transaction status
        switch (status) {
            case BORROWER_INITIATED:
                break;
            case LENDER_ACCEPTED:
                break;
            case CONFIRMED_DELIVERY:
                break;
            case CONFIRMED_RETURN:
                break;
            case CANCELED_BORROWER:
                break;
            case CANCELED_LENDER:
                break;
        }

        final ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Transaction transaction = (Transaction) mTransactions.get(i);
        viewHolder.tvBody.setText("hi");
        viewHolder.tvNat.setText("Poland");

    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        public TextView tvBody;
        public TextView tvNat;
        public CardView card;
        public Button btnRed;
        public Button btnBlue;

        public ViewHolder(View itemView) {
            super(itemView);
//             perform findViewById lookups
            tvBody = itemView.findViewById(R.id.tvBody);
            tvNat = itemView.findViewById(R.id.nationality);
            card = itemView.findViewById(R.id.cardNot);
            btnRed = itemView.findViewById(R.id.btnBlue);
            btnBlue = itemView.findViewById(R.id.btnBlue);

            card.setOnTouchListener(this);
        }


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.e("NotificationAdapter", "OnTOuch");
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (view.getTranslationX() == 0) {
                    view.setTranslationX(-buttonWidth);
                } else {
                    view.setTranslationX(0);
                }
            }
            return false;
        }

    }


    /**
     * Custom CardView to inflate multiple views
     */
    public class MyView extends CardView {

        public TextView tvBody;

        public MyView(Context context) {
            super(context);
            inflate(context, R.layout.item_notification, this);
            inflate(context, R.layout.notification_buttons, this);
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
        }

    }

}

