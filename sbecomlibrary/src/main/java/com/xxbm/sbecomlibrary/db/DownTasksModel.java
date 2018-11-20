package com.xxbm.sbecomlibrary.db;


import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import com.xxbm.sbecomlibrary.net.entry.response.ComAll;

/*
 * lv   2018/11/6 //@ManyToMany(referencedTable = ComAll.class)

 */
@Table(database = AppDatabase.class)
public class DownTasksModel extends BaseModel implements Parcelable {

    @PrimaryKey
    private String id;
    @Column
    private String title;
    @Column
    private String cover;
    @Column
    private String brief;
    @Column
    private String sizeAll;
    @Column
    private String sizeDown;
    @Column
    private String allJson;
    @Column
    private String downJson;


    private List<ComAll> comAlls;

    public String getAllJson() {
        return allJson;
    }

    public void setAllJson(String allJson) {
        this.allJson = allJson;
    }

    public String getDownJson() {
        return downJson;
    }

    public void setDownJson(String downJson) {
        this.downJson = downJson;
    }

    public List<ComAll> getComAlls() {
        return comAlls;
    }

    public void setComAlls(List<ComAll> comAlls) {
        this.comAlls = comAlls;
    }

    public void setComAll(ComAll comAll){
        if(null != comAll){
            if(null == comAlls){
                comAlls = new ArrayList<>();
                comAlls.add(comAll);
            }else {
                comAlls.add(comAll);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getSizeAll() {
        return sizeAll;
    }

    public void setSizeAll(String sizeAll) {
        this.sizeAll = sizeAll;
    }

    public String getSizeDown() {
        return sizeDown;
    }

    public void setSizeDown(String sizeDown) {
        this.sizeDown = sizeDown;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public DownTasksModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.brief);
        dest.writeString(this.sizeAll);
        dest.writeString(this.sizeDown);
        dest.writeString(this.allJson);
        dest.writeString(this.downJson);
        dest.writeTypedList(this.comAlls);
    }

    protected DownTasksModel(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.brief = in.readString();
        this.sizeAll = in.readString();
        this.sizeDown = in.readString();
        this.allJson = in.readString();
        this.downJson = in.readString();
        this.comAlls = in.createTypedArrayList(ComAll.CREATOR);
    }

    public static final Creator<DownTasksModel> CREATOR = new Creator<DownTasksModel>() {
        @Override
        public DownTasksModel createFromParcel(Parcel source) {
            return new DownTasksModel(source);
        }

        @Override
        public DownTasksModel[] newArray(int size) {
            return new DownTasksModel[size];
        }
    };
}
