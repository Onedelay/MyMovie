package com.onedelay.mymovie;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewData implements Parcelable {
    public int image;
    public String id;
    public long time;
    public float rating;
    public String content;
    public String recommend;

    public ReviewData(int image, String id, long time, float rating, String content, String recommend) {
        this.image = image;
        this.id = id;
        this.time = time;
        this.rating = rating;
        this.content = content;
        this.recommend = recommend;
    }

    public ReviewData(Parcel src){
        image = src.readInt();
        id = src.readString();
        time = src.readLong();
        rating = src.readFloat();
        content = src.readString();
        recommend = src.readString();
    }

    public static final Parcelable.Creator CREATOR = new Creator(){
        @Override
        public ReviewData createFromParcel(Parcel parcel) {
            return new ReviewData(parcel);
        }

        @Override
        public ReviewData[] newArray(int i) {
            return new ReviewData[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(image);
        parcel.writeString(id);
        parcel.writeLong(time);
        parcel.writeFloat(rating);
        parcel.writeString(content);
        parcel.writeString(recommend);
    }

    public int getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public float getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getRecommend() {
        return recommend;
    }
}
