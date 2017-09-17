package com.anubis.commons;

import static com.anubis.oauthkit.BuildConfig.baseUrl;

import com.google.gson.Gson;

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

public class FlickrClientApp extends MultiDexApplication {
  public static final String YELLOW = "4";
  public static final String BLUE = "8";
  public static final String ORANGE = "2";
  private static FlickrClientApp instance;
  private static FlickrService jacksonService;

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public static FlickrClientApp getAppContext() {
    return instance;
  }

  private static OkHttpOAuthConsumer getConsumer() {
    Gson gson = new Gson();
    String json = Util.getUserPrefs().getString(FlickrClientApp.getAppContext().getString(R.string.consumer), "");
    return gson.fromJson(json, OkHttpOAuthConsumer.class);
  }

  //@todo change docs for oauthkit with release
  public static FlickrService getJacksonService() {
    return ((jacksonService == null) ? createJacksonService(getConsumer()) : jacksonService);
  }

  //@todo change docs for oauthkit with release
  public static FlickrService createJacksonService(OkHttpOAuthConsumer consumer) {
    jacksonService = ServiceGenerator.createRetrofitRxService(consumer, com.anubis.commons.service.FlickrService.class, baseUrl, JacksonConverterFactory.create());
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
