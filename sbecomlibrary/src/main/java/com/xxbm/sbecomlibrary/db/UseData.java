package com.xxbm.sbecomlibrary.db;


import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.xxbm.sbecomlibrary.utils.CheckUtil;

/*
 * lv   2018/9/21  用于保存 应用的使用产生的数据变化
 */
@Table(database = AppDatabase.class)
public class UseData extends BaseModel implements Parcelable {
    public final static int USEID = 10012;
    private static UseData useData;


    @PrimaryKey
    private int id;
    @Column(defaultValue = "0")
    private String type;//关键字段 决定到播放页面的操作
    @Column
    private String name;//用于在播放页面显示
    @Column
    private String ZBId;//专辑 播单 id
    @Column
    private String DId;//单曲 id
    @Column
    private String playID;//正在播放资源id
    @Column
    private int timeing;//记录定时
    @Column
    private int pattern;//记录播放模式
    @Column
    private String historyId;//播放记录
    @Column
    private long progress;//播放进度
    @Column
    private String bindWx;//绑定微信
    @Column
    private String userName;//手机号
    @Column
    private String sessionkey;//手机号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeing() {
        return timeing;
    }

    public  void setTimeing(int timeing) {
        this.timeing = timeing;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZBId() {
        return ZBId;
    }

    public void setZBId(String ZBId) {
        this.ZBId = ZBId;
    }

    public String getDId() {
        return DId;
    }

    public void setDId(String DId) {
        this.DId = DId;
    }

    public String getPlayID() {
        return playID;
    }

    public void setPlayID(String playID) {
        this.playID = playID;
    }

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getBindWx() {
        return bindWx;
    }

    public void setBindWx(String bindWx) {
        this.bindWx = bindWx;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public static  void setSTimeing(int timeing) {
        UseData useData = getUseData();
        useData.setTimeing(timeing);
        useData.update();
    }

    public static void  setLogin(String UserName,String Sessionkey,String bindWx){
        UseData useData = getUseData();
            useData.userName = UserName;
            useData.sessionkey = Sessionkey;
        if(!CheckUtil.isEmpty(bindWx))
            useData.bindWx = bindWx;

        useData.update();
    }

    public static  void  setWhat(String type,
                   String name,
                   String ZBId,
                   String DId,
                   String playID,
                   int timeing,
                   int pattern,
                   String historyId,
                   long progress
    ) {
        UseData useData = getUseData();

        if(!CheckUtil.isEmpty(type))
        useData.type = type;
        if(!CheckUtil.isEmpty(name))
        useData.name = name;
        if(!CheckUtil.isEmpty(ZBId))
        useData.ZBId = ZBId;
        if(!CheckUtil.isEmpty(DId))
        useData.DId = DId;
        if(!CheckUtil.isEmpty(playID))
        useData.playID = playID;
        if(-1 != timeing)
        useData.timeing = timeing;
        if(-1 != pattern)
        useData.pattern = pattern;
        if(!CheckUtil.isEmpty(historyId))
        useData.historyId = historyId;
        if(-1 != progress)
        useData.progress = progress;

        useData.update();
    }

    public static void setInit(String What){
        UseData.setWhat(
                What,
                null,
                null,
                null,
                null,
                -1,
                -1,
                null,
                -1
        );
    }

    public static void setUseData(UseData useData){
            if(null != useData){
                if(getUseData() != null){
                    useData.update();
                }
            }
    }

    //获取数据库数据实体
    public static UseData getUseData(){
        useData = SQLite.select()
                .from(UseData.class)
                .where(UseData_Table.id.eq(USEID)).querySingle();

        if(null == useData){
            useData = new UseData();
            useData.setId(USEID);
            useData.setTimeing(0);
            useData.setType("0");
            useData.save();
        }

        return useData;
    }

    public UseData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.ZBId);
        dest.writeString(this.DId);
        dest.writeString(this.playID);
        dest.writeInt(this.timeing);
        dest.writeInt(this.pattern);
        dest.writeString(this.historyId);
        dest.writeLong(this.progress);
        dest.writeString(this.bindWx);
        dest.writeString(this.userName);
        dest.writeString(this.sessionkey);
    }

    protected UseData(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
        this.name = in.readString();
        this.ZBId = in.readString();
        this.DId = in.readString();
        this.playID = in.readString();
        this.timeing = in.readInt();
        this.pattern = in.readInt();
        this.historyId = in.readString();
        this.progress = in.readLong();
        this.bindWx = in.readString();
        this.userName = in.readString();
        this.sessionkey = in.readString();
    }

    public static final Creator<UseData> CREATOR = new Creator<UseData>() {
        @Override
        public UseData createFromParcel(Parcel source) {
            return new UseData(source);
        }

        @Override
        public UseData[] newArray(int size) {
            return new UseData[size];
        }
    };
}
