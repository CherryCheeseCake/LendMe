package com.example.jdocter.lendme.HomeFragments.CalenderFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.jdocter.lendme.R;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.DayViewAdapter;

public class SampleDayViewBorrowEnd extends SampleDayViewAdapter implements DayViewAdapter {
  @Override
  public void makeCellView(CalendarCellView parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View layout = inflater.inflate(R.layout.day_view_borrow_end, parent, false);
    parent.addView(layout);
    parent.setDayOfMonthTextView((TextView) layout.findViewById(R.id.day_view));
  }
}
