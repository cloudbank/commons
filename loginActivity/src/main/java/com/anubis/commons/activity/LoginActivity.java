package com.anubis.commons.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.util.Util;
import com.anubis.oauthkit.OAuthBaseClient;
import com.anubis.oauthkit.OAuthLoginActivity;

import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

public class LoginActivity extends OAuthLoginActivity {

    OAuthBaseClient client;
    ImageView spinner;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 10.0f * 360.0f;// 3.141592654f * 32.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@todo clear tokens for notifications open--don't want someone to access someone else's account this way
        setContentView(R.layout.activity_login);
        View v = findViewById(R.id.loginBtn);
        v.setOnClickListener(v1 -> loginToRest(v1));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //prevent leaking activity
        //@todo clean up of async tasks for fetching tokens
        if (null != this.client && null != this.client.getAccessHandler() ) {
            OAuthBaseClient.OAuthAccessHandler handler = this.client.getAccessHandler();
            handler = null;
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    */

    @Override
    public void onLoginSuccess(OkHttpOAuthConsumer consumer, String baseUrl) {
        //@todo save these to realm
        FlickrClientApp.createJacksonService(consumer);
        saveConsumer(consumer);
        Intent i = new Intent(this, PhotosActivity.class);
        startActivity(i);
     }

    private static void saveConsumer(OkHttpOAuthConsumer consumer) {
        SharedPreferences mPrefs = Util.getUserPrefs();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(consumer);
        prefsEditor.putString(FlickrClientApp.getAppContext().getString(R.string.Consumer), json);
        prefsEditor.commit();
    }

    @Override
    public void onLoginFailure(Exception e) {
        spinner.setAnimation(null);
        Toast.makeText(
                this,
                "There is a problem with login to the app.  Please check your internet connection and try again.",
                Toast.LENGTH_LONG).show();

        Log.e("ERROR", e.toString());

        e.printStackTrace();
    }

    public void loginToRest(View view) {
        RotateAnimation anim = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

// Start animating the image
        spinner = (ImageView) findViewById(R.id.logo);
        spinner.setVisibility(View.VISIBLE);
        ///logo.setAlpha(.20f);
        spinner.startAnimation(anim);

        final ProgressDialog ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setTitle("Please wait");
        ringProgressDialog.setMessage("Preparing to login");
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.show();
        if (!isNetworkAvailable()) {
            Toast.makeText(this, " You have no network/internet connection",
                    Toast.LENGTH_SHORT).show();
        } else {
            client = OAuthBaseClient.getInstance(FlickrClientApp.getAppContext(), this);
            client.connect();
        }

        ringProgressDialog.dismiss();
    }


}
