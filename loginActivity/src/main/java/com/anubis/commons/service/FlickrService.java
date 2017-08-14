package com.anubis.commons.service;

import com.anubis.commons.models.Comment;
import com.anubis.commons.models.Comments;
import com.anubis.commons.models.Photos;
import com.anubis.oauthkit.BuildConfig;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by sabine on 9/18/16.
 */

public interface FlickrService {


    String API_BASE_URL = "https://api.flickr.com/services/rest/";

    String API_CALLBACK_URL = "oauth://cprest";
    String KEY = "api_key=" + BuildConfig.consumerKey;

    @GET(API_BASE_URL + "?method=flickr.photos.search&content_type=7&safe_search=1&structured=yes&per_page=500&extras=date_taken,owner_name,tags,description,url_s&format=json&nojsoncallback=1&" + KEY + "&text='Search The Commons'&is_commons=true&sort=relevance")
    Observable<Photos> commons(@Query("page") String page);

    @GET(API_BASE_URL + "?method=flickr.photos.search&content_type=7&safe_search=1&structured=yes&per_page=500&extras=date_taken,owner_name,tags,description,url_s&format=json&nojsoncallback=1&" + KEY )
    Observable<Photos> bycolor(@QueryMap Map<String, String> options);


    @GET(API_BASE_URL + "?method=flickr.photos.search&per_page=500&extras=date_taken,owner_name,tags,description,url_s&format=json&nojsoncallback=1&" + KEY + "&is_commons=true&sort=interestingness-desc")
    Observable<Photos> explore(@Query("page") String page);

    @GET(API_BASE_URL + "?method=flickr.photos.comments.getList&format=json&nojsoncallback=1&" + KEY)
    Observable<Comments> getComments(@Query("photo_id") String photoId);

    @POST(API_BASE_URL + "?method=flickr.photos.comments.addComment&format=json&nojsoncallback=1&" + KEY)
    Observable<Comment> addComment(@QueryMap Map<String, String> options);


}

