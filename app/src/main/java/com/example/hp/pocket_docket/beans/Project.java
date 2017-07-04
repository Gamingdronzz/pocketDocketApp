package com.example.hp.pocket_docket.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hp.pocket_docket.formattingAndValidation.Format;

/**
 * Created by hp on 08-05-2017.
 */

public class Project implements Parcelable {
    String id;
    String title;
    String desc;
    String start;
    String end;
    String type;

    public Project() {

    }

    public Project(String id, String title, String desc, String start, String end, String type) {
        this.id = id;
        this.title = Format.FirstLetterCaps(title);
        this.desc = Format.FirstLetterCaps(desc);
        this.start = start;
        this.end = end;
        this.type = type;
    }

    protected Project(Parcel in) {
        id = in.readString();
        title = in.readString();
        desc = in.readString();
        start = in.readString();
        end = in.readString();
        type = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Format.FirstLetterCaps(title);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = Format.FirstLetterCaps(desc);
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return Format.FirstLetterCaps(title);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(type);
    }
}
