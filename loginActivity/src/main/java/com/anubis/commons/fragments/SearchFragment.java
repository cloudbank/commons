package com.anubis.commons.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.adapter.SearchAdapter;
import com.anubis.commons.listener.EndlessRecyclerViewScrollListener;
import com.anubis.commons.models.Common;
import com.anubis.commons.models.Photo;
import com.anubis.commons.models.Photos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static com.anubis.commons.FlickrClientApp.getJacksonService;


public class SearchFragment extends FlickrBaseFragment  {


    RecyclerView rvPhotos;
    SearchAdapter searchAdapter;
    List<Photo> sPhotos = new ArrayList<>();
    Realm commonsRealm;
    RealmChangeListener changeListener;
    Subscription commonSubscription;
    Common mCommon;
    private EndlessRecyclerViewScrollListener scrollListener;



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commonsRealm != null && !commonsRealm.isClosed()) {
            commonsRealm.close();
        }
        if (null != commonSubscription) {
            commonSubscription.unsubscribe();
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchAdapter = new SearchAdapter(FlickrClientApp.getAppContext(), sPhotos, true, isTwoPane);

        changeListener = new RealmChangeListener<Common>() {

            @Override
            public void onChange(Common c) {

                updateDisplay(c);
            }
        };


        commonsRealm = Realm.getDefaultInstance();
        mCommon = commonsRealm.where(Common.class).equalTo("page", 1).findFirst();
        if (mCommon == null) {
            showProgress("Loading photo data, please wait...");
            //reset because there is nothing in realm
            //@todo if db gets erased and SA is running already
            // commonsRealm.beginTransaction();

            //execute transaction
            commonsRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mCommon = realm.createObject(Common.class, Calendar.getInstance().getTime().toString());
                    mCommon.page = 1;

                    realm.insertOrUpdate(mCommon);
                    mCommon.addChangeListener(changeListener);
                    //@todo

                }
            });
            //commonsRealm.commitTransaction();
            if (FlickrClientApp.getCommonsPage() > 1) {
                FlickrClientApp.resetCommonsPage();
            }

            initCommonsPage("1");  //

        } else {
            mCommon.addChangeListener(changeListener);
            updateDisplay(mCommon);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("TABS", "search onstart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TABS", "search onresume");


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TABS", "search activcreated");

        //do in bg to avoid blocking UI
        //Date maxDate = commonsRealm.where(Common.class).maximumDate("timestamp");
        // mCommon = commonsRealm.where(Common.class).equalTo("timestamp", maxDate).findFirst();

        //updateDisplay(mCommon);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TABS", "search oncreate");
        setRetainInstance(true);


    }


    private void updateDisplay(Common c) {
        Log.d("TABS", "search updateDisplay(s)");
        //sPhotos.clear();
        if (null != c) {
            sPhotos.addAll(c.getCommonPhotos());
        }
        Log.d("TABS", "ADDED to updateDisplay(s): " + sPhotos.size());
        searchAdapter.notifyItemRangeChanged(0, c.getCommonPhotos().size() - 1);
        searchAdapter.notifyDataSetChanged();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvSearch);
        searchAdapter.setmAnimator(rvPhotos);


        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        rvPhotos.setLayoutManager(gridLayoutManager);
        //animationAdapter.setFirstOnly(true);
        rvPhotos.setAdapter(searchAdapter);
       /*
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {


            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPhotos.addOnScrollListener(scrollListener);
        */

        setItemListener(searchAdapter, sPhotos);



        Log.d("TABS", "search oncreateview");
        setHasOptionsMenu(true);
        //showProgress("");


        return view;

    }


    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            //dismissProgress();
        }
    };

    private void initCommonsPage(String page) {

        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        commonSubscription = getJacksonService().commons(page)
                .retry()
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Photos>() {
                    @Override
                    public void onCompleted() {
                        Log.d("end INIT", "init commons1/photos");
                        //UIHandler.post(sRunnable);

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            Log.e("ERROR", String.valueOf(code));
                        }
                        Log.e("ERROR", "error init commons1/photos" + e);
                    }

                    @Override
                    public void onNext(Photos p) {
                        Common.pages = p.getPhotos().getPages();
                        Common.total = p.getPhotos().getTotal();
                        Log.d("begin INIT", "init commons1/photos");
                        Realm realm = null;
                        try {

                            realm = Realm.getDefaultInstance();
                            //realm.beginTransaction();


                            //realm.copyToRealmOrUpdate(c);


                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    /*
                                    Insert or update an unmanaged RealmObject. This is generally faster than {@link #copyToRealmOrUpdate(RealmModel)} since it
                                    * doesn't return the inserted elements, and performs minimum allocations and checks.
                                     * After being inserted any changes to the original object will not be persisted.
                                     */
                                    //Date maxDate = realm.where(Common.class).maximumDate("timestamp");
                                    Common c = realm.where(Common.class).equalTo("page", 1).findFirst();
                                    //if null.... recreate object although unlikely

                                    for (Photo photo : p.getPhotos().getPhotoList()) {
                                        photo.isCommon = true;
                                        c.commonPhotos.add(photo);

                                    }

                                    c.timestamp = Calendar.getInstance().getTime();
                                    c.page = p.getPhotos().getPage();
                                    realm.insertOrUpdate(c);
                                }
                            });


                        } finally {

                            if (null != realm) {
                                realm.close();
                            }
                        }


                    }
                });
    }


}


