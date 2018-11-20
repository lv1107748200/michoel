package com.xxbm.sbecomlibrary.db;


/*
 * lv   2018/9/14
 * public final static String ID = "id";
    public final static String NAME = "name";
    public final static String URL = "url";
    public final static String PATH = "path";
 */

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = AppDatabase.class)
public class TasksManagerModel extends BaseData {

    @PrimaryKey(autoincrement = true)
    private int Idd;
    @Column
    private int id;
    @Column
    private String name;
    @Column
    private String url;
    @Column
    private String urlImg;
    @Column
    private String path;

    public int getIdd() {
        return Idd;
    }

    public void setIdd(int idd) {
        Idd = idd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
