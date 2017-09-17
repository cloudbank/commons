package com.anubis.commons.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

import com.anubis.commons.R;
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.RecyclerViewAdapter;
import com.anubis.commons.listener.ItemClickListener;
import com.anubis.commons.models.Photo;

import java.util.List;

public abstract class FlickrBaseFragment extends Fragment {
  public static final String RESULT = "result";
  protected static final String PAGE = "page";
  protected static final String TITLE = "title";
  protected static boolean isTwoPane;
  static ProgressDialog dialog;

  public static void setPane(boolean b) {
    isTwoPane = b;
  }

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
    setHasOptionsMenu(true);
  }

  public void setItemListener(RecyclerViewAdapter adapter, List<Photo> items) {
    adapter.setItemClickListener(new ItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        //user interface for activity\
        Photo photo = items.get(position);
        if (isTwoPane) {
          //start a fragment
          ItemDetailFragment fragmentItem = ItemDetailFragment.newInstance(photo.getId(), true);
          FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
          ft.replace(R.id.flDetailContainer, fragmentItem);
          ft.commit();
        } else {
          Intent intent = new Intent(getActivity(),
              ImageDisplayActivity.class);
          intent.putExtra(RESULT, photo.getId());
          startActivity(intent);
        }
      }
    });
  }

  public void showProgress(String msg) {
    // if (dialog == null) {
    dialog = new ProgressDialog(getActivity());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setBackgroundDrawable(
        getActivity().getApplication().getResources().getDrawable(
            R.drawable.background_gradient));
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setMessage(msg);
    dialog.show();
  }

  public static void dismissProgress() {
    dialog.dismiss();
  }
}