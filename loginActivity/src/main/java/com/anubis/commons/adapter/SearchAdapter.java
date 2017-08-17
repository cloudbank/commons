package com.anubis.commons.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anubis.commons.R;
import com.anubis.commons.animation.RecyclerAnimator;
import com.anubis.commons.listener.ItemClickListener;
import com.anubis.commons.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by sabine on 9/26/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements RecyclerViewAdapter {


    private ItemClickListener listener;


    public ItemClickListener getListener() {
        return this.listener;

    }


    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(final View itemView, final ItemClickListener listener) {
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.ivPhoto);

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
    private static boolean isTwoPane;

    private RecyclerAnimator mAnimator;

    public SearchAdapter(Context context, List<Photo> photos, boolean staggered, boolean isTwoPane) {
        mStaggered = staggered;
        mPhotos = photos;
        mContext = context;
        isTwoPane = isTwoPane;


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

        ImageView imageView = viewHolder.imageView;


        Picasso.with(this.getContext()).load(photo.getUrl()).fit().centerCrop()
                //.placeholder(android.R.drawable.btn_star)
                .error(android.R.drawable.btn_star)
                .into(imageView);


        mAnimator.onBindViewHolder(viewHolder.itemView, position);


    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }


}
