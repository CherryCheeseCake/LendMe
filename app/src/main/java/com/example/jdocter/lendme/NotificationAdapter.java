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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.example.jdocter.lendme.model.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *  Notification Adapter:
 *  Handles the different types of notifications and implements the correct
 *  swipe to reveal depending on the status of the transaction.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//        final MyView itemView = (MyView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        MyView itemView = new MyView(parent.getContext());
        itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // get status of transaction
        final Transaction transaction = (Transaction) mTransactions.get(i);
        int status = transaction.getStatusCode();

        String borrowerId = transaction.getBorrowerId();
        String lenderId = transaction.getLenderId();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();

        Date today = getTodayWithoutTime();
        Date startDate = withoutTime(transaction.getStartDate());
        Date endDate = withoutTime(transaction.getEndDate());


        RecyclerView.ViewHolder viewHolder = null;

        // get correct viewholder depending on transaction status
        switch (status) {
            case BORROWER_INITIATED:
                if (borrowerId.equals(currentUserId)) {                 // request made, waiting on lender to approve
                    viewHolder = new TextViewHolder(itemView);
                } else if (lenderId.equals(currentUserId)) {            // request made, need lender approval
                    viewHolder = new SwipeViewHolder(itemView);
                }
                break;

            case LENDER_ACCEPTED:
                if (borrowerId.equals(currentUserId)) {
                    if (today.before(startDate)) {                      // request confirmed, prepare borrower for pickup
                        viewHolder = new TextViewHolder(itemView);
                    } else if (today.after(startDate)) {                // transaction should happened today, confirm pickup
                        viewHolder = new SwipeViewHolder(itemView);
                    }
                } else if (lenderId.equals(currentUserId)) {            // prepare lender for pickup
                    viewHolder = new TextViewHolder(itemView);
                }
                break;

            case CONFIRMED_DELIVERY:
                if (borrowerId.equals(currentUserId)) {                 // borrower has item, reminder: days left
                    viewHolder = new TextViewHolder(itemView);
                } else if (lenderId.equals(currentUserId)) {
                    if (today.before(endDate)) {                        // request confirmed, prepare lender for return
                        viewHolder = new TextViewHolder(itemView);
                    } else if (today.equals(endDate)) {                 // transaction should end today, confirm return
                        viewHolder = new SwipeViewHolder(itemView);
                    }
                }
                break;

            case CONFIRMED_RETURN:                                      // both user's rate experience/report anything unusual
                viewHolder = new RatingViewHolder(itemView);
            case CANCELED_BORROWER:
                // TODO once a user can actually cancel
                break;
            case CANCELED_LENDER:
                // TODO once a user can actually cancel
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        // get status of transaction
        final Transaction transaction = (Transaction) mTransactions.get(i);
        int status = transaction.getStatusCode();

        String borrowerId = transaction.getBorrowerId();
        String lenderId = transaction.getLenderId();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();

        Date today = getTodayWithoutTime();
        Date startDate = withoutTime(transaction.getStartDate());
        Date endDate = withoutTime(transaction.getEndDate());

        String item = null;
        try {
            item = ((Post) transaction.getItemPost().fetch()).getItem();
        } catch (ParseException e) {
            Log.e("NotificationAdapter","Failed to fetch post data");
        }
        String startDateString = simpleDate(startDate);
        String endDateString = simpleDate(endDate);
        String borrower = null;
        String lender = null;
        try {
            borrower = ((User) transaction.getBorrower().fetch()).getFullName();
            lender = ((User) transaction.getLender().fetch()).getFullName();
        } catch (ParseException e) {
            Log.e("NotificationAdapter","Failed to fetch user data");
        }


        // get correct viewholder depending on transaction status
        switch (status) {
            case BORROWER_INITIATED:
                if (borrowerId.equals(currentUserId)) {
                    // request made, waiting on lender to approve
                    TextViewHolder holder = (TextViewHolder) viewHolder;
                    holder.tvtitle.setText(item);
                    holder.tvBody.setText("Waiting for "+lender+" to accept your request for "+item+" on "+startDateString);
                } else if (lenderId.equals(currentUserId)) {
                    // request made, need lender approval
                    SwipeViewHolder holder = (SwipeViewHolder) viewHolder;
                    holder.tvtitle.setText(item);
                    holder.tvBody.setText(borrower+" requests to borrow "+item+" from "+startDateString+" to "+endDateString);
                    holder.btnBlue.setText("Accept");
                    holder.btnRed.setText("Deny");
                    // TODO set onclick listeners
                }
                break;

            case LENDER_ACCEPTED:
                if (borrowerId.equals(currentUserId)) {
                    if (today.before(startDate)) {
                        // request confirmed, prepare borrower for pickup
                        TextViewHolder holder = (TextViewHolder) viewHolder;
                        holder.tvtitle.setText(item);
                        int daysLeft = getDifferenceInDays(today,startDate);
                        if (daysLeft == 1){
                            holder.tvBody.setText("Reminder: Pickup " + item+" from "+ lender + " tomorrow.");

                        } else {
                            holder.tvBody.setText("Reminder: Pickup " + item + " from " + lender+  " in " + String.valueOf(daysLeft) + " days.");
                        }

                    } else if (today.after(startDate)) {
                        // transaction should happened today, confirm pickup
                        SwipeViewHolder holder = (SwipeViewHolder) viewHolder;
                        holder.tvBody.setText("Did you recieve "+item+" from "+lender+"?");
                        holder.btnRed.setText("No.");
                        holder.btnBlue.setText("Yes!");
                        // TODO set onclick listeners

                    }
                } else if (lenderId.equals(currentUserId)) {
                    // prepare lender for pickup
                    TextViewHolder holder = (TextViewHolder) viewHolder;
                    holder.tvtitle.setText(item);
                    int daysLeft = getDifferenceInDays(today,startDate);
                    if (daysLeft == 1){
                        holder.tvBody.setText("Reminder: "+ borrower+" will pick up "+ item + " tomorrow.");

                    } else {
                        holder.tvBody.setText("Reminder: "+ borrower+" will pick up "+ item +" in " + String.valueOf(daysLeft) + " days.");
                    }
                }
                break;

            case CONFIRMED_DELIVERY:
                int daysLeft = getDifferenceInDays(today,endDate);

                if (borrowerId.equals(currentUserId)) {
                    // borrower has item, reminder: days left
                    TextViewHolder holder = (TextViewHolder) viewHolder;
                    holder.tvtitle.setText(item);
                    if (daysLeft == 1){
                        holder.tvBody.setText("Don't forget to return"+item+"tomorrow!");

                    } else {
                        holder.tvBody.setText("Reminder: Return in "+ String.valueOf(daysLeft) + " days.");
                    }
                } else if (lenderId.equals(currentUserId)) {
                    if (today.before(endDate)) {
                        // request confirmed, prepare lender for return
                        TextViewHolder holder = (TextViewHolder) viewHolder;
                        holder.tvtitle.setText(item);
                        holder.tvBody.setText(borrower+" will return "+item+" in "+String.valueOf(daysLeft) + " days.");

                    } else if (today.equals(endDate)) {
                        // transaction should end today, confirm return
                        SwipeViewHolder holder = (SwipeViewHolder) viewHolder;
                        holder.tvtitle.setText(item);
                        holder.tvBody.setText("Did "+borrower+" return "+item+"?");
                        holder.btnBlue.setText("Yes!");
                        holder.btnRed.setText("No.");
                        // TODO set onclick listeners

                    }
                }
                break;

            case CONFIRMED_RETURN:
                // both user's rate experience/report anything unusual
                RatingViewHolder holder = (RatingViewHolder) viewHolder;
            case CANCELED_BORROWER:
                // TODO once a user can actually cancel
                break;
            case CANCELED_LENDER:
                // TODO once a user can actually cancel
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }



    public class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBody;
        public TextView tvtitle;
        public ImageView ivSwipe;
        public CardView card;

        public TextViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            tvBody = itemView.findViewById(R.id.tvBody);
            ivSwipe = itemView.findViewById(R.id.ivSwipe);
            card = itemView.findViewById(R.id.cardNot);
            tvtitle = itemView.findViewById(R.id.tvTitle);
            ivSwipe.setImageResource(android.R.color.transparent);
        }
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBody;
        public TextView tvtitle;
        public ImageView ivSwipe;
        public CardView card;

        public RatingViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            tvBody = itemView.findViewById(R.id.tvBody);
            ivSwipe = itemView.findViewById(R.id.ivSwipe);
            card = itemView.findViewById(R.id.cardNot);
            tvtitle = itemView.findViewById(R.id.tvTitle);
        }
    }


    public class SwipeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        public TextView tvBody;
        public TextView tvtitle;
        public ImageView ivSwipe;
        public CardView card;
        public Button btnRed;
        public Button btnBlue;

        public SwipeViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            tvBody = itemView.findViewById(R.id.tvBody);
            ivSwipe = itemView.findViewById(R.id.ivSwipe);
            card = itemView.findViewById(R.id.cardNot);
            btnRed = itemView.findViewById(R.id.btnBlue);
            btnBlue = itemView.findViewById(R.id.btnBlue);
            tvtitle = itemView.findViewById(R.id.tvTitle);

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

    public Date getTodayWithoutTime() {
        return withoutTime(new Date());
    }

    public Date withoutTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public String simpleDate(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }

    public int getDifferenceInDays(Date d1,Date d2) {
        long diffInMillies = Math.abs(d2.getTime() - d1.getTime());
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}

