package com.xxbm.sbecomlibrary.utils;

import java.util.List;
import java.util.Map;

/**
 * 检测数据。。。
 */
public class CheckUtil {
    public CheckUtil() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(Object object) {
        return object == null;
    }

    public static boolean isEmpty(Object[] object) {
        return object == null || object.length == 0;
    }
}
