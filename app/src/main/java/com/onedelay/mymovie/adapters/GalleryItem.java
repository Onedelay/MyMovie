package com.onedelay.mymovie.adapters;

public class GalleryItem {
    private String thumbUrl;
    private String type;
    private String url;

    public GalleryItem(String thumbUrl, String type, String url) {
        this.thumbUrl = thumbUrl;
        this.type = type;
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
