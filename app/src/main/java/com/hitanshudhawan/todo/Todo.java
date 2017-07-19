package com.hitanshudhawan.todo;

import java.util.Calendar;

/**
 * Created by hitanshu on 17/7/17.
 */

public class Todo {

    private long id;
    private String mTitle;
    private Calendar mDate;

    public Todo(long id, String mTitle, Calendar mDate) {
        this.id = id;
        this.mTitle = mTitle;
        this.mDate = mDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Calendar getDate() {
        return mDate;
    }

    public void setDate(Calendar mDate) {
        this.mDate = mDate;
    }

}
