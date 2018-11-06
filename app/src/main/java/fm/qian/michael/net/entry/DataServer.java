package fm.qian.michael.net.entry;


import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class DataServer {

    private static final String HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK = "https://avatars1.githubusercontent.com/u/7698209?v=3&s=460";
    private static final String CYM_CHAD = "CymChad";
    private static final String CHAY_CHAN = "ChayChan";
    private static final String imageUrl = "https://imgazure.microsoftstore.com.cn/_ui/desktop/static/img/20180820bts/0831-0909/banner_2360.jpg";

    private DataServer() {
    }

    public static List getSampleData(int lenth) {
        List<Video> list = new ArrayList<>();
        for (int i = 0; i < lenth; i++) {
            Video status = new Video(imageUrl,"全心全意为人民服务");
            list.add(status);
        }
        return list;
    }

    public static List<MySection> getSampleData() {
        List<MySection> list = new ArrayList<>();
        list.add(new MySection(true, "Section 1", false));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(true, "Section 2", false));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(true, "Section 3", false));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(true, "Section 4", false));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(true, "Section 5", false));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        list.add(new MySection(new Video(HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK, CYM_CHAD)));
        return list;
    }

    public static List<MultipleItem> getMultipleItemData() {
        List<MultipleItem> list = new ArrayList<>();

            list.add(new MultipleItem(MultipleItem.BAN, MultipleItem.FOUR));

            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, new Video("","今日推荐")));

            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG2, MultipleItem.TWO, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG2, MultipleItem.TWO, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG2, MultipleItem.TWO, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.IMGANDTEXTG2, MultipleItem.TWO, new Video("","今日推荐")));

            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","热门文章")));

            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));
            list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR, new Video("","今日推荐")));


//            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","主题推荐")));
//            list.add(new MultipleItem(MultipleItem.RECY, MultipleItem.FOUR));
//            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","排行榜")));
//            list.add(new MultipleItem(MultipleItem.RANKING, MultipleItem.FOUR));
//            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","精彩视频")));
//            list.add(new MultipleItem(MultipleItem.RECY, MultipleItem.FOUR));
//            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video("","更多推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));
//            list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, new Video("","今日推荐")));

        return list;
    }


    public static List<String> getStrData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String str = HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK;
            if (i % 2 == 0) {
                str = CYM_CHAD;
            }
            list.add(str);
        }
        return list;
    }

}
