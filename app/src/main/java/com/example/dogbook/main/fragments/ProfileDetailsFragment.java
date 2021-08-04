package com.example.dogbook.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogbook.R;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ProfileDetailsFragment extends Fragment {

    private static final String TAG = "ProfileDetailsFragment";

    private User user;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private TextView tvBreed;

    public ProfileDetailsFragment() {
        // Required empty public constructor
    }

    public static ProfileDetailsFragment newInstance(User user) {
        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(User.class.getSimpleName(), Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = Parcels.unwrap(getArguments().getParcelable(User.class.getSimpleName()));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvOwner = view.findViewById(R.id.tvOwner);
        tvBreed = view.findViewById(R.id.tvBreed);

        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseFile profilePicture = user.getProfilePicture();
                    if (profilePicture != null) {
                        Glide.with(getContext()).load(profilePicture.getUrl()).into(ivProfilePicture);
                    }

                    tvUsername.setText(user.getUsername());
                    tvOwner.setText(user.getOwnerName());
                    tvBreed.setText(user.getBreed());
                    return;
                }
                Toast.makeText(getContext(), "Could not get user info", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Issue fetching user data", e);
            }
        });
    }
}