package com.xxbm.sbecomlibrary.net.entry.response;


import java.util.List;

/*
 * lv   2018/9/17  排行榜
 */
public class Rank {
    private List<Base> day7audio;//本周故事
    private List<Base> day30audio;//本月
    private List<Base> month7album;//本周专辑
    private List<Base> month30album;//本月

    public List<Base> getDay7audio() {
        return day7audio;
    }

    public void setDay7audio(List<Base> day7audio) {
        this.day7audio = day7audio;
    }

    public List<Base> getDay30audio() {
        return day30audio;
    }

    public void setDay30audio(List<Base> day30audio) {
        this.day30audio = day30audio;
    }

    public List<Base> getMonth7album() {
        return month7album;
    }

    public void setMonth7album(List<Base> month7album) {
        this.month7album = month7album;
    }

    public List<Base> getMonth30album() {
        return month30album;
    }

    public void setMonth30album(List<Base> month30album) {
        this.month30album = month30album;
    }
}
