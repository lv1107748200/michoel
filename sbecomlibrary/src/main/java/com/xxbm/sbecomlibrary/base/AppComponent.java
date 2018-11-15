package com.xxbm.sbecomlibrary.base;


import com.xxbm.sbecomlibrary.base.activity.BaseActivity;
import com.xxbm.sbecomlibrary.base.fragment.BaseFragment;
import com.xxbm.sbecomlibrary.net.base.BaseService;
import com.xxbm.sbecomlibrary.net.http.HttpModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 吕 on 2017/10/26.
 */
@Singleton
@Component(
        modules = {
        AppModule.class,
        HttpModule.class
})
public interface AppComponent {
    void inject(BaseActivity baseActivity);
    void inject(BaseFragment baseFragment);
    void inject(BaseService baseService);
}
