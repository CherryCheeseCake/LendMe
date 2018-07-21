package com.example.jdocter.lendme.model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")

public class Post extends ParseObject {
    public static final String descriptionKey = "description";
    public static final String imageKey = "image";
    public static final String ownerKey = "ownerId";
    public static final String priceKey = "price";
    public static final String itemKey = "itemName";

    public String getDescription() {
        return getString(descriptionKey);
    }

    public void setDescription(String description) { put(descriptionKey,description); }

    public String getItem() {
        return getString(itemKey);
    }

    public void setItem(String description) { put(itemKey,description); }

    public ParseFile getImage() {
        return getParseFile(imageKey);
    }

    public void setImage(ParseFile image) {
        put(imageKey,image);
    }

    public ParseUser getUser() {
        return getParseUser(ownerKey);
    }

    public void setUser(ParseUser user) {
        put(ownerKey,user);
    }

    public String getTimestamp() { return getRelativeTimeAgo(getCreatedAt()); }

    public int getPrice() { return getInt(priceKey); }

    public void setPrice(Float price) { put(priceKey, price); }




    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query dec() {
            orderByDescending("createdAt");
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query byUser(ParseUser user) {
            whereEqualTo(ownerKey,user);
            return this;
        }

        // TODO query by geoloc

        // TODO user specified query
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(Date date) {

        String relativeDate = "";
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();


        return relativeDate;
    }
}



