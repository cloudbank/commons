package com.anubis.commons.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sabine on 10/6/16.
 */

public class Common extends RealmObject implements RealmModel {

    public static int total=0;
    public static int pages =0;
    public static int count=1;

    @PrimaryKey

    public String id;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Index
    public int page;


    public Date timestamp;

    public RealmList<Photo> getCommonPhotos() {
        return commonPhotos;
    }

    public void setCommonPhotos(RealmList<Photo> commonPhotos) {
        this.commonPhotos = commonPhotos;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Photo> commonPhotos;


    public void setId(String id) {
        this.id = id;
    }


}
