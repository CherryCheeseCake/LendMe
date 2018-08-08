package com.example.jdocter.lendme.model;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

public class Message extends ParseObject {

    public static final String senderKey="sender";
    public static final String receiverKey="receiver";
    public static final String updatedAtKey = "updatedAt";
    public static final String messageKey="message";


    @Override
    public Date getUpdatedAt() {
        return getDate(updatedAtKey);
    }

    public String getSenderId(){
        return getParseUser(senderKey).getObjectId();
    }

    public ParseUser getSender(){
        return getParseUser(senderKey);
    }
    public void setSender(ParseUser user){
        put(senderKey, user);
    }

    public String getReceiverId(){
        return getParseUser(receiverKey).getObjectId();
    }

    public ParseUser getReceiver(){
        return getParseUser(receiverKey);
    }
    public void setReceiver(ParseUser user){
        put(receiverKey, user);
    }

    public String getMessageKey(){
        return getString(messageKey);
    }
    public void setMessageKey(String message){
        put(messageKey, message);
    }

    public static class Query extends ParseQuery<Message>{
        public Query(){super(Message.class);}

        public Message.Query bySender(ParseUser user){
            whereEqualTo(senderKey, user);
            return this;
        }

        public Message.Query byReceiver(ParseUser user){
            whereEqualTo(receiverKey, user);
            return this;
        }
    }

}
