package com.example.hp.pocket_docket.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 10-05-2017.
 */

public class Module extends Project implements Parcelable {

    String mno;
    String mtitle;
    String mdesc;
    String mstart;
    String mend;
    String status;
    String totalTime;
    String associationId;

    public Module() {
    }

    protected Module(Parcel in) {
        mno = in.readString();
        mtitle = in.readString();
        mdesc = in.readString();
        mstart = in.readString();
        mend = in.readString();
        status = in.readString();
        totalTime = in.readString();
        associationId=in.readString();
    }

    public static final Creator<Module> CREATOR = new Creator<Module>() {
        @Override
        public Module createFromParcel(Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };

    public String getAssociationId() {
        return associationId;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String toString() {
        return mtitle;
    }

    public String getMno() {
        return mno;
    }

    public void setMno(String mno) {
        this.mno = mno;
    }

    public String getMdesc() {
        return mdesc;
    }

    public void setMdesc(String mdesc) {
        this.mdesc = mdesc;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle =mtitle;
    }

    public String getMstart() {
        return mstart;
    }

    public void setMstart(String mstart) {
        this.mstart = mstart;
    }

    public String getMend() {
        return mend;
    }

    public void setMend(String mend) {
        this.mend = mend;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mno);
        dest.writeString(mtitle);
        dest.writeString(mdesc);
        dest.writeString(mstart);
        dest.writeString(mend);
        dest.writeString(status);
        dest.writeString(totalTime);
        dest.writeString(associationId);
    }
}
