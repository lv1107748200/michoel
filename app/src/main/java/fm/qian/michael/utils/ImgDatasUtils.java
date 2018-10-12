package fm.qian.michael.utils;

import java.util.Random;

/**
 * Created by owen on 2017/6/22.
 */

public class ImgDatasUtils {
    private static Random random = new Random();
    private static String[] imgs = new String[]{
            "http://pic4.nipic.com/20091113/2847083_105626034638_2.jpg",
            "http://a3.topitme.com/1/21/79/1128833621e7779211o.jpg",
            "http://fd.topitme.com/d/a8/1d/11315383988791da8do.jpg",
            "http://pic14.photophoto.cn/20100127/0036036848818577_b.jpg",
            "http://pic27.photophoto.cn/20130410/0005018344802601_b.jpg",
            "http://pic9.nipic.com/20100919/5123760_093408576078_2.jpg",
            "http://imgk.zol.com.cn/dcbbs/10430/a10429839.jpg",
            "http://img.taopic.com/uploads/allimg/140702/240404-140F20IG486.jpg",
    };
    
    public static String getUrl() {
        return imgs[random.nextInt(imgs.length)];
    }
}
