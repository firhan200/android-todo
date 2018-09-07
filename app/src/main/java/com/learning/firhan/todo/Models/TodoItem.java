package com.learning.firhan.todo.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoItem implements Parcelable{
    private int id;
    private String title;
    private String desciption;
    private Boolean isSelected;

    public TodoItem(int id, String title, String desciption) {
        this.id = id;
        this.title = title;
        this.desciption = desciption;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(desciption);
    }

    private TodoItem(Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.desciption = in.readString();
    }

    public static final Parcelable.Creator<TodoItem> CREATOR = new Parcelable.Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
