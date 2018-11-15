package com.xxbm.sbecomlibrary.net.http;

public class HttpException extends Exception {

    private int code ;
    private String msg ;

    public HttpException() {
        super();
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
