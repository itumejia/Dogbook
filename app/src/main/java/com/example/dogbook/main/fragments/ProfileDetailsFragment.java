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
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.dogbook.R;
import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.data.Reactions;
import com.example.dogbook.main.models.Follow;
import com.example.dogbook.main.models.Post;
import com.example.dogbook.main.models.User;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ProfileDetailsFragment extends Fragment {

    private static final String TAG = "ProfileDetailsFragment";
    public static final int ROUNDED_CORNERS_RADIUS = 50;

    private User user;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private TextView tvBreed;
    private ToggleButton btnFollow;

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
        btnFollow = view.findViewById(R.id.btnFollow);

        //Populate the user data
        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseFile profilePicture = user.getProfilePicture();
                    if (profilePicture != null) {
                        Glide.with(getContext())
                                .load(profilePicture.getUrl())
                                .centerCrop()
                                .transform(new RoundedCorners(ROUNDED_CORNERS_RADIUS))
                                .into(ivProfilePicture);
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

        //Populate follow button
        if (!user.hasSameId(ParseUser.getCurrentUser())) {
            ParseQuery<Follow> query = ParseApplication.getFollowFromUserToUser(ParseUser.getCurrentUser(), user);
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        btnFollow.setChecked(count > 0); //Set btn to ON if the logged user follows the selected user
                        btnFollow.setVisibility(View.VISIBLE);
                        return;
                    }
                    Toast.makeText(getContext(), "Could not load user information", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue fetching 'follow' information", e);
                }
            });
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The user will follow
                if(btnFollow.isChecked()) {
                    Reactions.follow(user, getContext(), new Reactions.UserFollowCallback() {
                        @Override
                        public void onOptimisticUpdate() {}

                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onFailure() {
                            btnFollow.setChecked(false);
                        }
                    });
                    return;
                }

                //The user will unfollow
                Reactions.unfollow(user, getContext(), new Reactions.UserFollowCallback() {
                    @Override
                    public void onOptimisticUpdate() {}

                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onFailure() {
                        btnFollow.setChecked(true);
                    }
                });
            }
        });

    }
}