package com.example.jdocter.lendme.HomeFragments.CalenderFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.R;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import com.squareup.timessquare.CalendarCellDecorator;
//import com.squareup.timessquare.CalendarPickerView;

public class CalenderFragment extends Fragment {

    CalendarPickerView calendar;

    public CalenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_calender, container, false);
        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

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



        //calendar.setCustomDayView(new SampleDayViewAdapter());
        //calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new SampleDecorator()));
        calendar.init(lastYear.getTime(), nextYear.getTime(),new SimpleDateFormat("MMMM, YYYY", Locale.getDefault())) //
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(arrayList)
                .displayOnly();


        return view;
    }


}
