package io.realm;


public interface InterestingRealmProxyInterface {
    public int realmGet$page();
    public void realmSet$page(int value);
    public String realmGet$id();
    public void realmSet$id(String value);
    public java.util.Date realmGet$timestamp();
    public void realmSet$timestamp(java.util.Date value);
    public RealmList<com.anubis.commons.models.Photo> realmGet$interestingPhotos();
    public void realmSet$interestingPhotos(RealmList<com.anubis.commons.models.Photo> value);
}
