package fm.qian.michael.widget.filecache;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.util.LruCache;

import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.NLog;
import com.hr.bclibrary.utils.SdcardUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by 吕 on 2017/12/9.
 */

public class FileCacheManger {
    //使用volatile关键字保其可见性
    volatile private static FileCacheManger instance = null;


    public static FileCacheManger getInstance() {
            if(instance == null){
                synchronized (FileCacheManger.class) {
                    if(instance == null){
                        instance = new FileCacheManger();
                    }
                }
            }

        return instance;
    }

    protected DiskLruCache mDiskLruCache;
    private  ModelLoader<Object> objectModelLoader ;


    public void init(Context context){
        String cachePath = SdcardUtil.getDiskFileDir(context) + File.separator + "historyCache" ;
        File cacheDir = new File(cachePath);
        if(null == cacheDir){
            return;
        }

        if (!cacheDir.exists()) {
            NLog.e(NLog.TAGOther,"创建播放记录缓存路径 path--->");
            cacheDir.mkdirs();
        }
        NLog.e(NLog.TAGOther,"path--->"+cacheDir.getAbsolutePath());
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, SdcardUtil.getAppVersion(context), 1, 10 * 1024 * 1024);
            NLog.e(NLog.TAGOther,"......create DiskLruCache......");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fromObj();
    }

    private synchronized <D> D getCache(String key, Class<D> cls) {
        if(null == objectModelLoader){
            return null;
        }
        return objectModelLoader.getObjCache(key, cls);

    }
    private synchronized <D> boolean saveCache(String key, D object) {

        if(null == objectModelLoader){
            return false;
        }

        return objectModelLoader.saveCache(key, object);
    }

    private synchronized <D> List<D> getList(String key, Class<D> cls){
        if(null == objectModelLoader){
            return null;
        }
       return objectModelLoader.getCacheList(key,cls);
    }

    private void remove(String key){
        if(null == objectModelLoader){
            return ;
        }
        objectModelLoader.remove(key);
    }

    public static boolean set(final String key, final Object object){
       return getInstance().saveCache(key,object);
    }

    public void setAndRe(final String key, final Object object){
        // FIXME: 2018/2/9 未加内存缓存
                remove(key);
                boolean is =  saveCache(key,object);

    }
    public static List get(final String key, final Class<?> cls){
        List list = getInstance().getList(key,cls);

        return list;
    }


    private ModelLoader<Object> fromObj() {
        if(null == mDiskLruCache){
            return null;
        }
         objectModelLoader = new ObjectLoader(mDiskLruCache);

        if (objectModelLoader == null) {
            throw new IllegalArgumentException("Unknown type obj . can't be save or nonsupport this type cache!");
        }

        return objectModelLoader;
    }

    public static void close(){
        if(null != getInstance().mDiskLruCache){
            try {
                getInstance().mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
