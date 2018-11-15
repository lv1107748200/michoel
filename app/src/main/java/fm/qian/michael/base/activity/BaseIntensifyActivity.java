package fm.qian.michael.base.activity;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import butterknife.BindView;
import fm.qian.michael.R;

/*
 * lv   2018/9/8
 */
public class BaseIntensifyActivity extends BaseActivity{


//    @BindView(R.id.status_bar)
//    View statusBar;
    LinearLayout base_left_layout;
  public   LinearLayout base_right_layout;
    ImageView gif_image;

    @BindView(R.id.title_tv)
    TextView titleTv;

    @Override
    public void initView() {
        super.initView();

        base_left_layout = findViewById(R.id.base_left_layout);
        base_right_layout = findViewById(R.id.base_right_layout);
        gif_image = findViewById(R.id.gif_image);
        if(isAddGifImage()){
            setGif(gif_image);
            if(null != base_right_layout){
                base_right_layout.setOnClickListener(new Onclick());
            }
        }

        if(null != base_left_layout){
            base_left_layout.setOnClickListener(new Onclick());
        }


       // setStatusBar(statusBar);
    }

    public void setTitleTv(String tv) {
        titleTv.setText(tv);
    }


    public View getEmpty(){
        View view = LayoutInflater.from(this).inflate(R.layout.empty_view,null,false);

        return view;
    }

    private class Onclick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.base_left_layout:
                    finish();
                    break;
                case R.id.base_right_layout:
                    startPlay();
                    break;

            }
        }
    }
}
