package fm.qian.michael.widget.single;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.SdcardUtil;
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
import fm.qian.michael.db.TasksManagerModel;
import fm.qian.michael.db.TasksManagerModel_Table;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.service.MqService;
import fm.qian.michael.ui.adapter.QuickAdapter;
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


    private FileDownloadListener downloadListener;
    private FileDownloadListener taskDownloadListener;
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

    public static void setIdDown(final List<ComAll> list, final ResultCallback<List<ComAll>> resultCallback){
        if(getInstance().workHandler == null){
            getInstance().init();
        }
        if(CheckUtil.isEmpty(list))
            return ;

//        NLog.e(NLog.TAGDOWN,"服务是否连接--- " +FileDownloader.getImpl().isServiceConnected());
        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {
                NLog.e(NLog.TAGDOWN,"服务是否连接--- " +FileDownloader.getImpl().isServiceConnected());
                NLog.e(NLog.TAGDOWN,"为列表生成下载的 id" );
                for(ComAll comAll: list){
                    String url = comAll.getUrl();
                    String path = instance.createPath(url);
                    int id = FileDownloadUtils.generateId(url, path);
                    comAll.setIsDown(id);
                    comAll.setDownPath(path);
                }
                if(null != resultCallback){
                    resultCallback.onCallbackSuccess(list);
                }

            }
        });
    }

    //任务管理
//    public static void setIdAndPath(final List<ComAll> list,
//                                    final FileDownloadListener fileDownloadListener,
//                                    final ResultCallback resultCallback){
//
//        if(getInstance().workHandler == null){
//            getInstance().init();
//        }
//
//        if(CheckUtil.isEmpty(list))
//            return ;
//
//        getInstance().workHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                getInstance().setDownloadListener();
//
//                if(null == getInstance().queueSet){
//                    getInstance().queueSet = new FileDownloadQueueSet(getInstance().taskDownloadListener);
//                    getInstance().queueSet.setAutoRetryTimes(1);
//                }
//
//
//
//                List<BaseDownloadTask> tasks = new ArrayList<>();
//
//                for(ComAll comAll: list){
//
//
//
//                    String url = comAll.getUrl();
//
//                    if(CheckUtil.isEmpty(url))
//                        return;
//
//                    String path = getInstance().createPath(url);
//                    String name = FileDownloadUtils.generateFileName(url);//MD5编码
//
//                    int id = FileDownloadUtils.generateId(url, path);
//
//                    TasksManagerModel tasksManagerModel =  SQLite.select()
//                            .from(TasksManagerModel.class)
//                            .where(TasksManagerModel_Table.id.eq(id))
//                            .querySingle();
//
//                    if(tasksManagerModel != null){
//
//                    }else {
//                        TasksManagerModel tasksManagerMode = new TasksManagerModel();
//                        tasksManagerMode.setId(id);
//                        tasksManagerMode.setName(name);
//                        tasksManagerMode.setUrl(url);
//                        tasksManagerMode.setUrlImg(ImgDatasUtils.getUrl());
//                        tasksManagerMode.setPath(path);
//                        tasksManagerMode.save();
//                    }
//
//                    BaseDownloadTask task = getInstance().getTaskSparseArray().get(id);
//
//                    if(task == null){
//                        task = FileDownloader.getImpl().create(url)
//                                .setPath(path);
//                        getInstance().getTaskSparseArray().put(task.getId(),task);
//
//                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {
//                            tasks.add(task);
//                        }
//
//                    }else {
////                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {
////                            tasks.add(task);
////                        }
//                    }
//
//                }
//                NLog.e(NLog.TAGOther,tasks.size());
//
//                if(isC){//串行
//                    getInstance().queueSet.downloadSequentially(tasks);
//                }else {//并行
//                    getInstance().queueSet.downloadTogether(tasks);
//                }
//
//                getInstance().queueSet.start();
//
//                if(null != resultCallback){
//                    resultCallback.onCallbackSuccess("成功添加下载任务");
//                }
//
//            }
//        });
//    }

    //任务管理
    public static void setIdAndPath(final List<ComAll> list,
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
                getInstance().setDownloadListener();

                FileDownloader.getImpl().pause(getInstance().taskDownloadListener);


                for(ComAll comAll: list){

                    String url = comAll.getUrl();

                    if(CheckUtil.isEmpty(url))
                        return;

                    String path = getInstance().createPath(url);
                    if(CheckUtil.isEmpty(path))
                        return;

                    String name = FileDownloadUtils.generateFileName(url);//MD5编码

                    int id = FileDownloadUtils.generateId(url, path);

                    TasksManagerModel tasksManagerModel =  SQLite.select()
                            .from(TasksManagerModel.class)
                            .where(TasksManagerModel_Table.id.eq(id))
                            .querySingle();

                    if(tasksManagerModel != null){

                    }else {
                        TasksManagerModel tasksManagerMode = new TasksManagerModel();
                        tasksManagerMode.setId(id);
                        tasksManagerMode.setName(name);
                        tasksManagerMode.setUrl(url);
                        tasksManagerMode.setUrlImg(ImgDatasUtils.getUrl());
                        tasksManagerMode.setPath(path);
                        tasksManagerMode.save();
                    }


                }

                List<TasksManagerModel> listTask =  SQLite.select()
                        .from(TasksManagerModel.class)
                        .orderBy(TasksManagerModel_Table.Idd,true)
                        .queryList();


                if(!CheckUtil.isEmpty(listTask)){
                    for(TasksManagerModel tasksManagerModel : listTask){

                        String url = tasksManagerModel.getUrl();
                        String path = tasksManagerModel.getPath();
                        int id = tasksManagerModel.getId();

                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {
                            BaseDownloadTask task = FileDownloader.getImpl().create(url)
                                    .setListener(getInstance().taskDownloadListener)
                                    .setPath(path);

                            getInstance().getTaskSparseArray().put(task.getId(),task);

                            task.asInQueueTask()
                                    .enqueue();
                        }
                    }
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
    }

//    public static boolean addListTask(final List<TasksManagerModel> list, final ResultCallback resultCallback){
//
//        if(getInstance().workHandler == null){
//            getInstance().init();
//        }
//        if(CheckUtil.isEmpty(list))
//            return false;
//        NLog.e(NLog.TAGDOWN,"服务是否连接--- " +FileDownloader.getImpl().isServiceConnected());
//
//        getInstance().workHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                getInstance().setDownloadListener();
//                if(null == getInstance().queueSet){
//                    getInstance().queueSet = new FileDownloadQueueSet(getInstance().taskDownloadListener);
//                    getInstance().queueSet.setAutoRetryTimes(1);
//                }
//
//                List<BaseDownloadTask> tasks = new ArrayList<>();
//                for (int i = 0; i < list.size(); i++) {
//
//                    TasksManagerModel tasksManagerModel = list.get(i);
//
//                    String url = tasksManagerModel.getUrl();
//                    String path = tasksManagerModel.getPath();
//
//                    int id = tasksManagerModel.getId();
//                    // NLog.e(NLog.TAGDOWN," 数据库 下载id : " + id);
//
//                    BaseDownloadTask task = getInstance().getTaskSparseArray().get(id);
//
//
//                    if(task == null){
//                        task = FileDownloader.getImpl().create(url)
//                                .setPath(path);
//                        getInstance().getTaskSparseArray().put(task.getId(),task);
//
//                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {
//                            tasks.add(task);
//                        }
//
//                    }else {
////                        if(isDownStatus(id,path) != FileDownloadStatus.completed) {
////                            tasks.add(task);
////                        }
//                    }
//                }
//
//                if(isC){//串行
//                    getInstance().queueSet.downloadSequentially(tasks);
//                }else {//并行
//                    getInstance().queueSet.downloadTogether(tasks);
//                }
//                getInstance().queueSet.start();
//
//                if(null != resultCallback){
//                    resultCallback.onCallbackSuccess("成功添加下载任务");
//                }
//
//            }
//        });
//
//        return true;
//    }

    public static void singTask(ComAll comAll, final ResultCallback resultCallback){
        String url = comAll.getUrl();
        String path = getInstance().createPath(url);
        BaseDownloadTask task = FileDownloader.getImpl().create(url)
                .setPath(path)
                .setCallbackProgressTimes(100)
                .setListener(getInstance().createLis());
        task.start();
    }

    public static boolean addListTask(final List<TasksManagerModel> list, final ResultCallback resultCallback){

        if(getInstance().workHandler == null){
            getInstance().init();
        }
        if(CheckUtil.isEmpty(list))
            return false;
        NLog.e(NLog.TAGDOWN,"服务是否连接--- " +FileDownloader.getImpl().isServiceConnected());

        getInstance().workHandler.post(new Runnable() {
            @Override
            public void run() {

                getInstance().setDownloadListener();
                FileDownloader.getImpl().pause(getInstance().taskDownloadListener);


                for (int i = 0; i < list.size(); i++) {

                    TasksManagerModel tasksManagerModel = list.get(i);

                    String url = tasksManagerModel.getUrl();
                    String path = tasksManagerModel.getPath();

                    int id = tasksManagerModel.getId();
                   // NLog.e(NLog.TAGDOWN," 数据库 下载id : " + id);

                    if(isDownStatus(id,path) != FileDownloadStatus.completed) {
                        BaseDownloadTask task = FileDownloader.getImpl().create(url)
                                .setListener(getInstance().taskDownloadListener)
                                .setPath(path);

                        getInstance().getTaskSparseArray().put(task.getId(),task);

                        task.asInQueueTask()
                                .enqueue();
                    }else {
                       // tasksManagerModel.delete();
                    }
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


                    String path = DownManger.createPath(comAll.getUrl());
                    int id = FileDownloadUtils.generateId(comAll.getUrl(), path);
                    int statue = DownManger.isDownStatus(id,path);

                    TasksManagerModel tasksManagerModel =  SQLite.select()
                            .from(TasksManagerModel.class)
                            .where(TasksManagerModel_Table.id.eq(id))
                            .querySingle();

                    if(tasksManagerModel !=null){
                        tasksManagerModel.delete();
                    }

                    if(statue == FileDownloadStatus.completed){
                        new File(path).delete();
                    }

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

    private void setDownloadListener(){
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
                        if(null != getInstance().baseQuickAdapter){
                            if(tag.getPosition() > -1){
                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                            }else {
                                tag.getView().setVisibility(View.VISIBLE);
                                tag.getView().setActivated(false);
                                tag.getTextView().setText("下载中");
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
                        if(null != getInstance().baseQuickAdapter){
                            if(tag.getPosition() > -1){
                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                            }else {
                                tag.getView().setVisibility(View.VISIBLE);
                                tag.getView().setActivated(false);
                                tag.getTextView().setText("下载中");
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


                        if(null != getInstance().baseQuickAdapter){
                            if(tag.getPosition() > -1){
                                getInstance().baseQuickAdapter.notifyItemChanged(tag.getPosition());
                            }else {
                                tag.getView().setVisibility(View.VISIBLE);
                                tag.getView().setActivated(true);
                                tag.getTextView().setText("已下载");
                            }
                        }else {
                            tag.getView().setVisibility(View.VISIBLE);
                            tag.getView().setActivated(true);
                            tag.getTextView().setText("已下载");
                        }
                    }
                    removeTaskForViewHolder(task.getId());

                }
            };

        }

    }


    private FileDownloadListener createLis() {
        return new FileDownloadListener() {

            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                // 之所以加这句判断，是因为有些异步任务在pause以后，会持续回调pause回来，而有些任务在pause之前已经完成，
                // 但是通知消息还在线程池中还未回调回来，这里可以优化
                // 后面所有在回调中加这句都是这个原因
                if (task.getListener() != downloadListener) {
                    return;
                }
                NLog.e(NLog.TAGDOWN,"pending --- "+soFarBytes+" --- " + totalBytes + " --- "+task.getSpeed());

            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                     int soFarBytes, int totalBytes) {

                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                if (task.getListener() != downloadListener) {
                    return;
                }
                NLog.e(NLog.TAGDOWN,"connected --- "+soFarBytes+" --- " + totalBytes + " --- "+task.getSpeed());

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != downloadListener) {
                    return;
                }

               // EventBus.getDefault().post(new Event.DownEvent(task.getId()+""));

                NLog.e(NLog.TAGDOWN,"progress --- "+soFarBytes+" --- " + totalBytes + " --- "+task.getSpeed());

            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void completed(final BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }



                if (task.isReusedOldFile()) {

                } else {

                }
                // EventBus.getDefault().post(new Event.DownEvent(task.getId()+""));

                NLog.e(NLog.TAGDOWN,"completed --- completed   "+task.getId());

               // setNoftion(task.getId());

            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (task.getListener() != downloadListener) {
                    return;
                }
                NLog.e(NLog.TAGDOWN," --- error" + e.getMessage());

            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }
                NLog.e(NLog.TAGDOWN," ---- warn");
            }
        };
    }




    public static String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String path = SdcardUtil.getDiskFileDir(BaseApplation.getBaseApp(), MqService.pathSong);

       // NLog.e(NLog.TAGOther,path);
        //"/storage/emulated/0/Android/data/fm.qian.michael/files/song"
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
