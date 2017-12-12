package com.droidteahouse.commons.adapter;
/**
 * Created by sabine on 5/15/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidteahouse.commons.R;
import com.droidteahouse.commons.models.Comment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

/**
 * Created by sabine on 9/26/16.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    TextView tvAuthor;
    TextView tvComment;

    public ViewHolder(final View itemView) {
      super(itemView);
      ivImage = itemView.findViewById(R.id.ivComment);
      tvAuthor = itemView.findViewById(R.id.author);
      tvComment = itemView.findViewById(R.id.content);
    }
  }

  private List<Comment> mComments;
  private Context mContext;

  public CommentAdapter(Context context, List<Comment> comments) {
    mComments = comments;
    mContext = context;
  }

  private Context getContext() {
    return mContext;
  }

  @Override
  public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View photosView = inflater.inflate(R.layout.comment_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(photosView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(CommentAdapter.ViewHolder viewHolder, int position) {
    Comment comment = mComments.get(position);
    TextView user = viewHolder.tvAuthor;
    String time = new PrettyTime().format(new Date(Long.parseLong(comment.getDatecreate()) * 1000L));
    user.setText(comment.getAuthorname() + "  " + time);
    TextView content = viewHolder.tvComment;
    String cString = comment.getContent();
    content.setLinkTextColor(fetchColor(R.color.BlackReed));
    content.setText(content.getText());
    Spanned spanned = Html.fromHtml(cString, new Html.ImageGetter() {
      @Override
      public Drawable getDrawable(String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();
        Picasso.with(getContext()).load(source).fit().into(new Target() {
          @Override
          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            final BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            result.setDrawable(drawable);
            result.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
          }

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {
          }
        });
        return result;
      }
    }, null);
    //@todo regex for img
    content.setText(spanned);
  }

  static class BitmapDrawablePlaceHolder extends BitmapDrawable {
    protected Drawable drawable;

    @Override
    public void draw(final Canvas canvas) {
      if (drawable != null) {
        drawable.draw(canvas);
      }
    }

    public void setDrawable(Drawable drawable) {
      this.drawable = drawable;
    }
  }

  private int fetchColor(@ColorRes int color) {
    return ContextCompat.getColor(getContext(), color);
  }

  @Override
  public int getItemCount() {
    return mComments.size();
  }
}