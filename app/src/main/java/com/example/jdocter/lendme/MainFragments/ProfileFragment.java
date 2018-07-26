package com.example.jdocter.lendme.MainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.CircleTransform;
import com.example.jdocter.lendme.R;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    ImageView profilePicture;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePicture= view.findViewById(R.id.ivProfileImage);
        ParseUser user=ParseUser.getCurrentUser();
        String profileUrl = null;
        try {
            profileUrl = user.fetch().getParseFile("profileImage").getUrl();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(getActivity())
                .load(profileUrl)
                .apply(new RequestOptions().transform(new CircleTransform(getActivity())))
                .into(profilePicture);
    }
}
