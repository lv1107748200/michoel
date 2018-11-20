package com.xxbm.sbecomlibrary.net.entry.response;



import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.xxbm.sbecomlibrary.db.AppDatabase;

import java.io.Serializable;


/*
 * lv   2018/9/17 通用版
 */
@Table(database = AppDatabase.class)
public class ComAll extends BaseModel implements Serializable,Parcelable ,MultiItemEntity {

    @PrimaryKey
    private String id;
    @Column
    private String tid;
    @Column
    private String zid;
    @Column
    private String cover;
    @Column
    private String title;
    @Column
    private String addtime;
    @Column
    private String pubdate;
    @Column
    private String name;
    @Column
    private String brief;
    @Column
    private String brief_pay;
    @Column
    private String userpay;
    @Column
    private String ispay;
    @Column
    private String price;
    @Column
    private String shareurl;
    @Column
    private String cover_small;
    @Column
    private String url;
    @Column
    private String broad;
    @Column
    private int minute;
    @Column
    private int second;
    @Column
    private String playnums;
    @Column
    private String istest;//用于专辑
    @Column
    private String wxurl;
    @Column
    private String author;
    @Column
    private int isDown;//0 1 2
    @Column
    private String downPath;//下载保存路径
    @Column
    private String nums;
    @Column
    private String albumId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getBrief_pay() {
        return brief_pay;
    }

    public void setBrief_pay(String brief_pay) {
        this.brief_pay = brief_pay;
    }

    public String getUserpay() {
        return userpay;
    }

    public void setUserpay(String userpay) {
        this.userpay = userpay;
    }

    public String getIspay() {
        return ispay;
    }

    public void setIspay(String ispay) {
        this.ispay = ispay;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl;
    }

    public String getCover_small() {
        return cover_small;
    }

    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBroad() {
        return broad;
    }

    public void setBroad(String broad) {
        this.broad = broad;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getPlaynums() {
        return playnums;
    }

    public void setPlaynums(String playnums) {
        this.playnums = playnums;
    }

    public String getIstest() {
        return istest;
    }

    public void setIstest(String istest) {
        this.istest = istest;
    }

    public String getWxurl() {
        return wxurl;
    }

    public void setWxurl(String wxurl) {
        this.wxurl = wxurl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIsDown() {
        return isDown;
    }

    public void setIsDown(int isDown) {
        this.isDown = isDown;
    }

    public String getDownPath() {
        return downPath;
    }

    public void setDownPath(String downPath) {
        this.downPath = downPath;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
    public ComAll() {
    }

    @Override
    public int getItemType() {
        return 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.tid);
        dest.writeString(this.zid);
        dest.writeString(this.cover);
        dest.writeString(this.title);
        dest.writeString(this.addtime);
        dest.writeString(this.pubdate);
        dest.writeString(this.name);
        dest.writeString(this.brief);
        dest.writeString(this.brief_pay);
        dest.writeString(this.userpay);
        dest.writeString(this.ispay);
        dest.writeString(this.price);
        dest.writeString(this.shareurl);
        dest.writeString(this.cover_small);
        dest.writeString(this.url);
        dest.writeString(this.broad);
        dest.writeInt(this.minute);
        dest.writeInt(this.second);
        dest.writeString(this.playnums);
        dest.writeString(this.istest);
        dest.writeString(this.wxurl);
        dest.writeString(this.author);
        dest.writeInt(this.isDown);
        dest.writeString(this.downPath);
        dest.writeString(this.nums);
        dest.writeString(this.albumId);
    }

    protected ComAll(Parcel in) {
        this.id = in.readString();
        this.tid = in.readString();
        this.zid = in.readString();
        this.cover = in.readString();
        this.title = in.readString();
        this.addtime = in.readString();
        this.pubdate = in.readString();
        this.name = in.readString();
        this.brief = in.readString();
        this.brief_pay = in.readString();
        this.userpay = in.readString();
        this.ispay = in.readString();
        this.price = in.readString();
        this.shareurl = in.readString();
        this.cover_small = in.readString();
        this.url = in.readString();
        this.broad = in.readString();
        this.minute = in.readInt();
        this.second = in.readInt();
        this.playnums = in.readString();
        this.istest = in.readString();
        this.wxurl = in.readString();
        this.author = in.readString();
        this.isDown = in.readInt();
        this.downPath = in.readString();
        this.nums = in.readString();
        this.albumId = in.readString();
    }

    public static final Creator<ComAll> CREATOR = new Creator<ComAll>() {
        @Override
        public ComAll createFromParcel(Parcel source) {
            return new ComAll(source);
        }

        @Override
        public ComAll[] newArray(int size) {
            return new ComAll[size];
        }
    };
}
