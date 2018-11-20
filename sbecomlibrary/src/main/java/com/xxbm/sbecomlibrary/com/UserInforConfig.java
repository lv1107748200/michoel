package com.xxbm.sbecomlibrary.com;

/**
 * Created by 吕 on 2017/12/6.
 */

public enum UserInforConfig {
    USERNAME("userName"),//姓名
    USERLOGO("logo"),//头像
    USERNICKNAME("NickName"),//昵称
    USERSESSIONKEY("sessionkey"),//sessionkey
    USERBINDWX("bindWx"),//绑定微信

    USERPASSWORD("userPassword"),
    USERCHANCETIME("userChanceTime"),//还可答题次数
    USERFIRSTLOAD("firstLoad"),//第一次打开
    USERFIRSTCECHA("firstcecha"),//第一次缓存

    USERFIRSTAUDIO("firstaudio"),//第一次执行播放

    USERMUSICTYPE("musicType"),//类型  专辑  我的播单 一首歌
    USERMUSICNAME("musicName"),//名字
    USERMUSICID("musicId"),//对应类型的id

    USERPLAYId("PLAYId"),//暂停后播放的的id
    USERPLAYSEEK("PLAYSEEK"),//暂停后的进度

    USERTMEING("type");//类型




    private String name;

    UserInforConfig(String name) {
        this.name = name;
    }

    public static String getName(String index) {
        for (UserInforConfig c : UserInforConfig.values()) {
            if (c.getName().equals(index)) {
                return c.name;
            }
        }
        return null;
    }
    public String getName() {
        return name;
    }


}
