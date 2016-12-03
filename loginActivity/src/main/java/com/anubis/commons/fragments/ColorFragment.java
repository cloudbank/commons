package com.anubis.commons.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.ColorAdapter;
import com.anubis.commons.models.Color;
import com.anubis.commons.models.Photo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

import static android.R.attr.maxDate;


public class ColorFragment extends FlickrBaseFragment {

    List mTags;
    private List<Photo> mPhotos;

    AdView mPublisherAdView;
    ProgressDialog ringProgressDialog;
    ColorAdapter colorAdapter;
    RecyclerView rvPhotos;

    RealmChangeListener changeListener;
    Realm colorRealm, r;
    Color color;

    @Override
    public void onPause() {
        if (mPublisherAdView != null) {
            mPublisherAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mPublisherAdView != null) {
            mPublisherAdView.resume();
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("TABS", "tags activcreated");
        changeListener = new RealmChangeListener<Color>()

        {
            @Override
            public void onChange(Color r) {
                updateDisplay(r);
            }
        };


        colorRealm = Realm.getDefaultInstance();
        ringProgressDialog.setTitle("Please wait");
        ringProgressDialog.setMessage("Retrieving tags/recent photos");
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.show();
        //bring back red
        //check red button
        //onclick listeners for buttons
        //get if no list in realm AND check some time duration
        Date maxDate = colorRealm.where(Color.class).maximumDate(getString(R.string.timestamp));
        color = colorRealm.where(Color.class)
                .equalTo("color",Color.Colors.RED.name()).equalTo("timestamp", maxDate).findFirst();


        if (null == color) {
            r = Realm.getDefaultInstance();
            RealmChangeListener realmListener = new RealmChangeListener<Realm>() {
                @Override
                public void onChange(Realm r) {
                    updateDisplay();
                }
            };
            r.addChangeListener(realmListener);

        } else {
            Log.d("TAGS PRESENT", "list: " + color);
            updateDisplay(color);
            color.addChangeListener(changeListener);
            if (null != r) {
                r.removeAllChangeListeners();
                r.close();
            }
        }
        ringProgressDialog.dismiss();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotos = new ArrayList<Photo>();
        ringProgressDialog = new ProgressDialog(getActivity(), R.style.MyDialogTheme);
        colorAdapter = new ColorAdapter(getActivity(), mPhotos, false);

        Log.d("TABS", "tags oncreate");
        setRetainInstance(true);
    }


    void customLoadMoreDataFromApi(int page) {
    }


    private void updateDisplay() {
        Log.d("TABS", "tags updateDisplay");
        Color c = colorRealm.where(Color.class).equalTo("color",Color.Colors.RED.name()).equalTo("timestamp", maxDate).findFirst();
        mPhotos.clear();
        mPhotos.addAll(c.getColorPhotos());
        colorAdapter.notifyDataSetChanged();
    }

    private void updateDisplay(Color c) {
        Log.d("TABS", "tags updateDisplay(t)");
        mPhotos.clear();
        mPhotos.addAll(c.getColorPhotos());
        colorAdapter.notifyDataSetChanged();


    }




    @Override
    public void onDestroy() {
        if (mPublisherAdView != null) {
            mPublisherAdView.pause();
        }
        super.onDestroy();
        if (null != r && !r.isClosed()) {
            r.close();
        }
        if (null != colorRealm && !colorRealm.isClosed()) {
            colorRealm.close();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container,
                false);

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvPhotos);
        rvPhotos.setAdapter(colorAdapter);
        rvPhotos.setLayoutManager(new GridLayoutManager(FlickrClientApp.getAppContext(), 3));
        colorAdapter.setOnItemClickListener(new ColorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),
                        ImageDisplayActivity.class);
                Photo photo = mPhotos.get(position);
                intent.putExtra(RESULT, photo.getId());
                startActivity(intent);
            }
        });
        //button on clicks
        //get color if not in realm and store it

        mPublisherAdView = (AdView) view.findViewById(R.id.publisherAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("E0A1F5B182052F3D0E7A96A9B862BFC8")  // My Galaxy Nexus test phone
                .build();
        mPublisherAdView.loadAd(adRequest);

        setHasOptionsMenu(true);
        return view;
    }


}
