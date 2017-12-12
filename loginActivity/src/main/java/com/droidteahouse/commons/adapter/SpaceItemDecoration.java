package com.droidteahouse.commons.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by sabine on 10/21/17.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

  private int halfSpace;

  public SpaceItemDecoration() {

  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    //BaseCard.CARD_TYPE viewType = (BaseCard.CARD_TYPE)view.getTag();
    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).leftMargin = 0;
    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).rightMargin = 0;
    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).topMargin = 0;
    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).bottomMargin= 0;
  }
}