package fm.qian.michael.widget.single;


import android.content.Context;
import android.widget.ImageView;

import com.xxbm.sbecomlibrary.utils.CheckUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import com.xxbm.musiclibrary.MusicPlayerManger;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;

/*
 * lv   2018/11/8
 */
public class PlayGifManger {

//    volatile private static PlayGifManger instance = null;

    private static Map<String,ImageView> imageViewMap;

    static {
        imageViewMap = new HashMap<>();
    }

//    public static PlayGifManger getInstance() {
//        if(instance == null){
//            synchronized (PlayGifManger.class) {
//                if(instance == null){
//                    instance = new PlayGifManger();
//                }
//            }
//        }
//        return instance;
//    }

    public static void add(String key, ImageView imageView, Context context){
        if(null != imageViewMap){
            imageViewMap.put(key,imageView);
        }

        if(MusicPlayerManger.isPlaying()){
            GlideUtil.setGif(context, R.mipmap.playing,imageView);
        }else {
            GlideUtil.setGlideImage(context, R.drawable.pause,imageView);
        }
    }

    public static void remove(String key){

        if(!CheckUtil.isEmpty(imageViewMap)){
            imageViewMap.remove(key);
            NLog.e(NLog.TAGOther," key :  " + key + " size : " + imageViewMap.size());
        }
    }


    public static void updata(){

        if(null != imageViewMap){
            Iterator<Map.Entry<String, ImageView>> iterator = imageViewMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ImageView> entry = iterator.next();
                ImageView value = entry.getValue();
                if(MusicPlayerManger.isPlaying()){
                    GlideUtil.setGif(BaseApplation.getBaseApp(), R.mipmap.playing,value);
                }else {
                    GlideUtil.setGlideImage(BaseApplation.getBaseApp(), R.drawable.pause,value);
                }
            }
        }

    }
    public static void updataPause(){

        if(null != imageViewMap){
            Iterator<Map.Entry<String, ImageView>> iterator = imageViewMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ImageView> entry = iterator.next();
                ImageView value = entry.getValue();

                GlideUtil.setGlideImage(BaseApplation.getBaseApp(), R.drawable.pause,value);

            }
        }

    }

    public static void updataPlay(){

        if(null != imageViewMap){
            Iterator<Map.Entry<String, ImageView>> iterator = imageViewMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ImageView> entry = iterator.next();
                ImageView value = entry.getValue();

                GlideUtil.setGif(BaseApplation.getBaseApp(), R.mipmap.playing,value);

            }
        }

    }
}
