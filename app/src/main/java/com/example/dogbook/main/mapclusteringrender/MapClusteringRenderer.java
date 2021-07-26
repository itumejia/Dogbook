package com.example.dogbook.main.mapclusteringrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dogbook.main.models.Post;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.parse.Parse.getApplicationContext;

public class MapClusteringRenderer extends DefaultClusterRenderer<Post> {

    private static final int MARKER_ICON_SIZE = 150;

    public MapClusteringRenderer(Context context, GoogleMap map, ClusterManager<Post> clusterManager) {
        super(context, map, clusterManager);
    }


    @Override
    protected void onClusterItemRendered(@NonNull Post clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        ParseFile postPicture = clusterItem.getPhoto();
        if (postPicture != null) {
            Glide.with(getApplicationContext()).asBitmap().load(postPicture.getUrl())
                    .apply(new RequestOptions().override(MARKER_ICON_SIZE,MARKER_ICON_SIZE))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });
        }
    }


}
