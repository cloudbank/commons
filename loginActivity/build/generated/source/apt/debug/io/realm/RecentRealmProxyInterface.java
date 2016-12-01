package io.realm;


public interface RecentRealmProxyInterface {
    public String realmGet$id();
    public void realmSet$id(String value);
    public java.util.Date realmGet$timestamp();
    public void realmSet$timestamp(java.util.Date value);
    public RealmList<com.anubis.commons.models.Photo> realmGet$recentPhotos();
    public void realmSet$recentPhotos(RealmList<com.anubis.commons.models.Photo> value);
    public RealmList<com.anubis.commons.models.Tag> realmGet$hotTagList();
    public void realmSet$hotTagList(RealmList<com.anubis.commons.models.Tag> value);
}
