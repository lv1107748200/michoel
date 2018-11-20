package com.xxbm.musiclibrary;


import android.text.TextUtils;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.utils.SdcardUtil;

import java.io.File;

/*
 * lv   2018/11/20
 */
public class DownPathUtil {
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
}
