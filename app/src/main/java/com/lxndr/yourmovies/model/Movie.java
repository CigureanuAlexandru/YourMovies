package com.lxndr.yourmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 01/02/2017.
 */

public class Movie implements Parcelable {

    protected int id;

    protected int remoteId;

    protected String imagePath;

    private String title;

    private String overview;

    private double rating;

    private String releaseDate;

    private double popularity;

    private byte isFavourite;

    public Movie(int id, int remoteId, String imagePath, String title, String overview, double rating,
                 String releaseDate, double popularity, byte isFavourite) {
        this.id = id;
        this.remoteId = remoteId;
        this.imagePath = imagePath;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.isFavourite = isFavourite;
    }

    public Movie(int remoteId, String imagePath, String title, String overview, double rating,
                 String releaseDate, double popularity, byte isFavourite) {
        this(0, remoteId,  imagePath,  title,  overview,  rating, releaseDate,  popularity, isFavourite);
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.remoteId = in.readInt();
        this.imagePath = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.popularity = in.readDouble();
        this.isFavourite = in.readByte();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public byte getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(byte isFavourite) {
        this.isFavourite = isFavourite;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", remoteId='" + remoteId + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
                ", popularity='" + popularity + '\'' +
                ", isFavourite='" + isFavourite + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(remoteId);
        dest.writeString(imagePath);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeDouble(popularity);
        dest.writeByte(isFavourite);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

}
