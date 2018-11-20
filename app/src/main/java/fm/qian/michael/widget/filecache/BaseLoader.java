package fm.qian.michael.widget.filecache;

import com.xxbm.sbecomlibrary.encode.MD5;
import com.xxbm.sbecomlibrary.utils.NLog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.util.List;


/**
 * Created by Fussen on 2017/12/20.
 */

public abstract class BaseLoader<D> implements ModelLoader<D> {



    protected DiskLruCache mDiskLruCache;

    public BaseLoader(DiskLruCache mDiskLruCache) {

        this.mDiskLruCache = mDiskLruCache;
    }


    @Override
    public boolean saveCache(String key, D data) {
        return false;
    }

    @Override
    public void saveImage(String imageUrl) {
    }

    @Override
    public <D> D getImage(String imageUrl) {
        return null;
    }

    @Override
    public <D> D getObjCache(String key, Class<D> cls) {
        return null;
    }

    @Override
    public <D> List<D> getCacheList(String key, Class<D> cls) {
        return null;
    }


    @Override
    public boolean remove(String key) {

        if (mDiskLruCache != null) {
            try {
                return mDiskLruCache.remove(getKey(key));
            } catch (IOException e) {
                e.printStackTrace();
                NLog.e(NLog.TAGOther,"===========remove failed ===========");
            }
        }
        return false;
    }

    @Override
    public void clear() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
                NLog.e(NLog.TAGOther, "===========clear failed ===========");
            }
        }
    }


    protected String getKey(String kind) {


        String fileName = "cache_" + kind;
        return MD5.encodeKey(fileName);
    }

}
