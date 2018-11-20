package com.xxbm.sbecomlibrary.net.entry.response;


/*
 * lv   2018/9/17  全部排行榜
 */
public class RankMore extends Base {


    private String cover_small;
    private String url;
    private String broad;
    private int minute;
    private int second;
    private String playnums;

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
}
