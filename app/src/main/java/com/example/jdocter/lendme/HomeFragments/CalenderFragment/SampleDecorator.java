//package com.example.jdocter.lendme.HomeFragments.CalenderFragment;
//
//import android.graphics.Color;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.format.DateFormat;
//import android.text.style.RelativeSizeSpan;
//
//import com.squareup.timessquare.CalendarCellDecorator;
//import com.squareup.timessquare.CalendarCellView;
//
//import java.util.Date;
//
//public class SampleDecorator implements CalendarCellDecorator {
//
//  public String ifBorrowLend(CalendarCellView cellView,Date date) {
//
//    String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
//    String day = (String) DateFormat.format("dd", date); // 20
//    String monthString = (String) DateFormat.format("MMM", date); // Jun
//    String monthNumber = (String) DateFormat.format("MM", date); // 06
//    String year = (String) DateFormat.format("yyyy", date); // 2013
//
//    if (year.equals("2018") && day.equals("25")&& monthNumber.equals("06")) {
//      //cellView.getDayOfMonthTextView().setBackgroundColor(Color.RED);
//      return  "Lend";
//    } else {
//      if (year.equals("2018") && day.equals("29")&& monthNumber.equals("06")) {
//        return "Borrow";
//      }
//      return "";}
//
//  }
//
//
//  public void setSampleDayView(CalendarCellView cellView, Date date) {
////    Calendar cal = Calendar.getInstance();
////    cal.setTime(date);
//    String day = (String) DateFormat.format("dd", date); // 20
//    String year = (String) DateFormat.format("yyyy", date); // 2013
//    if(day.equals("25")&&year.equals("2018") && (cellView.isSelectable())) {
//
//      cellView.getDayOfMonthTextView().setBackgroundColor(Color.RED);
//    }
//  }
//
//
//  @Override
//  public void decorate(CalendarCellView cellView, Date date) {
//    String dateString = Integer.toString(date.getDate());
//    SpannableString string = new SpannableString(dateString + "\n" + ifBorrowLend(cellView,date));
//    string.setSpan(new RelativeSizeSpan(0.5f), 0, dateString.length(),
//            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//    //setSampleDayView(cellView,date);
//    cellView.getDayOfMonthTextView().setText(string);
//
//  }
//}
