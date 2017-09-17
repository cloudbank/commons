package com.anubis.commons.models;

/**
 * Created by sabine on 12/9/16.
 */
public class ColorPhotos {
  private String code;
  private Photos p;

  public ColorPhotos(String s, Photos p) {
    this.code = s;
    this.p = p;
  }

  public Photos getP() {
    return p;
  }

  public void setP(Photos p) {
    this.p = p;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
