package com.example.jdocter.lendme.model;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Transaction")
public class Transaction extends ParseObject implements Comparable<Transaction> {
    public static final String startKey = "startDate";
    public static final String endKey = "endDate";
    public static final String lenderKey = "lender";
    public static final String borrowerKey = "borrower";
    public static final String itemKey = "item";
    public static final String costKey = "cost";
    public static final String statusCodeKey = "statusCode";
    public static final String updatedAtKey = "updatedAt";


    public int getStatusCode() { return getInt(statusCodeKey); }
    public void setStatusCode(int code){put(statusCodeKey,code);}

    public Date getUpdatedAt(){
        return getDate(updatedAtKey);
    }

    public Date getStartDate(){
        return getDate(startKey);
    }
    public void setStartDate(Date date){
        put(startKey,date);
    }

    public Date getEndDate(){
        return getDate(endKey);
    }
    public void setEndDate(Date date){
        put(endKey,date);
    }

    public String getLenderId() {
        return getParseUser(lenderKey).getObjectId();
    }
    public String getBorrowerId() {
        return getParseUser(borrowerKey).getObjectId();
    }

    public ParseUser getLender() {
        return getParseUser(lenderKey);
    }

    public void setLender(ParseUser user) {
        put(lenderKey,user);
    }

    public ParseUser getBorrower() {
        return getParseUser(borrowerKey);
    }

    public void setBorrower(ParseUser user) {
        put(borrowerKey,user);
    }

    public double getCost() { return getDouble(costKey); }

    public void setCost(Float price) { put(costKey, price); }

    public ParseObject getItemPost(){
        return getParseObject(itemKey);

    }

    public void setItemPost(Post post){
        put(itemKey,post);
    }

    public String getTimestamp() { return getRelativeTimeAgo(getCreatedAt()); }


    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(Date date) {

        String relativeDate = "";
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();


        return relativeDate;
    }

    // Comparable for sorting a list of transactions
    @Override
    public int compareTo(@NonNull Transaction transaction) {
        return transaction.getStartDate().compareTo(this.getStartDate());
    }
    public static class Query extends ParseQuery<Transaction> {
        public Query() {
            super(Transaction.class);
        }

        public Transaction.Query getTop() {
            setLimit(20);
            return this;
        }

        public Transaction.Query dec() {
            orderByDescending("updatedAt");
            return this;
        }

        public Transaction.Query withUser() {
            include("user");
            return this;
        }

        public Transaction.Query excludeStatusCode(int i) {
            whereNotEqualTo(statusCodeKey,i);
            return this;
        }

        public Transaction.Query byLender(ParseUser user) {
            whereEqualTo(lenderKey, user);
            return this;
        }

        public Transaction.Query byBorrower(ParseUser user) {
            whereEqualTo(borrowerKey, user);
            return this;
        }

        public Transaction.Query excludePast() {
            whereGreaterThan(endKey,new Date());
            return this;
        }


    }

}