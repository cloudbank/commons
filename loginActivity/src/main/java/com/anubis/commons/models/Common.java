package com.anubis.commons.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sabine on 10/6/16.
 */

public class Common extends RealmObject implements RealmModel {

    public enum Colors {
        RED("red"), YELLOW("yellow"), BLUE("blue"), VIOLET("violet"), ALL("all");
        private String name;
        Colors(String name) {
            this.name=name;
        }

    }
    @PrimaryKey
    public String id;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String color;

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

    public RealmList<Photo> getColorPhotos() {
        return colorPhotos;
    }

    public void setColorPhotos(RealmList<Photo> colorPhotos) {
        this.colorPhotos = colorPhotos;
    }

    public RealmList<Photo> colorPhotos;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
