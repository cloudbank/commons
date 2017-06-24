package com.anubis.commons.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.anubis.commons.R;
import com.anubis.commons.fragments.FlickrBaseFragment;
import com.anubis.commons.fragments.ItemDetailFragment;

/**
 * Created by sabine on 5/27/17.
 */

public class ImageDisplayActivity extends AppCompatActivity{


    ItemDetailFragment fragmentItemDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        // Fetch the item to display from bundle

       // Item item = (Item) getIntent().getSerializableExtra("item");

        String pid = getIntent().getStringExtra(FlickrBaseFragment.RESULT);
        if (savedInstanceState == null) {
            // Insert detail fragment based on the item passed
            fragmentItemDetail = ItemDetailFragment.newInstance(pid, false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, fragmentItemDetail);
            ft.commit();
        }
    }

}
