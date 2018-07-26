package com.example.jdocter.lendme.model;

import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String favoritePostsKey = "favoritePosts";

    public ParseQuery getFavoritePostsQuery() {
        return getRelation(favoritePostsKey).getQuery();
    }

    public class FavoritesQuery extends ParseQuery {

        public FavoritesQuery() {
            super(User.this.getFavoritePostsQuery());
        }

    }

}