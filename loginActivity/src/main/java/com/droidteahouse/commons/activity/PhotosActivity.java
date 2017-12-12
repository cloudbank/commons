package com.droidteahouse.commons.activity;

import static com.droidteahouse.commons.FlickrClientApp.getAppContext;

import com.google.android.gms.ads.MobileAds;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.droidteahouse.commons.FlickrClientApp;
import com.droidteahouse.commons.R;
import com.droidteahouse.commons.fragments.ColorFragment;
import com.droidteahouse.commons.fragments.FlickrBaseFragment;
import com.droidteahouse.commons.fragments.InterestingFragment;
import com.droidteahouse.commons.fragments.SearchFragment;
import com.droidteahouse.commons.sync.SyncAdapter;
import com.droidteahouse.commons.util.Util;
import com.droidteahouse.oauthkit.OAuthBaseClient;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {
  //https://stackoverflow.com/questions/36867298/using-android-vector-drawables-on-pre-lollipop-crash
  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  private MyPagerAdapter adapterViewPager;
  private ViewPager vpPager;
  protected SharedPreferences prefs;
  protected SharedPreferences.Editor editor;
  View rootView;
  private static final int GET_ACCOUNTS_PERMISSIONS_REQUEST = 1;
  private boolean mDownloading = false;
  SharedPreferences authPrefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_photos);
    FrameLayout fragmentItemDetail = (FrameLayout) findViewById(R.id.flDetailContainer);
    FlickrBaseFragment.setPane(fragmentItemDetail != null);
    rootView = findViewById(android.R.id.content);
    authPrefs = getApplicationContext().getSharedPreferences(getString(R.string.OAuthKit_Prefs), 0);
    //@todo persist user, sync across realm
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setHomeButtonEnabled(true);
    toolbar.inflateMenu(R.menu.photos);
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    tabLayout.addTab(tabLayout.newTab().setText(R.string.commons_search));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.interesting_today));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.tags));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), intializeItems());
    vpPager = (ViewPager) findViewById(R.id.vpPager);
    vpPager.setOffscreenPageLimit(3);
    vpPager.setAdapter(adapterViewPager);
    vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.addOnTabSelectedListener(onTabSelectedListener(vpPager));
    MobileAds.initialize(getApplicationContext(), "ca-app-pub-8660045387738182~8507434555");
    initSyncAdapter();
    updateUserInfo(authPrefs);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.photos, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_logout) {
      signOut();
    }
    return super.onOptionsItemSelected(item);
  }

  private void initSyncAdapter() {
    if (Util.getCurrentUser().length() > 0) {
      Account acct = SyncAdapter.getSyncAccount(Util.getCurrentUser(), getApplicationContext());
      if (!Util.getCurrentUser().equals(authPrefs.getString(getString(R.string.username), ""))) {
        Log.d("SYNC", "changing accounts for sync adapter");
        ContentResolver.cancelSync(acct, getApplication().getString(R.string.authority));
        ((AccountManager) getApplicationContext().getSystemService(Context.ACCOUNT_SERVICE)).removeAccount(acct, new AccountManagerCallback<Boolean>() {
          @Override
          public void run(AccountManagerFuture<Boolean> future) {
            updateUserInfo(authPrefs);
            SyncAdapter.startSyncAdapter(authPrefs.getString(getString(R.string.username), ""));
          }
        }, new Handler());
        //someone deleted their SA
      } else if (acct == null) {   ///
        SyncAdapter.startSyncAdapter(Util.getCurrentUser());
        //they turned off the individual sync
      }
      if (!ContentResolver.getSyncAutomatically(acct, FlickrClientApp.getAppContext().getString(R.string.authority))) {
        //turn the thing back on
        //Toast
        ContentResolver.setSyncAutomatically(acct, FlickrClientApp.getAppContext().getString(R.string.authority), true);
      }
    }
  }

  public void signOut() {
    OAuthBaseClient.getInstance(getApplicationContext(), null).clearTokens();
    Intent bye = new Intent(this, LoginActivity.class);
    startActivity(bye);
  }

  @TargetApi(Build.VERSION_CODES.M)
  private boolean checkAccountsPermission() {
    final String perm = Manifest.permission.GET_ACCOUNTS;
    int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
      // We have the permission
      return true;
    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
      // Need to show permission rationale, display a snackbar and then request
      // the permission again when the snackbar is dismissed to re-request it
      Snackbar.make(rootView, R.string.permission_grant, Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", v -> {
            // Request the permission again.
            ActivityCompat.requestPermissions(getParent(),
                new String[]{perm},
                GET_ACCOUNTS_PERMISSIONS_REQUEST);
          }).show();
      return false;
    } else {
      // No explanation needed, we can request the permission.
      ActivityCompat.requestPermissions(this,
          new String[]{perm},
          GET_ACCOUNTS_PERMISSIONS_REQUEST);
      return false;
    }
  }

  // Callback with the request from calling requestPermissions(...)
  @Override
  @TargetApi(Build.VERSION_CODES.M)
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[],
                                         @NonNull int[] grantResults) {
    // Make sure it's our original GET_ACCTS request
    if (requestCode == GET_ACCOUNTS_PERMISSIONS_REQUEST) {
      if (grantResults.length > 0 &&
          grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Get Accounts permission granted", Toast.LENGTH_SHORT).show();
        //callback to continue
      } else {
        // showRationale = false if user clicks Never Ask Again, otherwise true
        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS);
        if (showRationale) {
          //did not grant, but did not say never ask
          ActivityCompat.requestPermissions(getParent(),
              new String[]{Manifest.permission.GET_ACCOUNTS},
              GET_ACCOUNTS_PERMISSIONS_REQUEST);
        } else {
          Toast.makeText(this, "Get Accounts permission denied, app must quit", Toast.LENGTH_SHORT).show();
          signOut();
        }
      }
    }
  }

  // @todo  bg
  private void updateUserInfo(SharedPreferences authPrefs) {
    this.prefs = Util.getUserPrefs();
    this.editor = this.prefs.edit();
    editor.putString(getApplicationContext().getString(R.string.current_user), authPrefs.getString(getApplicationContext().getString(R.string.username), ""));
    editor.putString(getApplicationContext().getString(R.string.user_id), authPrefs.getString(getApplicationContext().getString(R.string.user_nsid), ""));
    editor.commit();
  }

  private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
    return new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    };
  }

  public ArrayList<Fragment> intializeItems() {
    ArrayList<Fragment> a = new ArrayList<>();
    a.add(SearchFragment.newInstance(0, getResources().getString(R.string.commons_search), new SearchFragment()));
    a.add(InterestingFragment.newInstance(1, getResources().getString(R.string.interesting_today), new InterestingFragment()));
    a.add(SearchFragment.newInstance(2, getResources().getString(R.string.tags), new ColorFragment()));
    return a;
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  //keep small number of pages in memory mostly
  public static class MyPagerAdapter extends FragmentPagerAdapter {
    public static int NUM_ITEMS = 4;
    public FragmentManager mFragmentManager;
    private ArrayList<Fragment> mPagerItems;

    public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> pagerItems) {
      super(fragmentManager);
      this.mFragmentManager = fragmentManager;
      mPagerItems = pagerItems;
    }

    public void setPagerItems(ArrayList<Fragment> pagerItems) {
      if (mPagerItems != null)
        for (int i = 0; i < mPagerItems.size(); i++) {
          mFragmentManager.beginTransaction().remove(mPagerItems.get(i))
              .commit();
        }
      mPagerItems = pagerItems;
    }
    // Returns the fragment to display for that page

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return mPagerItems.get(0);
        case 1:
          return mPagerItems.get(1);
        case 2:
          return mPagerItems.get(2);
        case 3:
          return mPagerItems.get(3);
        default:
          return null;
      }
    }

    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
      return getItem(position).getArguments().getString(getAppContext().getString(R.string.title));
    }

    // Returns total number of pages
    @Override
    public int getCount() {
      return mPagerItems.size();
    }

    public Fragment getPagerItem(int i) {
      return mPagerItems.get(i);
    }

    public void setPagerItem(FlickrBaseFragment f) {
      mPagerItems.remove(0);
      mPagerItems.add(0, f);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
