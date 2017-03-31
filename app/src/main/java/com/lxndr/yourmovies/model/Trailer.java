package com.lxndr.yourmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 01/02/2017.
 */

public class Trailer implements Parcelable {


    private String site;

    private String key;

    public Trailer(String site, String key) {
        this.site = site;
        this.key = key;
    }

    private Trailer(Parcel in) {
        this.site = in.readString();
        this.key = in.readString();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "site='" + site + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(site);
        dest.writeString(key);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }

    };

}
