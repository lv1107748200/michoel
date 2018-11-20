package fm.qian.michael.widget.pop;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xxbm.sbecomlibrary.utils.CheckUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.utils.CommonUtils;
import com.xxbm.sbecomlibrary.utils.NToast;

/*
 * lv   2018/10/15
 */
public class PopInputWindow extends BasePopupWindow   {

    private PopInputWindowCallBack popInputWindowCallBack;

    public void setPopInputWindowCallBack(PopInputWindowCallBack popInputWindowCallBack) {
        this.popInputWindowCallBack = popInputWindowCallBack;
    }

    @BindView(R.id.main_search)
    EditText main_search;
    @BindView(R.id.set_add_tv_title)
    TextView set_add_tv_title;

    @OnClick({R.id.layoutQX, R.id.layoutQD})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.layoutQX://取消
                dismiss();
                break;
            case R.id.layoutQD:

                if(null != popInputWindowCallBack){
                    popInputWindowCallBack.callBackInputText(main_search.getText().toString());
                }

                break;
        }
    }

    private BaseActivity activity;

    public PopInputWindow(BaseActivity context,CustomPopuWindConfig config) {
        super(config);
        this.activity = context;
    }

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.pop_input_layout,
                null,false
        );

        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void show(View parent) {
        super.show(parent);

        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.CENTER,0, 0);

        } else {
            this.dismiss();
        }
    }

    @Override
    public void onDiss() {

    }

    public void setMain_search(String main_search) {
        this.main_search.setHint(main_search);
    }

    public void setSet_add_tv_title(String set_add_tv_title) {
        this.set_add_tv_title .setText(set_add_tv_title);
    }

    public interface PopInputWindowCallBack{
        void callBackInputText(String text);
    }
}
