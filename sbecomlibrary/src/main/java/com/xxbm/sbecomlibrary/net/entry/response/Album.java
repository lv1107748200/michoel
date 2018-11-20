package com.xxbm.sbecomlibrary.net.entry.response;


import com.chad.library.adapter.base.entity.MultiItemEntity;


/*
 * lv   2018/9/17 专辑
 */
public class Album extends Base implements MultiItemEntity {
    private String brief;
    private String brief_pay;
    private String userpay;
    private String ispay;
    private String price;
    private String shareurl;
    private String isfav;//是否收藏 1是 0否

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

    public String getIsfav() {
        return isfav;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
