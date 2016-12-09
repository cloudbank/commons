package com.anubis.commons.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sabine on 10/6/16.
 */

public class Color extends RealmObject implements RealmModel {
    public enum Colors {
        RED("red","0"), YELLOW("yellow","4"), BLUE("blue","8"), VIOLET("violet","9");
        private String name;
        public String code;

        Colors(String name, String code) {
            this.name=name;
            this.code=code;
        }

    }
    @PrimaryKey
    public String id;

    public Date timestamp;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String color;

    public RealmList<Photo> getColorPhotos() {
        return colorPhotos;
    }

    public void setColorPhotos(RealmList<Photo> colorPhotos) {
        this.colorPhotos = colorPhotos;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Photo> colorPhotos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}