package com.example.jdocter.lendme.model;

import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String favoritePostsKey = "favoritePosts";
    public static final String itemKey = "itemName";

    public User(){}

    public ParseQuery getFavoritePostsQuery() {
        return getRelation(favoritePostsKey).getQuery();
    }
    
    public class FavoritesQuery extends ParseQuery {

        public FavoritesQuery() {
            super(User.this.getFavoritePostsQuery());
        }

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