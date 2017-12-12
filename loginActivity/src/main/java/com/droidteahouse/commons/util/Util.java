package com.droidteahouse.commons.util;

import static com.droidteahouse.commons.FlickrClientApp.getJacksonService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.droidteahouse.commons.FlickrClientApp;
import com.droidteahouse.commons.R;
import com.droidteahouse.commons.models.Photos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import rx.Observable;

/**
 * Created by sabine on 9/21/16.
 */
public class Util {
  public static Observable<Photos> setColorId(String ids) {
    HashMap<String, String> data = new HashMap<>();
    data.put("text", "Search The Commons");
    data.put("is_commons", "true");
    data.put("sort", "relevance");
    //data.put("page", "1");
    data.put("color_codes", ids);//
    return getJacksonService().bycolor(data);
  }

  public static Observable<List<String>> getIds() {
    return Observable.just(Arrays.asList(FlickrClientApp.YELLOW, FlickrClientApp.BLUE, FlickrClientApp.ORANGE));
  }

  public static String getProperty(String key, Context context) throws IOException {
    Properties properties = new Properties();
    AssetManager assetManager = context.getAssets();
    InputStream inputStream = assetManager.open("config.properties");
    properties.load(inputStream);
    return properties.getProperty(key);
  }

  public static boolean isInit() {
    return !(getUserPrefs().contains(FlickrClientApp.getAppContext().getResources().getString(R.string.current_user)));
  }

  public static boolean isNewUser(String username) {
    return !getUserPrefs().getString(FlickrClientApp.getAppContext().getResources().getString(R.string.current_user), "").equals(username);
  }

  public static String getCurrentUser() {
    SharedPreferences prefs = Util.getUserPrefs();
    return prefs.getString(FlickrClientApp.getAppContext().getResources().getString(R.string.current_user), "");
  }

  public static String getUserId() {
    SharedPreferences prefs = Util.getUserPrefs();
    return prefs.getString(FlickrClientApp.getAppContext().getResources().getString(R.string.user_id), "");
  }

  public static SharedPreferences getUserPrefs() {
    return FlickrClientApp.getAppContext().getSharedPreferences(FlickrClientApp.getAppContext().getResources().getString(R.string.OAuthKit_Prefs), 0);
  }

  public static void saveCommonsPage(int commonsPage) {
    getUserPrefs().edit().putInt("commonsPage", commonsPage).commit();
  }

  public static int getCommonsPage() {
    return getUserPrefs().getInt("commonsPage", 2);
  }

  public static void resetCommonsPage() {
    synchronized (Util.class) {
      Util.saveCommonsPage(1);
    }
  }
//@todo synchronize corrections
  public static void saveInterestingPage(int intPage) {
    getUserPrefs().edit().putInt("interestingPage", intPage).commit();
  }

  public static int getInterestingPage() {
    return getUserPrefs().getInt("interestingPage", 2);
  }

  public static void resetInterestingPage() {
    synchronized (Util.class) {
      Util.saveInterestingPage(1);
    }
  }

  //not Bluetooth or ethernet
  public static boolean isNetworkAvailable(Context ctx) {
    ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager
        .getActiveNetworkInfo();
    return activeNetworkInfo != null
        && activeNetworkInfo.isConnectedOrConnecting() && (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
        || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
  }

}
