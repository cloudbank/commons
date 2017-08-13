package com.anubis.commons.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.adapter.ColorAdapter;
import com.anubis.commons.models.Color;
import com.anubis.commons.models.ColorPhotos;
import com.anubis.commons.models.Photo;
import com.anubis.commons.models.Photos;
import com.anubis.commons.util.Util;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;


public class ColorFragment extends FlickrBaseFragment {

    List mTags;
    private List<Photo> mPhotos = new ArrayList<>();
    Subscription colorSubscription;
    AdView mPublisherAdView;
    com.aurelhubert.ahbottomnavigation.AHBottomNavigation bottomNavigation;

    ColorAdapter colorAdapter;
    RecyclerView rvPhotos;

    RealmChangeListener changeListener;
    Realm colorRealm;
    Color mColor;
    String[] colors = new String[3];
    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private Spring mScaleSpring;


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
    public void onAttach(Context context) {
        super.onAttach(context);


        colorAdapter = new ColorAdapter(getActivity(), mPhotos, false);


        changeListener = new RealmChangeListener<Color>()

        {
            @Override
            public void onChange(Color r) {

                updateDisplay(r);

            }
        };
        colorRealm = Realm.getDefaultInstance();
        mColor = colorRealm.where(Color.class).equalTo(getString(R.string.color), FlickrClientApp.YELLOW).findFirst();
        //init or realm has been deleted
        if ( mColor == null ) {
            //to add the listener an object query has to return something
            //this is primarily to add the listener--just create an empty obj w listener
            //so it is there when data comes back
            //colorRealm.beginTransaction();
            mColor = colorRealm.createObject(Color.class, Calendar.getInstance().getTime().toString());
            //not in bg!
            mColor.color = FlickrClientApp.YELLOW;
            colorRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(mColor);
                }
            });
            //colorRealm.commitTransaction();

            mColor.addChangeListener(changeListener);
            //last color?
            getColors();


        } else {

            mColor.addChangeListener(changeListener);
            updateDisplay(mColor);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TABS", "tags oncreate");

        colors[0] = FlickrClientApp.YELLOW;
        colors[1] = FlickrClientApp.BLUE;
        colors[2] = FlickrClientApp.ORANGE;

        setRetainInstance(true);

    }

    private void setupBottomNav(com.aurelhubert.ahbottomnavigation.AHBottomNavigation bottomNavigation) {
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getActivity().getString(R.string.yellow), R.drawable.ic_invert_colors_black_24dp, fetchColor(R.color.sand));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getActivity().getString(R.string.blue), R.drawable.ic_invert_colors_black_24dp, fetchColor(android.R.color.holo_blue_dark));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getActivity().getString(R.string.orange), R.drawable.ic_invert_colors_black_24dp, fetchColor(android.R.color.holo_orange_dark));

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.LithiumPool));

        // Change colors
        bottomNavigation.setAccentColor(fetchColor(R.color.sand));
        bottomNavigation.setInactiveColor(fetchColor(android.R.color.darker_gray));


        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);
        bottomNavigation.setBehaviorTranslationEnabled(true);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);


        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                if (position == 1) {
                    bottomNavigation.setAccentColor(fetchColor(android.R.color.holo_blue_dark));

                } else if (position == 2) {
                    bottomNavigation.setAccentColor(fetchColor(android.R.color.holo_orange_dark));
                } else {
                    bottomNavigation.setAccentColor(fetchColor(R.color.sand));
                }
                getPhotos(position);
                //return true;
            }
        });


    }


    private void updateDisplay(Color c) {
        Log.d("TABS", "tags updateDisplay(t)");
        mPhotos.clear();
        if (null != c) {
            mPhotos.addAll(c.getColorPhotos());
        }
        colorAdapter.notifyDataSetChanged();


    }





    @Override
    public void onDestroy() {
        if (mPublisherAdView != null) {
            mPublisherAdView.pause();
        }
        super.onDestroy();
        if (null != mColor) {
            mColor.removeChangeListeners();
        }

        if (null != colorRealm && !colorRealm.isClosed()) {
            colorRealm.close();
        }


    }


    private void getPhotos(int position) {
        //from bottom nav positions
        final String color = colors[position];
        Realm realm2 = null;

        try {
            realm2 = Realm.getDefaultInstance();

            Color colorObj = realm2.where(Color.class)
                    .equalTo(getResources().getString(R.string.color), color).findFirst();

            if (null != colorObj) {
                mPhotos.clear();
                mPhotos.addAll(colorObj.getColorPhotos());
                colorAdapter.notifyDataSetChanged();
            } else {
                Log.d("COLOR", "color not in db" + color);
                //getColors(color);
                //udpateDisplay--the listener should be attached already

            }
        } finally {
            if (realm2 != null) {
                realm2.close();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container,
                false);


        rvPhotos = (RecyclerView) view.findViewById(R.id.rvPhotos);
        //colorAdapter.setmAnimator(rvPhotos);
        rvPhotos.setAdapter(colorAdapter);

        rvPhotos.setLayoutManager(new GridLayoutManager(FlickrClientApp.getAppContext(), 3));

        bottomNavigation = (AHBottomNavigation) view.findViewById(R.id.bottom_navigation);
        setupBottomNav(bottomNavigation);
        //button on clicks
        //get mColor if not in mRealm and store it

        mPublisherAdView = (AdView) view.findViewById(R.id.publisherAdView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                //.addTestDevice("39EA0A51CB1E58F7B6CFC094BD01CA18")  // My Galaxy Nexus test phone
                .build();
        mPublisherAdView.loadAd(adRequest);

        setItemListener(colorAdapter, mPhotos);
        setHasOptionsMenu(true);
        return view;
    }




    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            dismissProgress();
        }
    };

    private void getColors() {
        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        Observable<String> getIdFromList = Util.getIds().concatMapIterable(ids -> ids);
        colorSubscription = getIdFromList
                .concatMap(Util::setColorId)
                .zipWith(getIdFromList, (Photos p, String s) -> new ColorPhotos(s, p))
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.io())
                //@todo change type to ColorListGroup
                .subscribe(new Subscriber<ColorPhotos>() {

                               @Override
                               public void onCompleted() {

                                   UIHandler.post(sRunnable);

                               }


                               @Override
                               public void onError(Throwable e) {
                                   // cast to retrofit.HttpException to get the response code
                                   if (e instanceof HttpException) {
                                       HttpException response = (HttpException) e;
                                       int code = response.code();
                                       Log.e("ERROR", String.valueOf(code));
                                   }
                                   Log.e("ERROR", "error getting color" + e);
                                   //signout
                                   //UIHandler.post(sRunnable);

                               }


                               @Override
                               public void onNext(ColorPhotos cp) {

                                   Realm mRealm = null;

                                   try {
                                       mRealm = Realm.getDefaultInstance();
                                      // mRealm.beginTransaction();

                                       if (cp.getCode().equals(FlickrClientApp.YELLOW)) {
                                           //init obj
                                           mColor= mRealm.where(Color.class).equalTo(getResources().getString(R.string.color), FlickrClientApp.YELLOW).findFirst();
                                       } else {
                                           mColor = mRealm.createObject(Color.class, cp.getCode() + Calendar.getInstance().getTime().toString());
                                       }

                                       mColor.timestamp = Calendar.getInstance().getTime();
                                       mColor.color = cp.getCode();

                                       for (Photo photo : cp.getP().getPhotos().getPhotoList()) {
                                           photo.isCommon = true;
                                           mColor.colorPhotos.add(photo);


                                       }
                                       mRealm.executeTransaction(new Realm.Transaction() {
                                           @Override
                                           public void execute(Realm realm) {
                                               realm.insertOrUpdate(mColor);
                                           }
                                       });
                                       // c.setColor(color);
                                       //c.timestamp = Calendar.getInstance().getTime();

                                       //mRealm.copyToRealmOrUpdate(c);


                                      // mRealm.commitTransaction();


                                   } finally {
                                       if (null != mRealm) {
                                           mRealm.close();
                                       }
                                   }


                               }
                           }

                );

    }




    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }
}
