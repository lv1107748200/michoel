package fm.qian.michael.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hr.bclibrary.utils.CheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.jpush.Logger;

/*
 * lv   2018/11/12
 */
public class JpushMessageActivity extends BaseActivity {


    @Override
    public int getLayout() {
        return R.layout.activity_jpush;
    }

    @Override
    public void initView() {

        Intent intent = getIntent();
        if(null != intent){
            Bundle bundle = intent.getExtras();

            if(bundle!=null){

                String s = bundle.getString(JPushInterface.EXTRA_EXTRA);

                if(!CheckUtil.isEmpty(s)){
                    try {
                        JSONObject json = new JSONObject(s);

                        String id = json.optString("id");
                        String tid = json.optString("tid");

                        Base base = new Base();

                        base.setTid(tid);
                        base.setZid(id);

                        CommonUtils.setIntent(intent,JpushMessageActivity.this,base);

                        finish();

                        //NToast.shortToastBaseApp(tid);

                    } catch (JSONException e) {
                      //  Logger.e(TAG, "Get message extra JSON error!");
                        finish();
                    }
                }else {
                    finish();
                }

            }else {
                finish();
            }
        }else {
            finish();
        }

    }


}
