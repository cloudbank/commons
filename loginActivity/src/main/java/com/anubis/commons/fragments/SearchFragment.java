package com.anubis.commons.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anubis.commons.FlickrClientApp;
import com.anubis.commons.R;
import com.anubis.commons.activity.ImageDisplayActivity;
import com.anubis.commons.adapter.SearchAdapter;
import com.anubis.commons.adapter.SpacesItemDecoration;
import com.anubis.commons.models.Common;
import com.anubis.commons.models.Photo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;


public class SearchFragment extends FlickrBaseFragment {


    RecyclerView rvPhotos;
    SearchAdapter searchAdapter;
    List<Photo> sPhotos = new ArrayList<Photo>();
    Realm commonsRealm, r;
    RealmChangeListener changeListener;
    ProgressDialog ringProgressDialog;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != r && !r.isClosed()) {
            r.close();
        }
        if (null != commonsRealm && !commonsRealm.isClosed())
        commonsRealm.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TABS", "search onresume");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TABS", "search activcreated");
        changeListener = new RealmChangeListener<Common>()


        {
            @Override
            public void onChange(Common c) {
                updateDisplay(c);
            }
        };
        commonsRealm = Realm.getDefaultInstance();
        ringProgressDialog.setTitle("Please wait");
        ringProgressDialog.setMessage("Retrieving tags/recent photos");
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.show();
        Common c = commonsRealm.where(Common.class).equalTo("color",Common.Colors.ALL.name()).findFirst();
        Log.d("DEBUG", "commonsFragment" + c);
        if (null == c) {
            r = Realm.getDefaultInstance();
            RealmChangeListener realmListener = new RealmChangeListener<Realm>() {
                @Override
                public void onChange(Realm r) {

                    updateDisplay();
                }
            };
            r.addChangeListener(realmListener);

        } else {
            Log.d("COMMON PRESENT", "list: " + c);
            updateDisplay(c);
            c.addChangeListener(changeListener);
            if (null != r) {
                r.removeAllChangeListeners();
                r.close();
            }
        }
        ringProgressDialog.dismiss();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchAdapter = new SearchAdapter(FlickrClientApp.getAppContext(), sPhotos, true);
        Log.d("TABS", "search oncreate");
        ringProgressDialog = new ProgressDialog(getActivity(), R.style.MyDialogTheme);
        setRetainInstance(true);
    }

    private void updateDisplay() {
        Log.d("TABS", "search updateDisplay");
        Common c = commonsRealm.where(Common.class).equalTo("color",Common.Colors.ALL.name()).findFirst();
        sPhotos.clear();
        sPhotos.addAll(c.getCommonPhotos());
        searchAdapter.notifyDataSetChanged();
    }

    private void updateDisplay(Common c) {
        Log.d("TABS", "search updateDisplay(s)");
        sPhotos.clear();
        sPhotos.addAll(c.getCommonPhotos());
        searchAdapter.notifyDataSetChanged();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvPhotos = (RecyclerView) view.findViewById(R.id.rvSearch);

        rvPhotos.setAdapter(searchAdapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvPhotos.setLayoutManager(gridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(15);
        rvPhotos.addItemDecoration(decoration);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),
                        ImageDisplayActivity.class);
                Photo photo = sPhotos.get(position);
                intent.putExtra(RESULT, photo.getId());
                startActivity(intent);
            }
        });


        setHasOptionsMenu(true);
        return view;

    }


    void customLoadMoreDataFromApi(int page) {
    }


}
