package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemCalendarActivity extends AppCompatActivity {

    CalendarPickerView calendar;
    Button btConfirm;
    List<Date> startEndDates= new ArrayList<Date>();
    ParseUser user = ParseUser.getCurrentUser();
    Post mPost;
    ArrayList<ParseObject> transactions;


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
//        query.getInBackground(objectId, new GetCallback<Post>() {
//            public void done(Post post, ParseException e) {
//                if (e == null) {
//                    mPost = post;
//                    final List<Transaction> transactions = new ArrayList<>();
//
//                    ParseQuery<ParseObject> query1= mPost.getTransactions();
//                    query1.findInBackground(new FindCallback<ParseObject>() {
//                        @Override
//                        public void done(List<ParseObject> objects, ParseException e) {
//                            for (int i = 0; i < objects.size();i++) {
//                                transactions.add((Transaction) objects.get(i));
//                                Transaction transaction = (Transaction)transactions;
//                                finalArrayList.add(transaction.getStartDate());
//
//
//                            initCalender(finalArrayList);
//                            }
//
//                        }
//                    });
//
//
//                } else {
//                    e.printStackTrace();
//                }
//            }
//
//        });

        try {
            mPost = (Post) query.get(objectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                //String toast = "Selected: " + calendar.getSelectedDates();
                //Toast.makeText(ItemCalendarActivity.this, toast, LENGTH_SHORT).show();
                try {
                    createTransaction(startEndDates,mPost);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


//        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
//        String strdate = "7-2-2018";
//        String strdate2 = "23-2-2018";
//
//        Date newdate = null;
//        try {
//            newdate = dateformat.parse(strdate);
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
//        Date newdate2 = null;
//        try {
//            newdate2 = dateformat.parse(strdate2);
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
//        arrayList.add(newdate);
//        arrayList.add(newdate2);


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

        final Transaction newTransaction = new Transaction();
        newTransaction.setStartDate(startEndDates.get(0));
        newTransaction.setEndDate(startEndDates.get(startEndDates.size()-1));
        newTransaction.setLender(post.getUser().fetchIfNeeded());
        newTransaction.setBorrower(user);
        newTransaction.setItemPost(post);

        newTransaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    post.addTransaction(newTransaction);
                    post.saveInBackground();
                    Log.d("ItemCalendarActivity", "Create transaction success");
                    finish();
                }else{
                    e.printStackTrace();
                }
            }
        });





    }


}
