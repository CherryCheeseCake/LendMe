package com.example.jdocter.lendme.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String favoritePostsKey = "favoritePosts";
    public static final String itemKey = "itemName";
    public static final String locationKey = "location";
    public static final String fullNameKey = "gullName";
    public static final String profileImageKey="profileImage";


    public User(){}

    public String getFullName() { return getString(fullNameKey); }

    public ParseGeoPoint getLocation() { return getParseGeoPoint(locationKey); }

    public ParseFile getProfileImage() {
        return getParseFile(profileImageKey);
    }

    public ParseQuery getFavoritePostsQuery() {
        return getRelation(favoritePostsKey).getQuery();
    }


    public static class QueryFavorites extends ParseQuery {
        public QueryFavorites(ParseQuery query) {
            super(query);
        }

        public QueryFavorites getTop() {
            setLimit(20);
            return this;
        }

        public QueryFavorites dec() {
            orderByDescending("createdAt");
            return this;
        }


        public QueryFavorites byItem(String keyword) {
            whereEqualTo(itemKey, keyword);
            return this;
        }
    }

    
}