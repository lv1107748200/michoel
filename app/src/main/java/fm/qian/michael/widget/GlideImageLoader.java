package fm.qian.michael.widget;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import fm.qian.michael.R;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;
import com.youth.banner.loader.ImageLoader;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择

        if(path instanceof Base){
            GlideUtil.setGlideImageMake(context,((Base) path).getCover(),imageView);
        }


    }

    @Override
    public ImageView createImageView(Context context) {

//        SelectableRoundedImageView selectableRoundedImageView  = new SelectableRoundedImageView(context);
//        selectableRoundedImageView.setCornerRadiiDP(4,4,4,4);
//        //圆角

        ImageView imageView = new ImageView(context);
       // imageView.setBackgroundResource(R.color.color_F5F);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
