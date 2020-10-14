package com.actiontec.gushitie.audiopicker;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by root on 16-12-19.
 */

public class Music {
    private long id;
    private String title;
    private String singer;
    private String album;
    private String url;
    private String name;
    private String image;

    public String getImage() {
      return image;
    }

    public void setImage(String img) {
      this.image = img;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
