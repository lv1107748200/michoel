package com.xxbm.sbecomlibrary.net.base;


/**
 * Created by 吕 on 2017/10/27.
 */

public class BaseResponse<O,L> {

    private int code;
    private String msg;
    private O info;
    private L list;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public O getInfo() {
        return info;
    }

    public void setInfo(O info) {
        this.info = info;
    }

    public L getList() {
        return list;
    }

    public void setList(L list) {
        this.list = list;
    }
}
