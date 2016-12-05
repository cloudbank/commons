package com.anubis.commons.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.ColorAdapter;
import com.anubis.commons.models.Color;
import com.anubis.commons.models.Photo;
import com.anubis.commons.models.Photos;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static com.anubis.commons.FlickrClientApp.getJacksonService;
import static com.anubis.commons.R.id.blue;


public class ColorFragment extends FlickrBaseFragment {

    List mTags;
    private List<Photo> mPhotos;
    Subscription colorSubscription, colorSubscription2;
    AdView mPublisherAdView;
    ProgressDialog ringProgressDialog;
    ColorAdapter colorAdapter;
    RecyclerView rvPhotos;

    RealmChangeListener changeListener;
    Realm colorRealm;
    Color mColor;
    HashMap<Integer, String> colors = new HashMap<>();


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
        ringProgressDialog = new ProgressDialog(getActivity(), R.style.MyDialogTheme);
        changeListener = new RealmChangeListener<Color>()

        {
            @Override
            public void onChange(Color r) {
                updateDisplay(r);
            }
        };

        colorRealm = Realm.getDefaultInstance();
        Date maxDate = colorRealm.where(Color.class).maximumDate("timestamp");
        //@todo get the last selected color?
        mColor = colorRealm.where(Color.class).equalTo("timestamp", maxDate).equalTo("color","0").findFirst();
        if (mColor == null) {
            colorRealm.beginTransaction();
            mColor = colorRealm.createObject(Color.class, Calendar.getInstance().getTime().toString());
            //not in bg!
            mColor.timestamp = Calendar.getInstance().getTime();
            colorRealm.commitTransaction();
            mColor.addChangeListener(changeListener);
            //last color?
            getColor("0");

        } else {
            mColor.addChangeListener(changeListener); //<--sync adapter
            updateDisplay(mColor);
        }
        ringProgressDialog.setTitle("Please wait");
        ringProgressDialog.setMessage("Retrieving photos");
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("TABS", "tags activcreated");



        ringProgressDialog.dismiss();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotos = new ArrayList<Photo>();
        colorAdapter = new ColorAdapter(getActivity(), mPhotos, false);

        Log.d("TABS", "tags oncreate");
        colors.put(blue, "8");
        colors.put(R.id.red, "0");
        colors.put(R.id.yellow, "4");
        colors.put(R.id.violet, "9");

        setRetainInstance(true);
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

        if (null != colorRealm && !colorRealm.isClosed()) {
            colorRealm.close();
        }
        
    }


    private void getPhotos(View v) {
        //from onclick buttons
        final String color = colors.get(v.getId());
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Color colorObj = realm.where(Color.class)
                .equalTo("color", color).findFirst();
        realm.close();
        if (null != colorObj) {
            mPhotos.clear();
            mPhotos.addAll(colorObj.getColorPhotos());
            colorAdapter.notifyDataSetChanged();
        } else {
            getColor(color);
            //udpateDisplay--the listener should be attached already

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
        //get mColor if not in mRealm and store it

        mPublisherAdView = (AdView) view.findViewById(R.id.publisherAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("E0A1F5B182052F3D0E7A96A9B862BFC8")  // My Galaxy Nexus test phone
                .build();
        mPublisherAdView.loadAd(adRequest);

        final Button red = (Button) view.findViewById(R.id.red);
        final Button violet = (Button) view.findViewById(R.id.violet);
        final Button yellow = (Button) view.findViewById(R.id.yellow);
        final Button blue = (Button) view.findViewById(R.id.blue);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get from mRealm, if null or stale
                //get from network once per 48 hrs;
                red.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                yellow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                blue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                violet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                getPhotos(v);
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get from mRealm, if null or stale
                //get from network once per 48 hrs;
                yellow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                red.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                blue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                violet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                getPhotos(v);
            }
        });


        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                red.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                yellow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                violet.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                //get from mRealm, if null or stale
                //get from network once per 48 hrs;
                getPhotos(v);
            }
        });


        violet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                violet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                red.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                blue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                yellow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                //get from mRealm, if null or stale
                //get from network once per 48 hrs;
                getPhotos(v);
            }
        });


        setHasOptionsMenu(true);
        return view;
    }


    private void getColor(String color) {

        //@todo check for page total if not then process with page 1
        //@todo while realm total is less than total increment page else stop
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "1");
        data.put("color_codes", color);  //start w red
        colorSubscription = getJacksonService().bycolor(data)
                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Photos>() {
                    @Override
                    public void onCompleted() {
                        //update total/page for next sync

                        //Log.d("DEBUG","oncompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            Log.e("ERROR", String.valueOf(code));
                        }
                        Log.e("ERROR", "error getting mColor/photos" + e);
                    }

                    @Override
                    public void onNext(Photos p) {
                        Realm mRealm = null;

                        try {
                            mRealm = Realm.getDefaultInstance();
                            mRealm.beginTransaction();


                            Date maxDate = colorRealm.where(Color.class).maximumDate("timestamp");
                            Color c = colorRealm.where(Color.class).equalTo("timestamp", maxDate).equalTo("color","0").findFirst();

                            for (Photo photo : p.getPhotos().getPhotoList()) {
                                photo.isCommon = true;
                                c.colorPhotos.add(photo);


                            }
                            c.setColor("0");
                            c.timestamp = Calendar.getInstance().getTime();;
                            mRealm.copyToRealmOrUpdate(c);


                            mRealm.commitTransaction();
                        } finally {
                            if (null != mRealm) {
                                mRealm.close();
                            }
                        }


                    }
                });

    }


}
