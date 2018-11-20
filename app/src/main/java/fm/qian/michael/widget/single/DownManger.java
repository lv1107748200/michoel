package fm.qian.michael.widget.single;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.xxbm.sbecomlibrary.utils.SdcardUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.DownTasksModel;
import com.xxbm.sbecomlibrary.db.DownTasksModelAndComAll;
import com.xxbm.sbecomlibrary.db.DownTasksModelAndComAll_Table;
import com.xxbm.sbecomlibrary.db.DownTasksModel_Table;
import com.xxbm.sbecomlibrary.db.TasksManagerModel;
import com.xxbm.sbecomlibrary.db.TasksManagerModel_Table;
import com.xxbm.sbecomlibrary.net.entry.response.Album;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll_Table;
import com.xxbm.sbecomlibrary.net.http.HttpUtils;
import fm.qian.michael.service.MqService;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.ImgDatasUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NetStateUtils;
import fm.qian.michael.utils.NotificationUitls;
import fm.qian.michael.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static fm.qian.michael.common.UserInforConfig.USERFIRSTLOAD;


/**
 * Created by 吕 on 2017/12/9.
 */

public class DownManger {
    //使用volatile关键字保其可见性
    volatile private static DownManger instance = null;

    private static boolean isC = true;


    private FileDownloadListener downloadImageListener;
    private FileDownloadSampleListener taskDownloadListener;
    private Handler mainHandler;
    private Handler workHandler;
    private HandlerThread mWorkThread;
    private static SparseArray<BaseDownloadTask> taskSparseArray;//下载的任务列表
    private FileDownloadQueueSet queueSet;
    private BaseQuickAdapter baseQuickAdapter;

    public static DownManger getInstance() {
            if(instance == null){
                synchronized (DownManger.class) {
                    if(instance == null){
                        instance = new DownManger();
                    }
                }
            }
        return instance;
    }
    public void init(){
        mWorkThread = new HandlerThread("CacheManger");
        mWorkThread.start();
        workHandler = new Handler(mWorkThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static SparseArray<BaseDownloadTask> getTaskSparseArray() {
        if(null == taskSparseArray){
            taskSparseArray =  new SparseArray<BaseDownloadTask>();
        }
        return taskSparseArray;
    }
    public static SparseArray<BaseDownloadTask> getTaskSparseArray(boolean isTu) {
        if(isTu){
            taskSparseArray =  new SparseArray<BaseDownloadTask>();
        }
        return taskSparseArray;
    }

    public static void bindService(){
        if(getInstance().workHandler == null){
            getInstance().init();
        }
        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!FileDownloader.getImpl().isServiceConnected()) {
                    FileDownloader.getImpl().bindService();
                }
            }
        });
    }

    //任务管理
    public static void setIdAndPath(final List<ComAll> comAllList, final Album album, final List<ComAll> list,
                                    final FileDownloadListener fileDownloadListener,
                                    final ResultCallback resultCallback){

        if(getInstance().workHandler == null){
            getInstance().init();
        }

        if(CheckUtil.isEmpty(list))
            return ;

        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {

                FileDownloader.getImpl().pause(getInstance().getDownloadListener());
                FileDownloader.getImpl().pause(getInstance().getDownloadImageListener());

                DownTasksModel downTasksModel = null;
//                List<DownTasksModelAndComAll> isAddComAll = null;
                if(null != album){
//                    isAddComAll = new ArrayList<>();

                    downTasksModel = SQLite.select()
                            .from(DownTasksModel.class)
                            .where(DownTasksModel_Table.id.eq(album.getId()))
                            .querySingle();

                    List<String> stringList = new ArrayList<>();
                    for(int i =0 , j = comAllList.size(); i<j; i++){
                        stringList.add(comAllList.get(i).getId());
                    }
                    if(downTasksModel != null){
//                        downTasksModel.setAllJson(HttpUtils.getStringValue(stringList));
//                        downTasksModel.update();
                    }else {
                        downTasksModel = new DownTasksModel();
                        downTasksModel.setId(album.getId());
                        downTasksModel.setTitle(album.getTitle());
                        downTasksModel.setBrief(album.getBrief());
                        downTasksModel.setCover(album.getCover());
                        downTasksModel.setSizeAll(comAllList.size()+"");
                        downTasksModel.setAllJson(HttpUtils.getStringValue(stringList));
                        downTasksModel.save();
                    }

                    getInstance().setDownImage(album.getCover());//添加专辑图片下载
                }


                for(ComAll comAll: list){

                    String url = comAll.getUrl();

                    if(CheckUtil.isEmpty(url))
                        return;

                    String path = getInstance().createPath(url);
                    if(CheckUtil.isEmpty(path))
                        return;

                    int id = FileDownloadUtils.generateId(url, path);

                  //  NLog.e(NLog.TAGDOWN,"特殊状态 path --- " + path );

                    ComAll comAll1 =  SQLite.select()
                            .from(ComAll.class)
                            .where(ComAll_Table.id.eq(comAll.getId()))
                            .querySingle();
                    if(comAll1 != null){

                    }else {
                        comAll.setIsDown(id);
                        comAll.setDownPath(path);
                        comAll.save();
                    }

                    if(null != downTasksModel){

                        String name = downTasksModel.getId() + comAll.getId();

                        DownTasksModelAndComAll modelAndComAll =  SQLite.select()
                                .from(DownTasksModelAndComAll.class)
                                .where(DownTasksModelAndComAll_Table.id.eq(name))
                                .querySingle();

                        if(null != modelAndComAll){

                        }else {
                            DownTasksModelAndComAll downTasksModel_comAll = new DownTasksModelAndComAll();
                            downTasksModel_comAll.setId(name);
                            if(null != comAll1){
                                downTasksModel_comAll.setComAll(comAll1);
                            }else {
                                downTasksModel_comAll.setComAll(comAll);
                            }
                            downTasksModel_comAll.setDownTasksModel(downTasksModel);
                            downTasksModel_comAll.save();
                        }



//                        if(CheckUtil.isEmpty(isAddComAll)){
//                            isAddComAll = SQLite.select()
//                                    .from(DownTasksModel_ComAll.class)
//                                    .where(DownTasksModel_ComAll_Table.downTasksModel_id.eq(downTasksModel.getId()))
//                                    .queryList();
//                        }
//
//                        boolean isSave = true;
//
//                        if(!CheckUtil.isEmpty(isAddComAll)){
//                            for(DownTasksModel_ComAll modelComAll: isAddComAll){
//
//                                if(comAll.getId().equals(modelComAll.getComAll().getId())){
//                                    isSave = false;
//                                    break;
//                                }
//
//                            }
//                        }
//
//                        if(isSave){
//                            DownTasksModel_ComAll downTasksModel_comAll = new DownTasksModel_ComAll();
//                            if(null != comAll1){
//                                downTasksModel_comAll.setComAll(comAll1);
//                            }else {
//                                downTasksModel_comAll.setComAll(comAll);
//                            }
//                            downTasksModel_comAll.setDownTasksModel(downTasksModel);
//
//                            downTasksModel_comAll.save();
//                        }
                    }

                }

                List<ComAll> listTask =  SQLite.select()
                        .from(ComAll.class)
                        .queryList();

                if(null != listTask){
                    for(ComAll tasksManagerModel : listTask){

                        String url = tasksManagerModel.getUrl();
                        String path = tasksManagerModel.getDownPath();
                        int id = tasksManagerModel.getIsDown();

                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {

                            BaseDownloadTask task = FileDownloader.getImpl().create(url)
                                    .setListener(getInstance().taskDownloadListener)
                                    .setPath(path);
                            // NLog.e(NLog.TAGDOWN,"特殊状态 path --- " + path );
                            getInstance().getTaskSparseArray().put(task.getId(),task);

                            task.asInQueueTask()
                                    .enqueue();

                        }
                        getInstance().setDownImage(tasksManagerModel.getCover());//故事图片大图
                        getInstance().setDownImage(tasksManagerModel.getCover_small());//故事图片小图
                    }
                }

                if(isC){//串行
                    FileDownloader.getImpl().start(getInstance().taskDownloadListener, true);
                }else {//并行
                    FileDownloader.getImpl().start(getInstance().taskDownloadListener, false);
                }
                FileDownloader.getImpl().start(getInstance().downloadImageListener,false);

                if(null != resultCallback){
                    resultCallback.onCallbackSuccess("成功添加下载任务");
                }

            }
        });
    }

    private void setDownImage(String url){

        if(!CheckUtil.isEmpty(url)){

            String imagePath = createImagePath(url);
            int id = FileDownloadUtils.generateId(url, imagePath);

            if(isDownStatus(id,imagePath) != FileDownloadStatus.completed) {
                BaseDownloadTask task = FileDownloader.getImpl().create(url)
                        .setCallbackProgressTimes(0)
                        .setListener(getInstance().downloadImageListener)
                        .setPath(imagePath);

                task.asInQueueTask()
                        .enqueue();
            }
        }

    }



    public static boolean addListTask(final List<ComAll> list, final ResultCallback resultCallback){

        if(getInstance().workHandler == null){
            getInstance().init();
        }
        if(CheckUtil.isEmpty(list))
            return false;
        NLog.e(NLog.TAGDOWN,"服务是否连接--- " +FileDownloader.getImpl().isServiceConnected());

        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {

                FileDownloader.getImpl().pause(getInstance().getDownloadListener());


                for (int i = 0; i < list.size(); i++) {



                }

                if(isC){//串行
                    FileDownloader.getImpl().start(getInstance().taskDownloadListener, true);
                }else {//并行
                    FileDownloader.getImpl().start(getInstance().taskDownloadListener, false);
                }

                if(null != resultCallback){
                    resultCallback.onCallbackSuccess("成功添加下载任务");
                }

            }
        });

        return true;
    }



    //爆粗播放列表
    public static void saveListComAll(final List<ComAll> comAlls,final ResultCallback resultCallback){

    //    SPUtils.putString(USERFIRSTLOAD,"已经不是了",false);

        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {


                Delete.table(ComAll.class); //清空之前历史纪录

                for (ComAll comAll : comAlls){
                    comAll.save();
                }

                if(null != resultCallback)
                resultCallback.onCallbackSuccess(comAlls);
            }
        });

    }

    //删除 list  源文件

    public static void delListFile(final List<ComAll> comAlls,final ResultCallback resultCallback){
        if(getInstance().workHandler == null){
            getInstance().init();
        }

        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {

                for (ComAll comAll : comAlls){

                    String path = comAll.getDownPath();
                    int id = comAll.getIsDown();
                    int statue = DownManger.isDownStatus(id,path);

                    if(statue == FileDownloadStatus.completed){
                        new File(path).delete();
                    }

                    List<DownTasksModelAndComAll> downTasksModel_comAlls
                            = SQLite.select()
                            .from(DownTasksModelAndComAll.class)
                            .where(DownTasksModelAndComAll_Table.comAll_id.eq(comAll.getId()))
                            .queryList();

                    if(!CheckUtil.isEmpty(downTasksModel_comAlls)){
                        for(DownTasksModelAndComAll downTasksModel_ComAll : downTasksModel_comAlls){
                            downTasksModel_ComAll.delete();
                        }
                    }
                    comAll.delete();

                }

                if(null != resultCallback)
                    resultCallback.onCallbackSuccess("");
            }
        });

    }

    //设置提醒
    public static void setNoftion(final int id){

               final TasksManagerModel tasksManagerModel =  SQLite.select()
                        .from(TasksManagerModel.class)
                        .where(TasksManagerModel_Table.id.eq(id))
                        .querySingle();

                if(null != tasksManagerModel){
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .error(fm.qian.michael.R.mipmap.logo)
                            .priority(Priority.HIGH);
                    Glide.with(BaseApplation.getBaseApp())
                            .asBitmap()
                            .apply(options)
                            .load(tasksManagerModel.getUrlImg())
                            .into(new SimpleTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            NotificationUitls.showNotification(new NotificationUitls.NFData(
                                    BaseApplation.getBaseApp(),tasksManagerModel.getName(),resource)
                            );
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                            NotificationUitls.showNotification(new NotificationUitls.NFData(
                                    BaseApplation.getBaseApp(),tasksManagerModel.getName(),( (BitmapDrawable) errorDrawable).getBitmap())
                            );
                        }
                    });
                }

    }

    public static void setImageView(ImageView imageView , String url, Context mFontext){

        if(NetStateUtils.isNetworkConnected(mFontext)){
            GlideUtil.setGlideImageMake(mFontext, url,
                    imageView);
        }else {
            String pathImage = DownManger.createImagePath(url);
            int idImage = FileDownloadUtils.generateId(url, pathImage);
            int statueImage = DownManger.isDownStatus(idImage, pathImage);

            if (statueImage == FileDownloadStatus.completed) {
                //NLog.e(NLog.TAGDOWN," 图下载path --->" + pathImage);
                GlideUtil.setGlideImageMake(mFontext,pathImage,
                        imageView);
            }else {
                GlideUtil.setGlideImageMake(mFontext, url,
                        imageView);
            }
        }

    }

    public static boolean isDownloaded(String url){

         String path = getInstance().createPath(url);

        int status = FileDownloader.getImpl().getStatus(url, path);

        if(status == FileDownloadStatus.completed){
            return true;
        }

        return false;
    }

    public static int isDownStatus(String url){
        String path = getInstance().createPath(url);
        int status = FileDownloader.getImpl().getStatus(url, path);
       return status;
    }
    public static int isDownStatus(final int id, String path){
        int status = FileDownloader.getImpl().getStatus(id, path);
      //  NLog.e(NLog.TAGDOWN,"下载状态 status 和 id--- " + status +"  "+id);
        return status;
    }
    public static int getStatusIgnoreCompleted(int downloadId){
        int status =  FileDownloader.getImpl().getStatusIgnoreCompleted(downloadId);
        NLog.e(NLog.TAGDOWN,"特殊状态 status --- " + status );
        return status;
    }
    public static int isDownImageStatus(String url){
        String path = getInstance().createImagePath(url);
        int status = FileDownloader.getImpl().getStatus(url, path);
        return status;
    }
    public static  String getDownID(String url){
        String path = getInstance().createPath(url);
        int id = FileDownloadUtils.generateId(url, path);
        return id+"";
    }

    public static void getTasksList(final ResultCallback<List<TasksManagerModel>> resultCallback){
        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {
              List<TasksManagerModel> tasksManagerModels =  SQLite.select()
                        .from(TasksManagerModel.class)
                        .queryList();

                if(null != resultCallback){
                    resultCallback.onCallbackSuccess(tasksManagerModels);
                }
            }
        });
    }

    public static void close(){
        if(null == instance)
            return ;

        NLog.e(NLog.TAGDOWN,"销毁下载管理器 --- ");

        FileDownloader.getImpl().unBindService();
        FileDownloadMonitor.releaseGlobalMonitor();

       // FileDownloader.getImpl().pauseAll();//暂停所有下载任务
        if(instance.getTaskSparseArray() != null){
            instance.getTaskSparseArray().clear();
        }
        if(instance.queueSet != null){
            instance.queueSet = null;
        }
        if(instance.taskDownloadListener != null){
            instance.taskDownloadListener = null;
        }

        isC = true;

        //SQLite.delete(TasksManagerModel.class);

        if(null != instance.mWorkThread){
            instance.mWorkThread.quit();
            instance. mWorkThread = null;
        }
        if(null != instance.mainHandler){
            instance.mainHandler = null;
        }
        if(null != instance.workHandler){
            instance.workHandler = null;
        }

    }

    public static void clear(){
        if(null == instance)
            return ;
        FileDownloader.getImpl().clearAllTaskData();//清空filedownloader数据库中的所有数据
    }



    public static void updateViewHolder(final int id, final BaseDownViewHolder holder) {

            if(getInstance().getTaskSparseArray() == null || getInstance().getTaskSparseArray().size() == 0)
                return;

            final BaseDownloadTask task = getInstance().getTaskSparseArray().get(id);
            if (task == null) {
                return;
            }

            task.setTag(holder);
    }
    public static void updateViewHolder(final int id) {

        if(getInstance().getTaskSparseArray() == null || getInstance().getTaskSparseArray().size() == 0)
            return;
            final BaseDownloadTask task = getInstance().getTaskSparseArray().get(id);
            if (task == null) {
                return;
            }

            task.setTag(null);

    }
    public static void removeTaskForViewHolder(final int id) {

            if(getInstance().getTaskSparseArray() == null)
                return;
            getInstance().getTaskSparseArray().remove(id);

        NLog.e(NLog.TAGDOWN,"从任务列表中删除 id --- "+ id);
        NLog.e(NLog.TAGDOWN,"删除后列表大小 size --- "+ getInstance().getTaskSparseArray().size());
    }

    public static void setQAdapter(BaseQuickAdapter quickAdapter){
        getInstance().baseQuickAdapter = quickAdapter;
    }

    private FileDownloadSampleListener getDownloadListener(){
//        synchronized (instance){
//
//        }
        if(taskDownloadListener == null){
            taskDownloadListener = new FileDownloadSampleListener() {

                private BaseDownViewHolder checkCurrentHolder(final BaseDownloadTask task) {
                    final BaseDownViewHolder tag = (BaseDownViewHolder) task.getTag();

                    if(null == tag)
                        return null;

                    if (tag.getId() != task.getId()) {
                        return null;
                    }

                    return tag;
                }

                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    super.pending(task, soFarBytes, totalBytes);
                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag == null) {
                        return;
                    }


                }

                @Override
                protected void started(BaseDownloadTask task) {
                    super.started(task);
                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag != null) {
                        if(tag.getPosition() > -1){
                            if(null != getInstance().baseQuickAdapter) {
                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                            }
                        }else {
                            tag.getView().setVisibility(View.VISIBLE);
                            tag.getView().setActivated(false);
                            tag.getTextView().setText("下载中");
                        }
                    }


                }

                @Override
                protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag != null) {

                            if(tag.getPosition() > -1){
                                if(null != getInstance().baseQuickAdapter) {
                                    getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                                }
                            }else {
                                tag.getView().setVisibility(View.VISIBLE);
                                tag.getView().setActivated(false);
                                tag.getTextView().setText("下载中");
                            }

                    }


                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    super.progress(task, soFarBytes, totalBytes);
//                    final BaseDownViewHolder tag = checkCurrentHolder(task);
//                    if (tag != null) {
//                        if(null != getInstance().baseQuickAdapter){
//                            if(tag.getPosition() > -1){
//                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
//                            }else {
//                                tag.getView().setVisibility(View.VISIBLE);
//                                tag.getView().setActivated(false);
//                                tag.getTextView().setText("下载中");
//                            }
//                        }else {
//                            tag.getView().setVisibility(View.VISIBLE);
//                            tag.getView().setActivated(false);
//                            tag.getTextView().setText("下载中");
//                        }
//                    }

                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {
                    super.error(task, e);

                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag != null) {

                    }
                    removeTaskForViewHolder(task.getId());
                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    super.paused(task, soFarBytes, totalBytes);
                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag == null) {
                        return;
                    }

                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    super.completed(task);

                    final BaseDownViewHolder tag = checkCurrentHolder(task);
                    if (tag != null) {
                        if(tag.getPosition() > -1){
                            if(null != getInstance().baseQuickAdapter){
                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                            }
                        }else {
                            if(tag.getPosition() == -1){
                                // tag.getDown_image().setBackgroundResource(R.drawable.down_press);
                                tag.getDown_image_checked().setVisibility(View.VISIBLE);

                                EventBus.getDefault().post(new Event.PlayEvent(2));//在播放页面下载后视图刷新
                            }

                            tag.getView().setVisibility(View.VISIBLE);
                            tag.getView().setActivated(true);
                            tag.getTextView().setText("已下载");
                        }
                    }
                    removeTaskForViewHolder(task.getId());

                }
            };

        }

        return taskDownloadListener;

    }

    private FileDownloadListener getDownloadImageListener(){

        if(null == downloadImageListener){
            downloadImageListener = new FileDownloadListener() {
                @Override
                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                }

                @Override
                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                }

                @Override
                protected void completed(BaseDownloadTask task) {

                }

                @Override
                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {

                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            };
        }
        return downloadImageListener;
    }


    public static String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String path = SdcardUtil.getDiskFileDir(BaseApplation.getBaseApp()) + File.separator + MqService.pathSong;

       // NLog.e(NLog.TAGOther,path);
        //"/storage/emulated/0/Android/data/fm.qian.michael/files/song"
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return FileDownloadUtils.generateFilePath(path,FileDownloadUtils.generateFileName(url));
    }
    public static String createImagePath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String path = SdcardUtil.getDiskFileDir(BaseApplation.getBaseApp()) + File.separator + MqService.pathImage;

        // NLog.e(NLog.TAGOther,path);
        //"/storage/emulated/0/Android/data/fm.qian.michael/files/image"
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return FileDownloadUtils.generateFilePath(path,FileDownloadUtils.generateFileName(url));
    }


    /**
     * 泛型类，主要用于 API 中功能的回调处理。
     *
     * @param <T> 声明一个泛型 T。
     */
    public static abstract class ResultCallback<T> {

        public static class Result<T> {
            public T t;
        }

        public ResultCallback() {

        }

        /**
         * 成功时回调。
         *
         * @param t 已声明的类型。
         */
        public abstract void onSuccess(T t);

        /**
         * 错误时回调。
         *
         * @param errString 错误提示
         */
        public abstract void onError(String errString);


        public void onCallbackFail(final String errString) {
            getInstance().mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onError(errString);
                }
            });
        }

        public void onCallbackSuccess(final T t) {
            getInstance(). mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(t);
                }
            });
        }
    }

}
