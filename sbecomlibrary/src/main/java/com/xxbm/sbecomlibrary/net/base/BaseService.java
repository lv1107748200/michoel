package com.xxbm.sbecomlibrary.net.base;


import android.os.Build;


import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.net.Service.AppService;
import com.xxbm.sbecomlibrary.net.Service.UserService;
import com.xxbm.sbecomlibrary.net.Service.WXUserService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;




/**
 * Created by 吕 on 2017/10/27.
 */

public class BaseService {

    public BaseService() {
        BaseApplation.getBaseApp().getAppComponent().inject(this);
    }
    @Inject
    public AppService appService;
    @Inject
    public WXUserService wxuserService;
    @Inject
    public UserService userService;

}
