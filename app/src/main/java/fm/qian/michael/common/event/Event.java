package fm.qian.michael.common.event;


/**
 * Created by 吕 on 2018/1/22.
 */

public class Event {
    //刷新 mainActivity;
    public static class MainActivityEvent{
        private int sel;

        public int getSel() {
            return sel;
        }

        public void setSel(int sel) {
            this.sel = sel;
        }

        public MainActivityEvent(int sel) {
            this.sel = sel;
        }
    }

    //刷新下载
    public static class DownEvent{
        private String id;

        public DownEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class LoginEvent{
        //0 刷新用户信息
        //1 刷新播单
        //2 整体刷新

        private String id;

        public LoginEvent() {
        }

        public LoginEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
    //收藏

    public static class FavEvent{
        private String id;
        private int i; //1 添加 2删除

        public FavEvent() {
        }

        public FavEvent(int i,String id) {
            this.id = id;
            this.i = i;
        }

        public String getId() {
            return id;
        }

        public int getI() {
            return i;
        }

    }

    //正在播放刷新
    public static class PlayEvent{
        private String id;
        private int i; //1 播放刷新 2在播放页面下载后视图刷新 3下载中删除专辑

        public PlayEvent() {

        }

        public PlayEvent(int i) {
            this.i = i;
        }

        public PlayEvent(int i, String id) {
            this.id = id;
            this.i = i;
        }

        public String getId() {
            return id;
        }

        public int getI() {
            return i;
        }

    }
    //网络变化
    public static class NetEvent{
        private int i; //1 搜索

        public NetEvent(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}
