package com.example.akshayb.nytsearch.models;

import java.util.Date;

/**
 * Created by akshayb on 11/16/16.
 */

public class SearchFilter {

    Date beginDate;
    boolean sortOldest;
    boolean arts;
    boolean fashionAndStyle;
    boolean sports;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public boolean isSortOldest() {
        return sortOldest;
    }

    public void setSortOldest(boolean sortOldest) {
        this.sortOldest = sortOldest;
    }

    public boolean isArts() {
        return arts;
    }

    public void setArts(boolean arts) {
        this.arts = arts;
    }

    public boolean isFashionAndStyle() {
        return fashionAndStyle;
    }

    public void setFashionAndStyle(boolean fashionAndStyle) {
        this.fashionAndStyle = fashionAndStyle;
    }

    public boolean isSports() {
        return sports;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }
}
