package fm.qian.michael.base;



import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.net.base.BaseService;
import fm.qian.michael.net.http.HttpModule;

import javax.inject.Singleton;

import dagger.Component;
import fm.qian.michael.base.activity.BaseActivity;

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
