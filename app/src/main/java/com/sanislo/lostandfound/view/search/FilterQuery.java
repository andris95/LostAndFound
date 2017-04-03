package com.sanislo.lostandfound.view.search;

/**
 * Created by root on 03.04.17.
 */

public class FilterQuery {
    private boolean isExactMatch;
    private String category;
    private String type;
    private String city;

    public FilterQuery(boolean isExactMatch, String category, String type, String city) {
        this.isExactMatch = isExactMatch;
        this.category = category;
        this.type = type;
        this.city = city;
    }

    public boolean isExactMatch() {
        return isExactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        isExactMatch = exactMatch;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "FilterQuery{" +
                "isExactMatch=" + isExactMatch +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
