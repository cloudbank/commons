package com.anubis.commons.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.InterestingAdapter;
import com.anubis.commons.models.Interesting;
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

public class InterestingFragment extends FlickrBaseFragment {
    InterestingAdapter rAdapter;
    RecyclerView rvPhotos;
    List<Photo> mPhotos = new ArrayList<Photo>();
    RealmChangeListener changeListener;
    Realm interestingRealm;
    Subscription interestingSubscription;
    Interesting mInteresting;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TABS", "interesting onresume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        rAdapter = new InterestingAdapter(FlickrClientApp.getAppContext(), mPhotos, true);

        changeListener = new RealmChangeListener<Interesting>() {
            @Override
            public void onChange(Interesting i) {
                updateDisplay(i);
            }
        };


        interestingRealm = Realm.getDefaultInstance();
        Date maxDate = interestingRealm.where(Interesting.class).maximumDate("timestamp");
        //@todo get the last selected color?
        mInteresting = interestingRealm.where(Interesting.class).equalTo("timestamp", maxDate).findFirst();
        if (mInteresting == null) {
            showProgress("Please wait, loading interesting data...");
            interestingRealm.beginTransaction();
            mInteresting = interestingRealm.createObject(Interesting.class, Calendar.getInstance().getTime().toString());
            //not in bg!
            interestingRealm.commitTransaction();
            mInteresting.addChangeListener(changeListener);
            getInterestingPhotos();

        } else {
            mInteresting.addChangeListener(changeListener);
            updateDisplay(mInteresting);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TABS", "interesting activcreated");



    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("TABS", "interesting oncreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interesting, container,
                false);
        rvPhotos = (RecyclerView) view.findViewById(R.id.rvPhotos);
        rvPhotos.setAdapter(rAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvPhotos.setLayoutManager(gridLayoutManager);

        rAdapter.setOnItemClickListener(new InterestingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),
                        ImageDisplayActivity.class);
                Photo photo = mPhotos.get(position);

                intent.putExtra(RESULT, photo.getId());
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        return view;
    }


    public void updateDisplay(Interesting i) {
        Log.d("TABS", "mPhotos updateDisplay(i)");
        mPhotos.clear();
        if (null != i) {
            mPhotos.addAll(i.getInterestingPhotos());
        }
        rAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mInteresting ) {
            mInteresting.removeChangeListeners();
        }
        if (null != interestingRealm && !interestingRealm.isClosed()) {
            interestingRealm.close();
        }

        if (null != interestingSubscription) {
            interestingSubscription.unsubscribe();
        }
    }


    public void getInterestingPhotos() {
        //@todo offline mode
        //@TODO need iterableFLATMAP TO GET ALL PAGES
        interestingSubscription = getJacksonService().explore("1")

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
                                   Log.e("ERROR", "error getting interesting photos" + e);
                               }

                               @Override
                               public void onNext(Photos p) {
                                   //og.d("DEBUG", "onNext interesting: " + p.getPhotos().getPhotoList());
                                   //pass photos to fragment
                                   Realm realm = null;
                                   try {


                                       realm = Realm.getDefaultInstance();
                                       realm.beginTransaction();

                                       Date maxDate = realm.where(Interesting.class).maximumDate("timestamp");
                                       Interesting interesting = realm.where(Interesting.class).equalTo("timestamp", maxDate).findFirst();


                                       for (Photo photo : p.getPhotos().getPhotoList()) {
                                           photo.isInteresting = true;
                                           interesting.interestingPhotos.add(photo);

                                       }

                                       interesting.timestamp = Calendar.getInstance().getTime();
                                       ;

                                       realm.copyToRealmOrUpdate(interesting);  //deep copy
                                       realm.commitTransaction();
                                   } finally {
                                       if (null != realm) {
                                           realm.close();
                                       }
                                   }

                               }
                           }

                );

    }

}



