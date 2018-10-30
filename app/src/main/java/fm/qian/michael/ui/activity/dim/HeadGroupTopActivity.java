package fm.qian.michael.ui.activity.dim;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.RankMore;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;

import static fm.qian.michael.utils.DisplayUtils.ImageHight3;
import static fm.qian.michael.utils.DisplayUtils.imageHight1;


/*
 * lv   2018/9/8  专题页
 */
public class HeadGroupTopActivity extends BaseRecycleViewActivity {
    public static final String HEADGROUPOTHER = "HEADGROUPOTHER";
    public static final int ONE = 1;//音频专辑
    public static final int TWO = 2;//视频集合
    public static final int THREE = 3;//文章集合

    public static final int FIVE = 5;//已购买专辑
    public static final int FOUR = 4;//收藏专集

    private int p = -1;
    private int p1 = -1;

    private int hight = -1;

    private String id;

    private QuickAdapter quickAdapterIma;
    private View view;

    TextView itemTv;
    SelectableRoundedImageView itemImage;

    @OnClick({R.id.base_left_layout, R.id.base_right_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.base_right_layout:

                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_head_top_group;
    }

    @Override
    public void initView() {
        super.initView();
        setTitleTv("HeadGroupActivity");
    }

    @Override
    public boolean isDamp() {
        return false;
    }

    @Override
    public void initData() {
        super.initData();

         view = LayoutInflater.from(this).inflate(R.layout.head_top_group,null,false);
        itemTv = view.findViewById(R.id.item_tv);
        itemImage = view.findViewById(R.id.item_image);
        LayoutParmsUtils.setHight(imageHight1(3,0,0),itemImage);

      //  itemTv.setText("呵呵呵金额合计耳机和九二九二二事件发生纠纷军事打击对方来得及");

       //View  headView = LayoutInflater.from(this).inflate(R.layout.item_image_and_text,null,false);

        Intent intent = getIntent();

        id = intent.getStringExtra(HEADGROUPOTHER);

        if(!CheckUtil.isEmpty(id)){
            topic();
        }
    }


    @Override
    public void Refresh() {
        pageNo = 1;
        topic();
    }
    @Override
    public void loadMore() {

        topic();
    }

    @Override
    protected void onDestroy() {
        RichText.clear(this);
        super.onDestroy();
    }

    private void  topic(){
        baseService.topic(id,pageNo+"",new HttpCallback<ComAll, BaseResponse<ComAll, List<Base>>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseResponse<ComAll, List<Base>> k) {
                ComAll album = k.getInfo();

                setView(k.getList(), album);

            }
        },this.bindUntilEvent(ActivityEvent.DESTROY));
    }



    public void setView(List<Base> comAlls,ComAll album){

        if(null == quickAdapterIma){

            GlideUtil.setGlideImageMake(HeadGroupTopActivity.this,album.getCover(),
                    itemImage);
//            itemTv.setText(album.getBrief());
            if(!CheckUtil.isEmpty(album.getBrief())){
                RichText.from(album.getBrief()).bind(this).into(itemTv);
            }
            setTitleTv(album.getTitle());

            RecyclerView.LayoutManager layoutManager = null;
            layoutManager =  new LinearLayoutManager(this);
            quickAdapterIma =  new QuickAdapter(R.layout.item_image_and_text_changed_one){
                @Override
                protected void convert(BaseViewHolder helper, Object item) {

//                    if(p < 0){
//                        p = DisplayUtils.getDimen(R.dimen.item_margin_four);
//                        p1 = DisplayUtils.getDimen(R.dimen.item_margin_two);
//                    }
//
//                    helper.itemView.setPadding(p,p1,p,p1);
                    LayoutParmsUtils.setHight(ImageHight3(),helper.getView(R.id.item_image));
                    helper.setGone(R.id.item_tv,false);

//                    if(hight < 0){
//                        hight = DisplayUtils.getDimen(R.dimen.margin_120);
//                    }
//
//                    helper.itemView.setLayoutParams(LayoutParmsUtils.getGroupParms(hight));

                    if(item instanceof  Base){

                        GlideUtil.setGlideImageMake(helper.itemView.getContext(),((Base) item).getCover(),
                                (ImageView) helper.getView(R.id.item_image));

                    }

                }
            };

            getRvList().setLayoutManager(layoutManager);

            quickAdapterIma.addHeaderView(view);

            quickAdapterIma.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Object item = quickAdapterIma.getItem(position);

                    if(item instanceof  Base){
                        Intent intent = new Intent(HeadGroupTopActivity.this,HeadGroupActivity.class);
                        intent.putExtra(HeadGroupActivity.HEADGROUP,((Base) item).getId());
                        startActivity(intent);
                    }


                }
            });

            getRvList().setAdapter(quickAdapterIma);

        }

        if(isUpOrDown){

            getRefreshLayout().finishLoadMore();
            if(!CheckUtil.isEmpty(comAlls)){
                pageNo ++ ;
                quickAdapterIma.addData(comAlls);
            }

        }else {

            getRefreshLayout().finishRefresh();
            if(!CheckUtil.isEmpty(comAlls)){
                pageNo ++ ;
                quickAdapterIma.replaceData(comAlls);
            }else {
                quickAdapterIma.replaceData(new ArrayList<>());
            }

        }

    }



}

