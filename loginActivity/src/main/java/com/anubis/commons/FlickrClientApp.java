package com.anubis.commons;

import static com.anubis.oauthkit.BuildConfig.baseUrl;

import android.accounts.Account;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.anubis.commons.service.FlickrService;
import com.anubis.commons.service.ServiceGenerator;
import com.anubis.commons.util.Util;
import com.facebook.stetho.Stetho;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class FlickrClientApp extends MultiDexApplication {


    public static final String YELLOW = "4";
    public static final String BLUE = "8";
    public static final String ORANGE = "2";

    private static int commonsPage = 1;
    public static int interestingPage = 1;

    //@tod fix this as well--store it in realm
    public static void incrementCommonsPage() {
        synchronized (FlickrClientApp.class) {
            commonsPage  = commonsPage== null ? Util.commonsPage : commonsPage;
            commonsPage++;
            persist
        }
    }

    public static void resetCommonsPage() {
        synchronized (FlickrClientApp.class) {
            commonsPage  = commonsPage== null ? Util.commonsPage : commonsPage;
            commonsPage =1;
            persist
        }
    }
    public static int getCommonsPage() {
        synchronized (FlickrClientApp.class) {
            return commonsPage == null ? Util.commonsPage : commonsPage;
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static FlickrService jacksonService;
    // The authority for the sync adapter's content provider
    // An account type, in the form of a domain name
    // The account name
    // Instance fields
    Account mAccount;


    private static FlickrClientApp instance;

    public static FlickrClientApp getAppContext() {
        return instance;
    }


    private static OkHttpOAuthConsumer getConsumer() {
        Gson gson = new Gson();
        String json = Util.getUserPrefs().getString(FlickrClientApp.getAppContext().getString(R.string.Consumer), "");
        return gson.fromJson(json, OkHttpOAuthConsumer.class);
    }


    //@todo change docs for oauthkit with release
    public static FlickrService createJacksonService(OkHttpOAuthConsumer consumer) {
        jacksonService = ServiceGenerator.createRetrofitRxService(consumer, com.anubis.flickr.service.FlickrService.class, baseUrl, JacksonConverterFactory.create());
        return jacksonService;
    }





    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);


        //TypefaceUtil.setDefaultFont(this, "SERIF", "fonts/Exo-Medium.otf");
        Picasso.Builder builder = new Picasso.Builder(this);
        //wharton lib requires picasso 2.5.2 right now
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        //built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);

        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }


}
