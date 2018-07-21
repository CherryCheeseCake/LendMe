package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.GetCallback;
import com.parse.ParseException;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_calendar);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        btConfirm = (Button) findViewById(R.id.btConfirm);

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEndDates= (ArrayList<Date>) calendar.getSelectedDates();
                //String toast = "Selected: " + calendar.getSelectedDates();
                //Toast.makeText(ItemCalendarActivity.this, toast, LENGTH_SHORT).show();
                final String objectId = getIntent().getStringExtra("objectId");
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.getInBackground(objectId, new GetCallback<Post>() {
                    public void done(Post post, ParseException e) {
                        if (e == null) {
                            try {
                                createTransaction(startEndDates,post);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        calendar.deactivateDates(list);

        ArrayList<Date> arrayList = new ArrayList<>();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String strdate = "7-2-2018";
        String strdate2 = "23-2-2018";

        Date newdate = null;
        try {
            newdate = dateformat.parse(strdate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Date newdate2 = null;
        try {
            newdate2 = dateformat.parse(strdate2);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        arrayList.add(newdate);
        arrayList.add(newdate2);


        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, YYYY", Locale.getDefault())) //
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date())
                // deactivates given dates, non selectable
                .withDeactivateDates(new ArrayList<>(Collections.singletonList(7)))
                // highlight dates in red color, mean they are aleady used.
                .withHighlightedDates(arrayList);





    }



    private void createTransaction(List<Date> startEndDates, Post post) throws ParseException {

        Transaction newTransaction = new Transaction();
        newTransaction.setStartDate(startEndDates.get(0));
        newTransaction.setEndDate(startEndDates.get(startEndDates.size()-1));
        newTransaction.setLender(post.getUser().fetchIfNeeded());
        newTransaction.setBorrower(user);
        newTransaction.setItemPost(post);

        newTransaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.d("ItemCalendarActivity", "Create transaction success");
                    finish();

                }else{
                    e.printStackTrace();
                }
            }
        });



    }


}
