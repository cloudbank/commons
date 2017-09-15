package com.anubis.commons.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.adapter.CommentAdapter;
import com.anubis.commons.models.Comment;
import com.anubis.commons.models.Comments;
import com.anubis.commons.models.Comments_;
import com.anubis.commons.models.Photo;
import com.anubis.commons.util.ImageRoundedTransformation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.hkm.soltag.TagContainerLayout;
import io.realm.Realm;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static com.anubis.commons.FlickrClientApp.getAppContext;


public class ItemDetailFragment extends Fragment {

    TagContainerLayout mTags;
    EditText etComments;
    String mUid = "";
    List<Comment> mComments = new ArrayList<>();
    private Subscription subscription, subscription2;
    Map<String, String> data = new HashMap<>();
    Photo mPhoto;
    Realm pRealm;
    AdView mPublisherAdView;
    CommentAdapter cAdapter;
    RecyclerView rvComments;
    HandlerThread handlerThread;
    static Comments_ comments_;

    public static ItemDetailFragment newInstance(String pid, boolean isTwoPane) {
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putString("pid", pid);
        args.putBoolean("isTwoPane", isTwoPane);
        itemDetailFragment.setArguments(args);
        return itemDetailFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_display,
                container, false);
        String pid = getArguments().getString("pid");
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackground(getResources().getDrawable(R.drawable.background_toolbar));
        boolean isTwoPane = getArguments().getBoolean("isTwoPane");
        if (isTwoPane) {
            toolbar.setVisibility(GONE);
        } else {

            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        pRealm = Realm.getDefaultInstance();
        mPhoto = pRealm.where(Photo.class).equalTo("id", pid).findFirst();

        cAdapter = new CommentAdapter(getAppContext(), mComments);


        // wvComments = (WebView) view.findViewById(comments);
        // wvComments.setVerticalScrollBarEnabled(true);
        // wvComments.setHorizontalScrollBarEnabled(true);
        //  wvComments.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //  wvComments.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //wvComments.getSettings().setJavaScriptEnabled(true);

        mUid = mPhoto.getId();
        rvComments = (RecyclerView) view.findViewById(R.id.rvComments);
        cAdapter = new CommentAdapter(getAppContext(), mComments);
        rvComments.setAdapter(cAdapter);
        LinearLayoutManager linLayoutManager =
                new LinearLayoutManager(getAppContext());
        rvComments.setLayoutManager(linLayoutManager);
        //@todo comments do not get refreshed w sync adapter
        //@todo this needs to check if 24 hr has passed
        Comments_ c = pRealm.where(Comments_.class).equalTo("photoId", mUid).findFirst();
        if (null == c || (!withinADay(c.getTimestamp()))) {
            //network call
            getComments(mUid);
        } else {
            displayComments(rvComments, c.commentsList);
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.ivResult);
        Picasso.with(getAppContext()).load(mPhoto.getUrl()).fit().transform(new ImageRoundedTransformation(5, 5)).resize(300, 300).centerCrop().into(imageView);

        TextView tvUsername = (TextView) view.findViewById(R.id.tvAuthor);
        tvUsername.setText(mPhoto.getOwnername());
        //TextView tvTimestamp = (TextView) view.findViewById(R.id.timestamp);
        //@todo relative Time pretty time
        //tvTimestamp.setText(DateUtility.relativeTime(mPhoto.getDatetaken(), this));

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText(mPhoto.getTitle());


        // etComments = (EditText) view.findViewById(R.id.etComments);
        // etComments.setScroller(new Scroller(this));
        // etComments.setMaxLines(1);
        // etComments.setVerticalScrollBarEnabled(true);
        // etComments.setMovementMethod(new ScrollingMovementMethod());

        etComments = (EditText) view.findViewById(R.id.etComments);
        // mTags = (TagContainerLayout) view.findViewById(R.id.tag_group);
        //displayTags(mPhoto.getTags());
        mPublisherAdView = (AdView) view.findViewById(R.id.publisherAdView2);
        AdRequest adRequest = new AdRequest.Builder()
                // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                // .addTestDevice("39EA0A51CB1E58F7B6CFC094BD01CA18")  // My Galaxy Nexus test phone
                .build();
        mPublisherAdView.loadAd(adRequest);
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        Button btn = (Button) view.findViewById(R.id.btnComments);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addComment();
            }
        });

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView textView = (TextView) view.findViewById(R.id.tvComments);

                if (etComments.getVisibility() == View.VISIBLE) {
                    textView.setVisibility(View.VISIBLE);
                    etComments.setVisibility(GONE);
                    btn.setVisibility(GONE);
                } else {
                    textView.setVisibility(View.GONE);
                    etComments.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.VISIBLE);
                }
            }


        });
        return view;
    }

    private boolean withinADay(Date timestamp) {
        long diff = new Date().getTime() - timestamp.getTime();
        return diff < 86400000;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //getActivity().mViewPager.setCurrentItem(viewPageSelected - 1);
                //getActivity().getSupportFragmentManager().popBackStack();
                //@todo backstack viewpager
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getComments(final String uid) {
        //Observable<PhotoInfo> photoInfo = FlickrClientApp.getJacksonService().getPhotoInfo(uid);
        subscription = FlickrClientApp.getJacksonService().getComments(uid)

                .subscribeOn(Schedulers.io()) // optional if you do not wish to override the default behavior
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Comments>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            int code = response.code();
                            Log.e("ERROR", String.valueOf(code));
                        }
                        Log.e("ERROR", "error getting comments" + e);
                    }

                    @Override
                    public void onNext(Comments c) {

                        List<Comment> comments = c.getComments().getComments();
                        Log.d("DEBUG", "comments: " + comments);
                        if (comments.size() > 0) {
                            if (saveComments(comments, mUid)) {
                                displayComments(rvComments, comments);
                            } else {
                                Log.e("ERROR", "comments not saved: " + c);
                            }
                        }
                    }
                });

    }


    //prevent activity leaks
    private static class SHandler extends Handler {
        public SHandler(Looper l) {
            super(l);
        }

    }

    private static class SRunnable implements Runnable {
        String uid;
        List<Comment> cList;

        public SRunnable(String s, List l) {
            uid = s;
            cList = l;
        }

        @Override
        public void run() {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                // realm.beginTransaction();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        comments_ = realm.where(Comments_.class).equalTo("photoId", uid).findFirst();
                        if (null == comments_) {
                            comments_ = realm.createObject(Comments_.class, uid);
                        }

                        for (Comment comment : cList) {
                            if (!comments_.commentsList.contains(comment)) {
                                comments_.commentsList.add(comment);
                            }
                        }
                        comments_.setTimestamp(new Date());
                        realm.insertOrUpdate(comments_);
                    }
                });
                //  realm.copyToRealmOrUpdate(c);
                //realm.commitTransaction();
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }
    }


    public boolean saveComments(final List<Comment> cList, final String uid) {
        Log.d("SAVE COMMENT", String.valueOf(cList.get(0).getContent()) + ":" + uid);

        handlerThread = new HandlerThread("comments");
        handlerThread.start();
        SHandler sHandler = new SHandler(handlerThread.getLooper());

        return sHandler.post(new SRunnable(uid, cList));


    }

    private void displayComments(RecyclerView commentsView, List<Comment> comments) {

        mComments.addAll(comments);
        Collections.sort(mComments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return new Date(Long.parseLong(o2.getDatecreate()) * 1000L).compareTo(new Date(Long.parseLong(o1.getDatecreate()) * 1000L));
            }
        });
        cAdapter.notifyDataSetChanged();


    }


    //@todo check for idempotence;in reverse w date
    public void addComment() {
        //openComments
        String commentString = etComments.getText().toString();
        etComments.setText("");
        etComments.clearFocus();
        if (commentString.length() > 0) {

            data.put("comment_text", commentString);
            data.put("photo_id", mPhoto.getId());
            subscription2 = FlickrClientApp.getJacksonService().addComment(data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Comment>() {
                        @Override
                        public void onCompleted() {


                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof HttpException) {
                                HttpException response = (HttpException) e;
                                int code = response.code();
                                Log.e("ERROR", String.valueOf(code));
                            }
                            Log.e("ERROR", "error add comment" + e);
                        }

                        @Override
                        public void onNext(Comment c) {
                            //@todo bg thread for all here?
                            //@todo maybe we should let this be a network call to have fresh data?
                            List<Comment> displayList = new ArrayList<>();
                            Realm cRealm = Realm.getDefaultInstance();
                            Comments_ comments = cRealm.where(Comments_.class).equalTo("photoId", mUid).findFirst();
                            if (null != comments && comments.getCommentsList().size() > 0) {
                                displayList.addAll(comments.getCommentsList());
                            }


                            List<Comment> commentsList = new ArrayList<>();
                            Comment comment = new Comment();
                            //@todo  workaround the json maps anon obj { comment: {
                            Map comObj = (Map) c.getAdditionalProperties().get("comment");
                            comment.setAuthor((String) comObj.get("author"));
                            comment.setId((String) comObj.get("id"));
                            comment.setAuthorname((String) comObj.get("authorname"));
                            comment.setDatecreate((String) comObj.get("datecreate"));
                            comment.setContent((String) comObj.get("_content"));
                            commentsList.add(comment);
                            if (saveComments(commentsList, mUid)) {
                                displayList.add(comment);
                                displayComments(rvComments, displayList);
                            } else {
                                //@todo comment not added
                                Log.e("ERROR", "comment not added: " + c);
                            }
                            cRealm.close();
                        }
                    });

        }
    }

    //

    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();


    }

    @Override
    public synchronized void onPause() {


        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }

        if (null != pRealm && !pRealm.isClosed()) {
            pRealm.close();
        }
        if (null != subscription) {
            subscription.unsubscribe();
        }
        if (null != subscription2) {
            subscription2.unsubscribe();
        }


    }

}
