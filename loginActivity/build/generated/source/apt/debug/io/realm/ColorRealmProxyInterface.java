package io.realm;


public interface ColorRealmProxyInterface {
    public String realmGet$id();
    public void realmSet$id(String value);
    public java.util.Date realmGet$timestamp();
    public void realmSet$timestamp(java.util.Date value);
    public String realmGet$color();
    public void realmSet$color(String value);
    public RealmList<com.anubis.commons.models.Photo> realmGet$colorPhotos();
    public void realmSet$colorPhotos(RealmList<com.anubis.commons.models.Photo> value);
}
