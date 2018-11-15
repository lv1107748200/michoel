package com.xxbm.sbecomlibrary.net.http;


/*
 * lv   2018/9/17
 */
public abstract class CallBack<T,A> {
    public abstract   void onSuccessAll(A t);
    public abstract   void onSuccess(T t);
    public abstract void onNotNet();
}
