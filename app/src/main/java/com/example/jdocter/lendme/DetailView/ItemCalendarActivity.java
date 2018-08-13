package com.example.jdocter.lendme.DetailView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jdocter.lendme.MainActivity;
import com.example.jdocter.lendme.MyCustomReceiver;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class ItemCalendarActivity extends AppCompatActivity {

    CalendarPickerView calendar;
    Button btConfirm;
    List<Date> startEndDates= new ArrayList<Date>();
    ParseUser user = ParseUser.getCurrentUser();
    Post mPost;
    private ArrayList<ParseObject> transactions;
    private String itemImageUrl;
    private String username;
    private String borrowerUrl;



    //push notification

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_calendar);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        btConfirm = (Button) findViewById(R.id.btConfirm);

        ArrayList<Date> arrayList = new ArrayList<>();

        final String objectId = getIntent().getStringExtra("objectId");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        final ArrayList<Date> finalArrayList = arrayList;

        try {
            mPost = (Post) query.get(objectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        itemImageUrl = mPost.getImage().getUrl();

        List<ParseObject> transactions = new ArrayList<>();

        ParseQuery<ParseObject> query1 = mPost.getTransactionQuery();
        try {
            String name = mPost.getItem();
            transactions = query1.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = (Transaction) transactions.get(i);

            Calendar start = Calendar.getInstance();
            start.setTime(transaction.getStartDate());
            Calendar end = Calendar.getInstance();
            end.setTime(transaction.getEndDate());
            end.add(Calendar.DATE, 1);

            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                finalArrayList.add(date);
            }


        }

        initCalender(finalArrayList);


        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEndDates= (ArrayList<Date>) calendar.getSelectedDates();

                try {
                    createTransaction(startEndDates,mPost);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //push

                Toast toast = Toast.makeText(ItemCalendarActivity.this,"Post Sucess!",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent i = new Intent(ItemCalendarActivity.this, MainActivity.class);
                startActivity(i);


            }
        });



    }

    public void pushNotification(Transaction transaction){
        try {
            username = user.fetchIfNeeded().getUsername();
            borrowerUrl=user.fetchIfNeeded().getParseFile("profileImage").getUrl();
        } catch (ParseException e) {
            Log.e("ItemCalendarActivity","no Username");
        }

        JSONObject payload = new JSONObject();
//        final DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
//        startEndDates.get(0) (dateformat);

        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(startEndDates.get(0));
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(startEndDates.get(startEndDates.size()-1));
        String date = DateUtils.formatDateRange(this, cal1.getTimeInMillis(), cal2.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_ALL);



        try {
            payload.put("sender", ParseInstallation.getCurrentInstallation().getInstallationId());
            payload.put("itemImageUrl", itemImageUrl);
            payload.put("startEndDates",date);
            payload.put("borrowerName",username);
            payload.put("borrowerProfile",borrowerUrl);
            payload.put("transactionId", transaction.getObjectId());
            payload.put("item",mPost.getItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());


        ParseCloud.callFunctionInBackground("pushChannelTest", data);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));
    }

    public void initCalender(ArrayList finalArrayList) {

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        calendar.deactivateDates(list);



        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, YYYY", Locale.getDefault())) //
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date())
                // deactivates given dates, non selectable
                .withDeactivateDates(new ArrayList<>(Collections.singletonList(7)))
                // highlight dates in red color, mean they are aleady used.
                .withHighlightedDates(finalArrayList);



    }

    private void createTransaction(List<Date> startEndDates, final Post post) throws ParseException {

        Date startDate = startEndDates.get(0);
        Date endDate = startEndDates.get(startEndDates.size()-1);


        final Transaction newTransaction = new Transaction();
        newTransaction.setStartDate(startDate);
        newTransaction.setEndDate(endDate);
        newTransaction.setLender(post.getUser().fetchIfNeeded());
        newTransaction.setBorrower(user);
        newTransaction.setItemPost(post);
        newTransaction.setStatusCode(1);
        newTransaction.setCost(post.getPrice()*(startEndDates.size()));

        newTransaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    post.addTransaction(newTransaction);
                    post.saveInBackground();
                    Log.d("ItemCalendarActivity", "Create transaction success");
                    pushNotification(newTransaction);
                    finish();
                }else{
                    e.printStackTrace();
                }
            }
        });





    }


}
