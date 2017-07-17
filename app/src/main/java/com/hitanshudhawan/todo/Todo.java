package com.hitanshudhawan.todo;

import java.util.Date;

/**
 * Created by hitanshu on 17/7/17.
 */

public class Todo {

    private String mTitle;
    private String mDescription;
    private Date mDate;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

}
