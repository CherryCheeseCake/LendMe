package com.example.jdocter.lendme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.example.jdocter.lendme.model.User;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SimpleNotificationAdapter extends RecyclerView.Adapter<SimpleNotificationAdapter.ViewHolder> {

    public static final String TAG = "NotificationAdapter";

    private List<Transaction> mTransactions;
    private static final float buttonWidth = 460;
    public static final int ITEM_HOLD = 0; // 0 this is a "transaction" that represents a user putting their own item on hold
    public static final int BORROWER_INITIATED = 1;
    public static final int LENDER_ACCEPTED = 2;
    public static final int CONFIRMED_DELIVERY = 3;
    public static final int CONFIRMED_RETURN = 4;
    public static final int CANCELED_BORROWER = 5;
    public static final int CANCELED_LENDER = 6;
    public static final int COMPLETE = 7;
    public static final int MALCONDUCT = 8;

    private Context context;


    public SimpleNotificationAdapter(List<Transaction> transactions) {
        mTransactions = transactions;
    }


    @NonNull
    @Override
    public SimpleNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        SimpleNotificationAdapter.MyView itemView = new SimpleNotificationAdapter.MyView(context);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleNotificationAdapter.ViewHolder holder, final int position) {
        // get status of transaction
        final Transaction transaction = (Transaction) mTransactions.get(position);



        String item = null;
        try {
            item = ((Post) transaction.getItemPost().fetch()).getItem();
        } catch (ParseException e) {
            Log.e("NotificationAdapter", "Failed to fetch post data");
        }
        String startDateString = simpleDate(transaction.getStartDate());
        String endDateString = simpleDate(transaction.getEndDate());
        String borrower = null;
        try {
            borrower = ((User) transaction.getBorrower().fetch()).getFullName();
        } catch (ParseException e) {
            Log.e("NotificationAdapter", "Failed to fetch user data");
        }

        // request made, need lender approval
        holder.tvtitle.setText(item);
        holder.tvBody.setText(borrower + " requests to borrow " + item + " from " + startDateString + " to " + endDateString);

        // set onclickListener
        holder.btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                transaction.setStatusCode(CANCELED_LENDER);
                mTransactions.remove(position);
                notifyItemRemoved(position);
                //TODO Create a notification to the borrower
                try {
                    pushNotification(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(view.getContext(),"Transaction denied.",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        holder.btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.setStatusCode(LENDER_ACCEPTED);
                notifyItemChanged(position);
                //TODO Create a notification to the borrower
                try {
                    pushNotification(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(view.getContext(),"Transaction confirmed!",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });



    }


    public void pushNotification(boolean ifAccepted) throws JSONException {
        JSONObject payload = new JSONObject();
        if(ifAccepted){
            payload.put("message", "Your request has been accepted");
        }else{
            payload.put("message", "Sorry, your request has been declined");
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());


        ParseCloud.callFunctionInBackground("pushChannelTest", data);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        public TextView tvBody;
        public TextView tvtitle;
        public ImageView ivSwipe;
        public CardView card;
        public Button btnRed;
        public Button btnBlue;

        // onTouch variables
        private final int MAX_CLICK_DISTANCE = 5;
        private long startClickTime;
        private float x1;
        private float x2;
        private float dx;

        public ViewHolder(View itemView) {
            super(itemView);
            // perform findViewById lookups
            tvBody = itemView.findViewById(R.id.tvBody);
            ivSwipe = itemView.findViewById(R.id.ivSwipe);
            card = itemView.findViewById(R.id.cardNot);
            btnRed = itemView.findViewById(R.id.btnRed);
            btnBlue = itemView.findViewById(R.id.btnBlue);
            tvtitle = itemView.findViewById(R.id.tvTitle);

            card.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    x1 = event.getX();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    x2 = event.getX();
                    dx = x2-x1;

                    if(dx < - MAX_CLICK_DISTANCE) view.setTranslationX(-buttonWidth);
                    if(dx > MAX_CLICK_DISTANCE) view.setTranslationX(0);

                }
                case MotionEvent.ACTION_CANCEL:
                {
                    x2 = event.getX();
                    dx = x2-x1;

                    if(dx < - MAX_CLICK_DISTANCE) view.setTranslationX(-buttonWidth);
                    if(dx > MAX_CLICK_DISTANCE) view.setTranslationX(0);

                }
            }
            return false;
        }

    }

    /**
     * Custom CardView to inflate multiple views
     */
    public class MyView extends CardView {

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

}
