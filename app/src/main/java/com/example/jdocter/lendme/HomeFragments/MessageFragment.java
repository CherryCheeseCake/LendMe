package com.example.jdocter.lendme.HomeFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.MessageAdapter;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    ArrayList<Message> messages;
    private SwipeRefreshLayout swipeRefreshLayout;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    private Context context;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //swipeRefreshLayout
        recyclerView=view.findViewById(R.id.rvMessageProfiles);
        recyclerView.setHasFixedSize(true);
        messages=new ArrayList<>();
        messageAdapter=new MessageAdapter(messages);
        context=getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(messageAdapter);

        loadMessages(ParseUser.getCurrentUser());

    }
    private void loadMessages(ParseUser user){
        final Message.Query messageQuery=new Message.Query();
        messageQuery.byReceiver(user);

        messageQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e==null){
                    for (int i=0;i<objects.size(); i++){
                        messages.add(objects.get(i));
                        messageAdapter.notifyItemInserted(messages.size()-1);
                        System.out.println("SIZE OF MESSAGES IS "+messages.size());
                    }
                    messageAdapter.notifyDataSetChanged();
                }else {
                    Log.d("item", "error"+e.getMessage());
                }
            }
        });


    }
}
