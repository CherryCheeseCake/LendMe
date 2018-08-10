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
import java.util.GregorianCalendar;
import java.util.Locale;



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
        nextYear.add(Calendar.YEAR, 2);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -2);

        ArrayList<Date> multirangelist = new ArrayList<Date>();
        Date date1 = new GregorianCalendar(2018, Calendar.FEBRUARY, 11).getTime();
        Date date2 = new GregorianCalendar(2018, Calendar.FEBRUARY, 20).getTime();
        Date date3= new GregorianCalendar(2018, Calendar.MARCH, 1).getTime();
        Date date4 = new GregorianCalendar(2018, Calendar.MARCH, 10).getTime();

        multirangelist.add(date1);
        multirangelist.add(date2);
//        multirangelist.add(date3);
//        multirangelist.add(date4);



        //calendar.setCustomDayView(new SampleDayViewAdapter());
        //calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new SampleDecorator()));
        calendar.init(lastYear.getTime(), nextYear.getTime(),new SimpleDateFormat("MMMM, YYYY", Locale.getDefault())) //
                //.inMode(CalendarPickerView.SelectionMode.MULTIPLE_RANGE)
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDates(multirangelist)
                .displayOnly();


        return view;
    }


}

