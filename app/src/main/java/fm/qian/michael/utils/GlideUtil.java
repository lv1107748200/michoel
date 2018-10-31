package fm.qian.michael.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import fm.qian.michael.R;

/**
 * Created by 吕 on 2017/12/4.
 */

public class GlideUtil {


    public static void setGlideImage(Context context, int url, ImageView imageView){
        if(imageView == null)
            return;
        Glide.with(context)
                .load(url)//
                .into(imageView);
    }

    public static void setGlideImage(Context context, String url, ImageView imageView){
        if(imageView == null)
            return;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(url)//
                .apply(options)
                .into(imageView);
    }

    public static void setGlideImage(Context context, String url, ImageView imageView, int id){
        if(imageView == null)
            return;
        RequestOptions options = new RequestOptions()
                .placeholder(id)
                .error(id)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(url)//
                .apply(options)
                .into(imageView);
    }
    public static void setGlideImageMake(Context context, String url, ImageView imageView){

        if(imageView == null)
            return;

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.hold)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.hold)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(url)//
                .apply(options)
                .into(imageView);
    }

    public static void setGif(Context context,int gif,ImageView imageView){
        Glide.with( context ).asGif().load( gif ).into( imageView ) ;
    }
}
