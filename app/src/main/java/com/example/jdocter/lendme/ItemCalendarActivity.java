package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ItemCalendarActivity extends AppCompatActivity {

    CalendarPickerView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_calendar);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);


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


}
