package com.xxbm.sbecomlibrary.net.entry.response;


import java.util.List;

/*
 * lv   2018/9/17  首页 tid
 */
public class Index {

    private List<Base> swiper;//首页轮播
    private List<Base> nav;//轮播下导航
    private List<Base> today;//今日推荐
    private List<Base> topic;//主题推荐
    private List<Base> video;//精彩视频
    private String videoall;//全部的 id
    private List<Base> news;//热门文章
    private Rank rank;//排行榜

    private String firstaudio;//第一次进入 播放 id

    public List<Base> getSwiper() {
        return swiper;
    }

    public void setSwiper(List<Base> swiper) {
        this.swiper = swiper;
    }

    public List<Base> getNav() {
        return nav;
    }

    public void setNav(List<Base> nav) {
        this.nav = nav;
    }

    public List<Base> getToday() {
        return today;
    }

    public void setToday(List<Base> today) {
        this.today = today;
    }

    public List<Base> getTopic() {
        return topic;
    }

    public void setTopic(List<Base> topic) {
        this.topic = topic;
    }

    public List<Base> getVideo() {
        return video;
    }

    public void setVideo(List<Base> video) {
        this.video = video;
    }

    public String getVideoall() {
        return videoall;
    }

    public void setVideoall(String videoall) {
        this.videoall = videoall;
    }

    public List<Base> getNews() {
        return news;
    }

    public void setNews(List<Base> news) {
        this.news = news;
    }

    public String getFirstaudio() {
        return firstaudio;
    }

    public void setFirstaudio(String firstaudio) {
        this.firstaudio = firstaudio;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
