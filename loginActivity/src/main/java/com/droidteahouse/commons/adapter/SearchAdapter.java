package com.droidteahouse.commons.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.droidteahouse.commons.R;
import com.droidteahouse.commons.animation.RecyclerAnimator;
import com.droidteahouse.commons.listener.ItemClickListener;
import com.droidteahouse.commons.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by sabine on 9/26/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements RecyclerViewAdapter {
  private ItemClickListener listener;
  private View mSelectedView;

  public ItemClickListener getListener() {
    return this.listener;
  }

  public void setItemClickListener(ItemClickListener listener) {
    this.listener = listener;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    CardView cardView;


    public ViewHolder(final View itemView, final ItemClickListener listener) {
      super(itemView);
      imageView = itemView.findViewById(R.id.ivPhoto);
      cardView = itemView.findViewById(R.id.cardView);
      itemView.setOnClickListener(v -> {
        if (listener != null) {
          int position = getAdapterPosition();
          if (position != RecyclerView.NO_POSITION) {
            listener.onItemClick(itemView, position);
          }
        }
        //v.setPressed(true);
      });
    }
  }

  private List<Photo> mPhotos;
  private Context mContext;
  private boolean mStaggered;
  private int lastPosition = -1;
  private boolean mTwoPane;
  private boolean isInit = true;
  private RecyclerAnimator mAnimator;

  public SearchAdapter(Context context, List<Photo> photos, boolean staggered, boolean isTwoPane) {
    mStaggered = staggered;
    mPhotos = photos;
    mContext = context;
    mTwoPane = isTwoPane;
  }

  public void setmAnimator(RecyclerView rv) {
    mAnimator = new RecyclerAnimator(rv);
  }

  private Context getContext() {
    return mContext;
  }

  @Override
  public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View photosView = inflater.inflate(R.layout.photo_item_friends, parent, false);
    ViewHolder viewHolder = new ViewHolder(photosView, getListener());
    mAnimator.onCreateViewHolder(photosView);
    return viewHolder;
  }

  private int mLastPosition;

  @Override
  public void onBindViewHolder(SearchAdapter.ViewHolder viewHolder, int position) {
    Photo photo = mPhotos.get(position);
    CardView cv = viewHolder.cardView;
    cv.setUseCompatPadding(true);
    //cv.setCardElevation(2.0f);

    StaggeredGridLayoutManager.LayoutParams cl = (StaggeredGridLayoutManager.LayoutParams) viewHolder.cardView.getLayoutParams();
    ImageView imageView = viewHolder.imageView;
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView
        .getLayoutParams();
    int aspectRatio = 0;
    if ((photo != null && photo.getHeight() != null) && photo.getWidth() != null) {
      aspectRatio = (Integer.parseInt(photo.getHeight()) / Integer.parseInt(photo.getWidth()));
    } else {
      Log.d(TAG, " photo is null" + photo);
    }
    Random rand = new Random();
    int n = rand.nextInt(100) + 200;
    lp.height = n; // photo.getPhotoHeight() * 2;
    lp.width = aspectRatio > 0 ? n / aspectRatio : n;//
    cl.width = lp.width;
    cl.height = lp.height;
    cv.setLayoutParams(cl);
    Picasso.with(this.getContext()).load(photo.getUrl()).fit().centerCrop()
        .placeholder(android.R.drawable.btn_star)
        .error(android.R.drawable.btn_star)
        .into(imageView);
  }



  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return mPhotos.size();
  }
}
