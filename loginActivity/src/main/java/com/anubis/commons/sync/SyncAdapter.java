package com.anubis.commons.sync;

/**
 * Created by sabine on 10/6/16.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.activity.LoginActivity;
import com.anubis.commons.models.Color;
import com.anubis.commons.models.ColorPhotos;
import com.anubis.commons.models.Common;
import com.anubis.commons.models.Interesting;
import com.anubis.commons.models.Photo;
import com.anubis.commons.models.Photos;
import com.anubis.commons.util.Util;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static com.anubis.commons.FlickrClientApp.getAppContext;
import static com.anubis.commons.FlickrClientApp.getJacksonService;


/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    public static final int HOUR_IN_SECS = 60 * 60;
    public static final int SYNC_INTERVAL = 12 * HOUR_IN_SECS;    //every 12 hours
    public static final int MIN_IN_SECS = 60;
    //flex time has to be > max(5mins, 5% of interval) that would be 37 mins
    public static final int SYNC_FLEXTIME = 37 * MIN_IN_SECS;  // within 37 minutes
    private static final int DATA_NOTIFICATION_ID = 3004;
    Subscription interestingSubscription, commonsSubscription, colorSubscription;
    Color mColor;
    Interesting mInteresting;
    Common mCommon;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */

        Log.d("SYNC", "starting onPerformSync " + FlickrClientApp.getCommonsPage() );

        if (FlickrClientApp.getCommonsPage() < Common.pages) {

            FlickrClientApp.incrementCommonsPage();
            getCommonsPage(String.valueOf(FlickrClientApp.getCommonsPage()));
        }

        //getColorPhotos();  //there are not more than 500 of these and they bleed into others
        getInterestingPhotos(String.valueOf(FlickrClientApp.interestingPage));

        notifyMe();
        Log.d("SYNC", "END onPeformSync");

    }









/*

    */

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = FlickrClientApp.getAppContext().getString(R.string.authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                FlickrClientApp.getAppContext().getString(R.string.authority), bundle);
        Log.d("SYNC", "sync request");
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account

        Account newAccount = new Account(
                Util.getCurrentUser(), FlickrClientApp.getAppContext().getString(R.string.account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, empty password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "password", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            //Log.d("SYNC", "about to call onACCOUNT");
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, FlickrClientApp.getAppContext().getString(R.string.authority), true);

        /*
         * Finally, let's do a sync to get things started--
         * NOT NEEDED @todo
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void notifyMe() {

        Context context = FlickrClientApp.getAppContext();
        int iconId = R.drawable.ic_flower;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setColor(context.getResources().getColor(R.color.LithiumPool))
                        .setSmallIcon(iconId)
                        .setContentTitle("Commons photos")
                        .setContentText("500 Photos added daily.")
                        .setAutoCancel(true);


        Intent resultIntent = new Intent(context, LoginActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(DATA_NOTIFICATION_ID, mBuilder.build());


    }


    public void getInterestingPhotos(String page) {
        //@todo offline mode
        //@TODO need iterableFLATMAP TO GET ALL PAGES
        interestingSubscription = getJacksonService().explore(page)
//sync works on bg thread already
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<Photos>() {
                               @Override
                               public void onCompleted() {

                                   if (FlickrClientApp.interestingPage < Interesting.pages) {
                                       FlickrClientApp.interestingPage++;
                                   }
                                   Log.d("DEBUG", "oncompleted interesting");

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
                                   //Interesting
                                   Realm realm3 = null;
                                   try {


                                       realm3 = Realm.getDefaultInstance();
                                       //realm3.beginTransaction();


                                       realm3.executeTransaction(new Realm.Transaction() {
                                           @Override
                                           public void execute(Realm realm) {
                                               Date maxDate = realm.where(Interesting.class).maximumDate("timestamp");
                                               mInteresting = realm.where(Interesting.class).equalTo("timestamp", maxDate).findFirst();


                                               for (Photo photo : p.getPhotos().getPhotoList()) {
                                                   photo.isInteresting = true;
                                                   if (!mInteresting.interestingPhotos.contains(photo)) {
                                                       mInteresting.interestingPhotos.add(photo);
                                                   }

                                               }

                                               mInteresting.timestamp = Calendar.getInstance().getTime();
                                               mInteresting.page = p.getPhotos().getPage();
                                               realm.insertOrUpdate(mInteresting);
                                           }
                                       });

                                       //realm3.copyToRealmOrUpdate(interesting);  //deep copy
                                       //realm3.commitTransaction();
                                       Log.d("SYNC", "end get interesting: " + mInteresting);
                                   } finally {
                                       if (null != realm3) {
                                           realm3.close();
                                       }
                                   }

                               }
                           }

                );

    }


    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            //show how many of total
            Toast.makeText(getAppContext(), "You have " + String.valueOf(Common.count / Common.total) + " of Commons collection ", Toast.LENGTH_LONG);
            // FlickrBaseFragment.dismissProgress();
        }
    };

    private void getCommonsPage(String page) {

        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        commonsSubscription = getJacksonService().commons(page)
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<Photos>() {
                               @Override
                               public void onCompleted() {


                                   UIHandler.post(sRunnable);
                                   //Your UI code here
                                   //Toast.makeText(FlickrClientApp.getAppContext(), "Got our photos", Toast.LENGTH_SHORT).show();

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

                                   Realm realm4 = null;

                                   try {
                                       realm4 = Realm.getDefaultInstance();
                                       //realm4.beginTransaction();


                                       //Date maxDate = realm4.where(Common.class).maximumDate("timestamp");
                                       //c = realm4.where(Common.class).equalTo("timestamp", maxDate).findFirst();


                                       realm4.executeTransaction(new Realm.Transaction() {
                                           @Override
                                           public void execute(Realm realm) {
                                               mCommon = realm.createObject(Common.class, Calendar.getInstance().getTime().toString());


                                               for (Photo photo : p.getPhotos().getPhotoList()) {
                                                   photo.isCommon = true;
                                                   if (!mCommon.commonPhotos.contains(photo)) {
                                                       mCommon.commonPhotos.add(photo);
                                                   }

                                               }
                                               Log.d("SYNC", "commonsPhotos, page: " + page);

                                               mCommon.timestamp = Calendar.getInstance().getTime();
                                               mCommon.page = p.getPhotos().getPage();
                                               realm.insertOrUpdate(mCommon);
                                           }
                                       });
                                       //realm4.copyToRealmOrUpdate(c);

                                       //realm4.commitTransaction();
                                   } finally

                                   {
                                       if (null != realm4) {
                                           realm4.close();
                                       }
                                   }


                               }
                           }

                );

    }


//there are fewer than 500 color photos for most colors
    //thereby we don't need updates for this


    private void getColorPhotos() {
        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        Observable<String> getIdFromList = Util.getIds().concatMapIterable(ids -> ids);
        colorSubscription = getIdFromList
                .concatMap(Util::setColorId)
                .zipWith(getIdFromList, (Photos p, String s) -> new ColorPhotos(s, p))
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.immediate())
                //@todo change type to ColorListGroup
                .subscribe(new Subscriber<ColorPhotos>() {

                               @Override
                               public void onCompleted() {
                                    /*
                                   Handler handler = new Handler(Looper.getMainLooper());
                                   handler.post(() -> {

                                   });
*/
                               }


                               @Override
                               public void onError(Throwable e) {
                                   // cast to retrofit.HttpException to get the response code
                                   if (e instanceof HttpException) {
                                       HttpException response = (HttpException) e;
                                       int code = response.code();
                                       Log.e("ERROR", String.valueOf(code));
                                   }
                                   Log.e("ERROR", "error getting friends" + e);
                                   //signout

                               }


                               @Override
                               public void onNext(ColorPhotos cp) {

                                   Log.d("SYNC", "Color Photos&&&&&&");
                                   Realm mRealm = null;

                                   try {
                                       mRealm = Realm.getDefaultInstance();
                                       //mRealm.beginTransaction();

                                       mRealm.executeTransaction(new Realm.Transaction() {
                                           @Override
                                           public void execute(Realm realm) {
                                               mColor = realm.where(Color.class).equalTo("color", cp.getCode()).findFirst();
                                               if (null == mColor) {
                                                   mColor = realm.createObject(Color.class, cp.getCode() + Calendar.getInstance().getTime().toString());
                                               }

                                               mColor.timestamp = Calendar.getInstance().getTime();
                                               mColor.color = cp.getCode();

                                               for (Photo photo : cp.getP().getPhotos().getPhotoList()) {
                                                   photo.isCommon = true;
                                                   if (!mColor.colorPhotos.contains(photo)) {
                                                       mColor.colorPhotos.add(photo);
                                                   }


                                               }
                                               realm.insertOrUpdate(mColor);
                                           }
                                       });
                                       // c.setColor(color);
                                       //c.timestamp = Calendar.getInstance().getTime();

                                       //mRealm.copyToRealmOrUpdate(c);


                                       //mRealm.commitTransaction();


                                   } finally {
                                       if (null != mRealm) {
                                           mRealm.close();
                                       }
                                   }


                               }
                           }

                );

    }


}






