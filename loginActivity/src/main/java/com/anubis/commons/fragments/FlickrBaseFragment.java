package com.anubis.commons.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anubis.commons.R;
import com.anubis.commons.activity.LoginActivity;
import com.anubis.oauthkit.OAuthBaseClient;

public abstract class FlickrBaseFragment extends Fragment {

    public static final String RESULT = "result";
    protected static final String PAGE = "page";
    protected static final String TITLE = "title";



    ProgressDialog dialog;
    // newInstance constructor for creating fragment with arguments
    public static FlickrBaseFragment newInstance(int page, String title, FlickrBaseFragment f) {
        Bundle args = new Bundle();
        args.putInt(PAGE, page);
        args.putString(TITLE, title);
        f.setArguments(args);
        return f;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.photos, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();


        if (itemId == R.id.action_logout) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }


    public void signOut() {

        OAuthBaseClient.getInstance(getActivity().getApplicationContext(), null).clearTokens();
        Intent bye = new Intent(getActivity(), LoginActivity.class);
        startActivity(bye);
    }

    public void showProgress(String msg)
    {
        if(dialog == null){
            dialog = new ProgressDialog(getActivity(), R.style.MyDialogTheme);
            dialog.setTitle(null);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        if(dialog.isShowing())
        {
            dialog.dismiss();
        }

        dialog.setMessage(msg);
        dialog.show();
    }

    public void dismissProgress()
    {
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }



}