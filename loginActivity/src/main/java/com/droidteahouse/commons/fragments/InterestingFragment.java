package com.droidteahouse.commons.fragments;

import static com.droidteahouse.commons.FlickrClientApp.getJacksonService;

import android.content.Context;
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

import com.droidteahouse.commons.FlickrClientApp;
import com.droidteahouse.commons.R;
import com.droidteahouse.commons.adapter.InterestingAdapter;
import com.droidteahouse.commons.adapter.SpaceItemDecoration;
import com.droidteahouse.commons.models.Interesting;
import com.droidteahouse.commons.models.Photo;
import com.droidteahouse.commons.models.Photos;
import com.droidteahouse.commons.util.Util;

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

public class InterestingFragment extends FlickrBaseFragment {
  InterestingAdapter rAdapter;
  RecyclerView rvPhotos;
  List<Photo> mPhotos = new ArrayList<>();
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
    //@todo
    interestingRealm = Realm.getDefaultInstance();
    Date maxDate = interestingRealm.where(Interesting.class).maximumDate("timestamp");
    mInteresting = interestingRealm.where(Interesting.class).equalTo("timestamp", maxDate).findFirst();
    if (mInteresting == null) {
      //showProgress("Please wait, loading interesting data...");
      //interestingRealm.beginTransaction();
      //not in bg!
      //interestingRealm.commitTransaction();
      interestingRealm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          mInteresting = realm.createObject(Interesting.class, Calendar.getInstance().getTime().toString());
          mInteresting.setTimestamp(Calendar.getInstance().getTime());
          realm.insertOrUpdate(mInteresting);
          mInteresting.addChangeListener(changeListener);
        }
      });
      if (Util.getInterestingPage() > 1) {
        Util.resetInterestingPage();
      }
      initInterestingPhotos();
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
    rvPhotos = view.findViewById(R.id.rvPhotos);
    rvPhotos.setAdapter(rAdapter);
    StaggeredGridLayoutManager gridLayoutManager =
        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
    rvPhotos.setLayoutManager(gridLayoutManager);
    rvPhotos.addItemDecoration(new SpaceItemDecoration());
    //rvPhotos.setRecycledViewPool(pool);
    /*scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to the bottom of the list
        loadNextDataFromApi(page);
      }
    };*/
    // Adds the scroll listener to RecyclerView
    //rvPhotos.addOnScrollListener(scrollListener);
    setItemListener(rAdapter, mPhotos);
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
    if (interestingRealm != null && !interestingRealm.isClosed()) {
      interestingRealm.close();
    }
    if (null != interestingSubscription) {
      interestingSubscription.unsubscribe();
    }
  }

  public static Handler UIHandler = new Handler(Looper.getMainLooper());
  private static final Runnable sRunnable = new Runnable() {
    @Override
    public void run() {
      //dismissProgress();
    }
  };

  public void initInterestingPhotos() {
    //@todo offline mode
    //@TODO maybe iterableFLATMAP TO GET ALL PAGES
    interestingSubscription = getJacksonService().explore("1")
        .retry()
        .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
        .observeOn(Schedulers.io())
        .subscribe(new Subscriber<Photos>() {
                     @Override
                     public void onCompleted() {

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
                       Log.e("ERROR", "error init interesting photos" + e);
                     }

                     @Override
                     public void onNext(Photos p) {
                       //og.d("DEBUG", "onNext interesting: " + p.getPhotos().getPhotoList());
                       //pass photos to fragment
                       Realm realm = null;
                       try {
                         realm = Realm.getDefaultInstance();
                         //realm.beginTransaction();
                         realm.executeTransaction(new Realm.Transaction() {
                           @Override
                           public void execute(Realm realm) {
                             Date maxDate = realm.where(Interesting.class).maximumDate("timestamp");
                             Interesting interesting = realm.where(Interesting.class).equalTo("timestamp", maxDate).findFirst();
                             for (Photo photo : p.getPhotos().getPhotoList()) {
                               photo.isInteresting = true;
                               interesting.interestingPhotos.add(photo);
                             }
                             interesting.timestamp = Calendar.getInstance().getTime();
                             interesting.page = p.getPhotos().getPage();
                             realm.insertOrUpdate(interesting);
                           }
                         });
                         //realm.copyToRealmOrUpdate(interesting);  //deep copy
                         //realm.commitTransaction();
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



