package com.anubis.commons.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anubis.commons.R;
import com.anubis.commons.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;


/**
 * Created by sabine on 9/26/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    private OnItemClickListener listener;


    public OnItemClickListener getListener() {
        return this.listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.ivPhoto);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });


        }
    }

    private List<Photo> mPhotos;
    private Context mContext;
    private boolean mStaggered;

    public SearchAdapter(Context context, List<Photo> photos, boolean staggered) {
        mStaggered = staggered;
        mPhotos = photos;
        mContext = context;


    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photosView = inflater.inflate(R.layout.photo_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(photosView, getListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder viewHolder, int position) {
        Photo photo = mPhotos.get(position);

        ImageView imageView = viewHolder.imageView;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.imageView
                .getLayoutParams();



        int aspectRatio = (null != photo.getWidth() && null != photo.getHeight()) ? Integer.parseInt(photo.getHeight()) / Integer.parseInt(photo.getWidth()) : 1;

        if (mStaggered) {
            Random rand = new Random();
            int n = rand.nextInt(200) + 200;
            lp.height = n; // photo.getPhotoHeight() * 2;
            //n = rand.nextInt(200) + 100;

            lp.width = aspectRatio > 0 ? n / aspectRatio : n; // photo.getPhotoList//set the title, name, comments
            imageView.setLayoutParams(lp);

        } else {
            lp.height = 250;
            //lp.width = 300;
        }

        Picasso.with(this.getContext()).load(photo.getUrl()).fit().centerCrop()
                //.placeholder(android.R.drawable.btn_star)
                .error(android.R.drawable.btn_star)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }


}
