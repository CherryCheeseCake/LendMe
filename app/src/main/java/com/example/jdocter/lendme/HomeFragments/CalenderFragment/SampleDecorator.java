package com.example.jdocter.lendme.HomeFragments.CalenderFragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.example.jdocter.lendme.R;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Date;

public class SampleDecorator implements CalendarCellDecorator {

  public String ifBorrowLend(CalendarCellView cellView,Date date) {

    String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
    String day = (String) DateFormat.format("dd", date); // 20
    String monthString = (String) DateFormat.format("MMM", date); // Jun
    String monthNumber = (String) DateFormat.format("MM", date); // 06
    String year = (String) DateFormat.format("yyyy", date); // 2013

    if (year.equals("2018") && day.equals("25")) {
      return  "Lend";
    } else {
      if (year.equals("2018") && day.equals("29")) {
        return "Borrow";
      }
      return "";}

  }


  public View setSampleDayView(CalendarCellView cellView, Date date) {
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(date);
    String day = (String) DateFormat.format("dd", date); // 20
//    String day = Integer.toString(date.getDate());

    String year = (String) DateFormat.format("yyyy", date); // 2013
    if(day.equals("25")&&year.equals("2018") && (cellView.isSelectable())) {
      LayoutInflater inflater = LayoutInflater.from(cellView.getContext());
      return inflater.inflate(R.layout.day_view_borrow_end, cellView, false);
    }
    LayoutInflater inflater = LayoutInflater.from(cellView.getContext());
    return inflater.inflate(R.layout.day_view_borrow_start, cellView, false);
  }


  @Override
  public void decorate(CalendarCellView cellView, Date date) {
    String dateString = Integer.toString(date.getDate());
    SpannableString string = new SpannableString(dateString + "\n" + ifBorrowLend(cellView,date));
    string.setSpan(new RelativeSizeSpan(0.5f), 0, dateString.length(),
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    cellView.getDayOfMonthTextView().setText(string);
    //cellView.addView(setSampleDayView(cellView, date));
  }
}
