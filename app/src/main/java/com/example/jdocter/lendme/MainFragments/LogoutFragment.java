package com.example.jdocter.lendme.MainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jdocter.lendme.LoginActivity;
import com.example.jdocter.lendme.R;
import com.parse.ParseUser;


public class LogoutFragment extends Fragment {
    private Button logoutButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logoutButton=view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user= ParseUser.getCurrentUser();
                ParseUser.logOut();

                Intent intent= new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
