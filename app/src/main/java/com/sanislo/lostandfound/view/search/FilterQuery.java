package com.sanislo.lostandfound.view.search;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 03.04.17.
 */

public class FilterQuery implements Parcelable {
    private String category;
    private int type;
    private String city;
    private int radius;
    private boolean newestFirst;
    private boolean returnedOnly;

    public FilterQuery(String category, int type, String city, int radius, boolean newestFirst, boolean returnedOnly) {
        this.category = category;
        this.type = type;
        this.city = city;
        this.radius = radius;
        this.newestFirst = newestFirst;
        this.returnedOnly = returnedOnly;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isNewestFirst() {
        return newestFirst;
    }

    public void setNewestFirst(boolean newestFirst) {
        this.newestFirst = newestFirst;
    }

    public boolean isReturnedOnly() {
        return returnedOnly;
    }

    public void setReturnedOnly(boolean returnedOnly) {
        this.returnedOnly = returnedOnly;
    }

    @Override
    public String toString() {
        return "FilterQuery{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                ", radius=" + radius +
                ", newestFirst=" + newestFirst +
                ", returnedOnly=" + returnedOnly +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeInt(type);
        dest.writeString(city);
        dest.writeInt(radius);
        dest.writeByte((byte) (newestFirst ? 1 : 0));
        dest.writeByte((byte) (returnedOnly ? 1 : 0));
    }

    public static final Parcelable.Creator<FilterQuery> CREATOR = new Parcelable.Creator<FilterQuery>() {
        // распаковываем объект из Parcel
        public FilterQuery createFromParcel(Parcel in) {
            return new FilterQuery(in);
        }

        public FilterQuery[] newArray(int size) {
            return new FilterQuery[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private FilterQuery(Parcel in) {
        category = in.readString();
        type = in.readInt();
        city = in.readString();
        radius = in.readInt();
        newestFirst = in.readByte() != 0;
        returnedOnly = in.readByte() != 0;
    }
}
