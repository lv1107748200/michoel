package fm.qian.michael.widget;


import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;

import static com.hr.bclibrary.utils.SdcardUtil.getStorageDirectory;

/*
 * lv   2018/10/30
 */
@GlideModule
public class CacheGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //定义缓存大小为100M
        int  diskCacheSize =  100 * 1024 * 1024;

        //自定义缓存 路径 和 缓存大小
        String diskCachePath = getStorageDirectory() + File.separator + "glideCache";


        builder.setDiskCache(new DiskLruCacheFactory( diskCachePath , diskCacheSize ));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }
}
