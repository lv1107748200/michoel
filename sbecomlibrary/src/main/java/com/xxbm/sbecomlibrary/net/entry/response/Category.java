package com.xxbm.sbecomlibrary.net.entry.response;


import java.util.List;

/*
 * lv   2018/9/17  分类
 */
public class Category  {
      private String name;
      private List<Base> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Base> getList() {
        return list;
    }

    public void setList(List<Base> list) {
        this.list = list;
    }
}
