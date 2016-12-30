package com.anubis.commons.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.SearchAdapter;
import com.anubis.commons.adapter.SpacesItemDecoration;
import com.anubis.commons.models.Common;
import com.anubis.commons.models.Photo;
import com.anubis.commons.models.Photos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static com.anubis.commons.FlickrClientApp.getJacksonService;


public class SearchFragment extends FlickrBaseFragment {


    RecyclerView rvPhotos;
    SearchAdapter searchAdapter;
    List<Photo> sPhotos = new ArrayList<Photo>();
    Realm commonsRealm;
    RealmChangeListener changeListener;
    Subscription commonSubscription;
    Common mCommon;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != commonSubscription) {
            commonSubscription.unsubscribe();
        }

        if (null != commonsRealm && !commonsRealm.isClosed()) {
            commonsRealm.close();
        }
       if (null != mCommon) {
           mCommon.removeChangeListeners();

       }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        searchAdapter = new SearchAdapter(FlickrClientApp.getAppContext(), sPhotos, true);

        changeListener = new RealmChangeListener<Common>() {

            @Override
            public void onChange(Common c) {

                updateDisplay(c);
            }
        };


        commonsRealm = Realm.getDefaultInstance();
        Date maxDate = commonsRealm.where(Common.class).maximumDate("timestamp");
        mCommon = commonsRealm.where(Common.class).equalTo("timestamp", maxDate).findFirst();
        if (mCommon == null) {
            showProgress("Loading data, please wait...");

            commonsRealm.beginTransaction();
            mCommon  = commonsRealm.createObject(Common.class, Calendar.getInstance().getTime().toString());
            commonsRealm.commitTransaction();
            mCommon.addChangeListener(changeListener);
            getCommonsPage1();  //<---- change
        } else {
            //<--sync adapter
            mCommon.addChangeListener(changeListener);
            updateDisplay(mCommon);
        }



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
        sPhotos.clear();
        if (null != c) {
            sPhotos.addAll(c.getCommonPhotos());
        }
        searchAdapter.notifyDataSetChanged();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvSearch);

        rvPhotos.setAdapter(searchAdapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvPhotos.setLayoutManager(gridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(15);
        rvPhotos.addItemDecoration(decoration);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),
                        ImageDisplayActivity.class);
                Photo photo = sPhotos.get(position);
                intent.putExtra(RESULT, photo.getId());
                startActivity(intent);
            }
        });


        Log.d("TABS", "search oncreateview");
        setHasOptionsMenu(true);
        return view;

    }

    private void getCommonsPage1() {

        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        commonSubscription = getJacksonService().commons("1")
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Photos>() {
                    @Override
                    public void onCompleted() {

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> dismissProgress());

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            Log.e("ERROR", String.valueOf(code));
                        }
                        Log.e("ERROR", "error getting commons1/photos" + e);
                    }

                    @Override
                    public void onNext(Photos p) {
                        Realm realm = null;
                        try {
                            realm = Realm.getDefaultInstance();
                            realm.beginTransaction();


                            Date maxDate = realm.where(Common.class).maximumDate("timestamp");
                            Common c = realm.where(Common.class).equalTo("timestamp", maxDate).findFirst();


                            for (Photo photo : p.getPhotos().getPhotoList()) {
                                photo.isCommon = true;
                                c.commonPhotos.add(photo);

                            }

                            c.timestamp = Calendar.getInstance().getTime();;

                            realm.copyToRealmOrUpdate(c);

                            realm.commitTransaction();
                        } finally {

                            if (null != realm) {
                                realm.close();
                            }
                        }


                    }
                });
    }


}


