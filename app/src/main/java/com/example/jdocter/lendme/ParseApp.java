package com.example.jdocter.lendme;

import android.app.Application;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.example.jdocter.lendme.model.User;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Transaction.class);
        ParseUser.registerSubclass(User.class);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        Parse.enableLocalDatastore(this);




        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("CanIBorrow")
                .clientKey("qwe123!@#")
                .server("http://caniborrow.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "72446384673");
        installation.saveInBackground();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}