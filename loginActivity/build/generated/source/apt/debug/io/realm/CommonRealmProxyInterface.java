package io.realm;


public interface CommonRealmProxyInterface {
    public String realmGet$id();
    public void realmSet$id(String value);
    public int realmGet$page();
    public void realmSet$page(int value);
    public java.util.Date realmGet$timestamp();
    public void realmSet$timestamp(java.util.Date value);
    public RealmList<com.anubis.commons.models.Photo> realmGet$commonPhotos();
    public void realmSet$commonPhotos(RealmList<com.anubis.commons.models.Photo> value);
}
