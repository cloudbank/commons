package com.anubis.commons.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anubis.commons.R;
import com.anubis.commons.listener.ItemClickListener;
import com.anubis.commons.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by sabine on 9/26/16.
 */

public class InterestingAdapter extends RecyclerView.Adapter<InterestingAdapter.ViewHolder>  implements RecyclerViewAdapter {


    private ItemClickListener listener;

    public ItemClickListener getListener(){
        return this.listener;

    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        CardView cardView;


        public ViewHolder(final View itemView, final ItemClickListener listener) {
            super(itemView);


            ivImage = (ImageView) itemView.findViewById(R.id.ivPhoto);
            cardView = (CardView)itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });

        }
    }

    private List<Photo> mPhotos;
    private Context mContext;
    private boolean mStaggered;

    public InterestingAdapter(Context context, List<Photo> photos, boolean staggered) {
        mStaggered = staggered;
        mPhotos = photos;
        mContext = context;
        this.setHasStableIds(true);

    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public InterestingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photosView = inflater.inflate(R.layout.photo_item_friends, parent, false);

        ViewHolder viewHolder = new ViewHolder(photosView, getListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InterestingAdapter.ViewHolder viewHolder, int position) {
        Photo photo = mPhotos.get(position);

        ImageView imageView = viewHolder.ivImage;


        CardView cv = viewHolder.cardView;

        cv.setUseCompatPadding(true);
        cv.setCardElevation(2.0f);
        //StaggeredGridLayoutManager.LayoutParams fp = (StaggeredGridLayoutManager.LayoutParams) viewHolder.cardView.getLayoutParams();

        if (mStaggered) {
            int aspectRatio = (null != photo.getWidth()  && null != photo.getHeight()) ? Integer.parseInt(photo.getHeight())/Integer.parseInt(photo.getWidth()): 1;

            Random rand = new Random();
            int n = rand.nextInt(60) + 75;
            cv.setMinimumHeight(n); // photo.getPhotoHeight() * 2;
            //n = rand.nextInt(200) + 100;
            cv.setMinimumWidth(aspectRatio > 0 ? n/aspectRatio : n);
        }


        Picasso.with(this.getContext()).load(photo.getUrl()).fit().centerCrop()
                //.placeholder(android.R.drawable.btn_star)
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
