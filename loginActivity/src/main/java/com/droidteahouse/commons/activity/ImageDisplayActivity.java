package com.droidteahouse.commons.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.droidteahouse.commons.R;
import com.droidteahouse.commons.fragments.FlickrBaseFragment;
import com.droidteahouse.commons.fragments.ItemDetailFragment;

/**
 * Created by sabine on 5/27/17.
 */
public class ImageDisplayActivity extends AppCompatActivity {
  ItemDetailFragment fragmentItemDetail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail);
    String pid = getIntent().getStringExtra(FlickrBaseFragment.RESULT);
    if (savedInstanceState == null) {
      fragmentItemDetail = ItemDetailFragment.newInstance(pid, false);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.flDetailContainer, fragmentItemDetail);
      ft.commit();
    }
  }
}
