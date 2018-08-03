package com.example.jdocter.lendme;

import android.app.Application;

import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.example.jdocter.lendme.model.User;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.networkInterceptors().add(httpLoggingInterceptor);

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
        installation.put("channels","default");//TODO set a channel
        installation.saveInBackground();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}