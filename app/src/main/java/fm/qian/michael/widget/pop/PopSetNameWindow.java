package fm.qian.michael.widget.pop;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.R;
/*
 * lv   2018/10/8
 */
public class PopSetNameWindow extends BasePopupWindow {


    private Context context;
    private SetNameCallBack setNameCallBack;

    private int show;
    private String sex;

    public void setSetNameCallBack(SetNameCallBack setNameCallBack) {
        this.setNameCallBack = setNameCallBack;
    }
    @BindView(R.id.main_search)
    EditText main_search;
    @BindView(R.id.sex_layout)
    LinearLayout sex_layout;
    @BindView(R.id.nan_layout)
    LinearLayout nan_layout;
    @BindView(R.id.nv_layout)
    LinearLayout nv_layout;
    @BindView(R.id.set_tv_title)
    TextView set_tv_title;
    @OnClick({R.id.layoutQX, R.id.layoutQD,R.id.nan_layout,R.id.nv_layout})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.layoutQX://取消
                dismiss();
                break;
            case R.id.layoutQD:
                if(show == 1){//输入框
                    if(null != setNameCallBack){
                        setNameCallBack.callBackName(main_search.getText().toString());
                    }
                }else if(show == 2){
                    if(null != setNameCallBack){
                        setNameCallBack.callBackSex(sex);
                    }
                }
                break;
            case R.id.nan_layout:
                sex = "2";
                nan_layout.setSelected(true);
                nv_layout.setSelected(false);

                break;
            case R.id.nv_layout:
                sex = "1";
                nan_layout.setSelected(false);
                nv_layout.setSelected(true);
                break;

        }
    }


    public PopSetNameWindow(Context context,CustomPopuWindConfig config) {
        super(config);
        this.context = context;
    }

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_set_name,null,false);

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

    public void setShow(int show,String sex){
        this.show = show;
        if(show == 1){//输入框

            set_tv_title.setText("昵称");
            main_search.setVisibility(View.VISIBLE);
            sex_layout.setVisibility(View.GONE);

        }else if(show == 2){

            set_tv_title.setText("性别");

            this.sex = sex;
            if("女".equals(sex)){
                nv_layout.setSelected(true);
            }else if("男".equals(sex)){
                nan_layout.setSelected(true);
            }

            main_search.setVisibility(View.GONE);
            sex_layout.setVisibility(View.VISIBLE);
        }
    }

    public interface SetNameCallBack{
        void callBackName(String s);
        void callBackSex(String s);
    }
}
