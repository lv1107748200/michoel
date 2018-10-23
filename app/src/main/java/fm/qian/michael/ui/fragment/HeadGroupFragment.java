package fm.qian.michael.ui.fragment;


import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
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
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleScFragment;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;

/*
 * lv   2018/9/19  普通专辑
 */
public class HeadGroupFragment extends BaseRecycleViewFragment {


    public static final String HEADGROUP = "HEADGROUP";

    private int p = -1;
    private int p1 = -1;

    private int hight = -1;

    private String id;

    private QuickAdapter quickAdapterIma;

    private String type;

    TextView itemTv;
    SelectableRoundedImageView itemImage;


    @Override
    public void init() {
        super.init();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            itemTv.setTextAppearance(R.style.Text3);
//        }else {
//            itemTv.setTextAppearance(mFontext, R.style.Text3);
//        }


        Intent intent = mFontext.getIntent();

        id = intent.getStringExtra(HEADGROUP);
    }

    @Override
    public void loadData() {
        pageNo = 1;
        if(!CheckUtil.isEmpty(id)){
            album();
        }

    }
    @Override
    public void Refresh() {
        pageNo = 1;
        if(!CheckUtil.isEmpty(id)){
            album();
        }
    }

    @Override
    public void loadMore() {

        if(!CheckUtil.isEmpty(id)){
            album();
        }
    }


    private void  album(){
        baseService.album(id, pageNo + "", "", "", new HttpCallback<Album, BaseResponse<Album, List<ComAll>>>() {
            @Override
            public void onError(HttpException e) {
                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                }else {
                    getRefreshLayout().finishRefresh();
                }
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseResponse<Album, List<ComAll>> k) {
                Album album = k.getInfo();

                setView(album.getTid(),k.getList());

                GlideUtil.setGlideImageMake(mFontext,album.getCover(),
                        itemImage);
                //itemTv.setText(album.getBrief());
                if(!CheckUtil.isEmpty(album.getBrief())){
                    RichText.from(album.getBrief()).bind(this).into(itemTv);
                }

            }
        },this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }



    public void setView(String tid,List<ComAll> comAlls) {

        type = tid;
        if (null == quickAdapterIma) {

            View view = LayoutInflater.from(mFontext).inflate(R.layout.head_group_ordinary_album,null,false);
            itemTv = view.findViewById(R.id.item_tv);
            itemImage = view.findViewById(R.id.item_image);



            RecyclerView.LayoutManager layoutManager = null;

            switch (tid) {
                case GlobalVariable.TWO://视频
                    layoutManager = new GridLayoutManager(mFontext, 2);
                    quickAdapterIma = new QuickAdapter(R.layout.item_image_and_text_changed_one) {
                        @Override
                        protected void convert(BaseViewHolder helper, Object item) {

                            if (p < 0) {
                                p = DisplayUtils.getDimen(R.dimen.item_margin_four);
                                p1 = DisplayUtils.getDimen(R.dimen.item_margin_two);
                            }

                            helper.itemView.setPadding(p, p1, p, p1);


                            if(hight < 0){
                                hight = wight2();
                            }

                            LayoutParmsUtils.setHight(hight,helper.getView(R.id.item_image));

                            if (item instanceof ComAll) {
                                helper.setText(R.id.item_tv, ((ComAll) item).getTitle());
                                GlideUtil.setGlideImageMake(helper.itemView.getContext(), ((ComAll) item).getCover(),
                                        (ImageView) helper.getView(R.id.item_image));
                            }
                        }
                    };
                    break;
                case GlobalVariable.THREE://图文
                    layoutManager = new LinearLayoutManager(mFontext);
                    quickAdapterIma = new QuickAdapter(R.layout.item_text_layout) {
                        @Override
                        protected void convert(BaseViewHolder helper, Object item) {

                            if (p < 0) {
                                p = DisplayUtils.getDimen(R.dimen.item_margin_four);
                            }
                            helper.itemView.setPadding(0, 0, 0, 0);
                            helper.itemView.findViewById(R.id.haolou_layout).setPadding(p, 0, p, 0);

                            if (item instanceof ComAll) {
                                helper.setText(R.id.title_tv_news, ((ComAll) item).getTitle());
                                helper.setText(R.id.title_tv_date, ((ComAll) item).getAddtime());

                                GlideUtil.setGlideImageMake(helper.itemView.getContext(), ((ComAll) item).getCover(),
                                        (ImageView) helper.getView(R.id.item_image));
                            }

                        }
                    };
                    break;
            }

            getRvList().setLayoutManager(layoutManager);

            quickAdapterIma.addHeaderView(view);

            quickAdapterIma.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Object item = quickAdapterIma.getItem(position);

                    if( item instanceof ComAll){

                        CommonUtils.getWebIntent(mFontext,type,((ComAll) item).getId());
//
//                        switch (type) {
//                            case GlobalVariable.TWO://视频
//                                CommonUtils.getWebIntent(mFontext,type,((ComAll) item).getId());
//                                break;
//                            case GlobalVariable.THREE://图文
//                                CommonUtils.getWebIntent(mFontext,type,((ComAll) item).getId());
//                                break;
//                        }

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


    private int wight2(){
        int  wight = DisplayUtils.getWideP(2,0
                ,DisplayUtils.getDimen(R.dimen.margin_80));

        // NLog.e(NLog.TAGOther,"宽度： --->" + wight);
        return wight;
    }

    @Override
    public void onDestroy() {
        RichText.clear(this);
        super.onDestroy();
    }
}
