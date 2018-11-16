package fm.qian.michael.widget.custom;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.NLog;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.db.SearchHistory;
import fm.qian.michael.db.SearchHistory_Table;
import fm.qian.michael.db.TasksManagerModel;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.DateUtils;
import fm.qian.michael.utils.NToast;

/*
 * lv   2018/9/18
 */
public class SearchLayout extends FrameLayout implements
        TextView.OnEditorActionListener,View.OnFocusChangeListener
,View.OnClickListener{
    private View viewHead;
    private View viewBottom;
    private String searchText;
    private List<Base> stringList;
    private QuickAdapter quickAdapter;

    private SearchCallBack searchCallBack;

    @BindView(R.id.nv)
    LinearLayout nv;
    @BindView(R.id.left_back_image)
    ImageView left_back_image;
    @BindView(R.id.image_scan)
    ImageView image_scan;
    @BindView(R.id.item_recycler)
    RecyclerView item_recycler;
    @BindView(R.id.search_line)
            View search_line;


    TagFlowLayout id_flowlayout;
    TextView tv_clear;

    @BindView(R.id.search_et_input)
    EditText search_et_input;

    @OnClick({R.id.search_iv_delete,
            R.id.image_scan,R.id.left_back_image})
    public void  Onclick(View view){
        switch (view.getId()){
            case R.id.search_iv_delete:


                if(null != searchCallBack){
                    if(!CheckUtil.isEmpty(search_et_input.getText().toString())){
                        search_et_input.setText("");
                        searchCallBack.del(1);
                    }else if(nv.getVisibility() == VISIBLE){
                        searchCallBack.del(2);
                    }
                }

                break;
            case R.id.image_scan:
                if(null != searchCallBack){
                    searchCallBack.scan();
                }
                break;
        }
    }

    public void setSearchCallBack(SearchCallBack searchCallBack) {
        this.searchCallBack = searchCallBack;
    }

    public SearchLayout(@NonNull Context context) {
        this(context,null);
    }

    public SearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }
    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.search_layout,null,false);
         viewHead = LayoutInflater.from(context).inflate(R.layout.head_search,null,false);
        viewBottom = LayoutInflater.from(context).inflate(R.layout.bottom_search,null,false);
        addView(view);

        ButterKnife.bind(this,view);

        id_flowlayout = viewHead.findViewById(R.id.id_flowlayout);
        tv_clear = viewBottom.findViewById(R.id.tv_clear);


        search_et_input.setOnEditorActionListener(this);
        search_et_input.setOnFocusChangeListener(this);
        search_et_input.setOnClickListener(this);
        tv_clear.setOnClickListener(this);



        id_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searchText = stringList.get(position).getName();
                NLog.e(NLog.TAGOther, "searchText: "  + searchText);
                if(!CheckUtil.isEmpty(searchText)){
                    search_et_input.setText(searchText);
                    search_et_input. setSelection(searchText.length());
                    if(null != searchCallBack){
                        searchCallBack.textCallBack(searchText);
                        save();
                    }
                }

                return true;
            }
        });

        setItem_recycler();


    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(hasFocus){
            if(nv.getVisibility() == GONE){
                nv.setVisibility(VISIBLE);
                setItem_recyclerData();//焦点改变时
                if(null != searchCallBack){
                    searchCallBack.show(GONE);
                }
            }
        }else {
            nv.setVisibility(GONE);
            if(null != searchCallBack){
                searchCallBack.show(VISIBLE);
            }
        }

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.search_et_input:
                if(nv.getVisibility() == GONE){
                    nv.setVisibility(VISIBLE);
                    setItem_recyclerData();//点击按钮
                    if(null != searchCallBack){
                        searchCallBack.show(GONE);
                    }
                }
                break;
            case R.id.tv_clear:
               // NToast.shortToastBaseApp("清空");
                Delete.table(SearchHistory.class);
                setItem_recyclerData();//清空后
                break;
        }
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEARCH:



                searchText = v.getText().toString();
                NLog.e(NLog.TAGOther, "searchText: "  + searchText);

                if(!CheckUtil.isEmpty(searchText)){
                    if(null != searchCallBack){
                        searchCallBack.textCallBack(searchText);
                    }
                   save();
                }else {
                    if(null != searchCallBack) {
                        searchCallBack.del(1);
                    }
                }

                break;
        }
        return true;
    }

    public void setId_flowlayout(List<Base> list){
        stringList = list;
        id_flowlayout.setAdapter(new TagAdapter<Base>(stringList) {
            @Override
            public View getView(FlowLayout parent, int position, Base o) {

                TextView tv = (TextView) LayoutInflater.from(SearchLayout.this.getContext()).inflate(R.layout.tv,
                        id_flowlayout, false);

                tv.setText(o.getName());

                return tv;
            }


        });
    }

    public List<Base>  getStringList(){
        return stringList;
    }

    public void setItem_recycler(){

        item_recycler.setNestedScrollingEnabled(false);
        item_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        quickAdapter = new QuickAdapter(R.layout.item_com_list){
            @Override
            protected void convert(BaseViewHolder helper,final Object item) {
                if(item instanceof SearchHistory){
                    helper.setText(R.id.what_tv,((SearchHistory) item).getName());
                }
                helper.setOnClickListener(R.id.delete_layout, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item instanceof SearchHistory){
                            ((SearchHistory) item).delete();

                            setItem_recyclerData();//删除某一个后
                        }
                    }
                });
            }
        };

        quickAdapter.addHeaderView(viewHead);
        quickAdapter.addFooterView(viewBottom);
        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                searchText =( (SearchHistory) quickAdapter.getData().get(position)).getName();
                NLog.e(NLog.TAGOther, "searchText: "  + searchText);
                if(!CheckUtil.isEmpty(searchText)){
                    search_et_input.setText(searchText);
                    search_et_input. setSelection(searchText.length());
                    if(null != searchCallBack){
                        save();
                        searchCallBack.textCallBack(searchText);
                    }
                }
            }
        });

        item_recycler.setAdapter(quickAdapter);

    }

    private void save(){
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setName(searchText);
        searchHistory.setTime(DateUtils.getStringTodayLong());
        searchHistory.save();
        setItem_recyclerData();//保存后刷新
    }

    public void setItem_recyclerData(){

      SQLite.select()
              .from(SearchHistory.class)
              .orderBy(SearchHistory_Table.time,false)
              .async()
              .queryListResultCallback(new QueryTransaction.QueryResultListCallback<SearchHistory>() {
                  @Override
                  public void onListQueryResult(QueryTransaction transaction, @NonNull List<SearchHistory> tResult) {
                   if(!CheckUtil.isEmpty(tResult)){
                       tv_clear.setVisibility(VISIBLE);
                       quickAdapter.replaceData(tResult);
                   }else {
                       tv_clear.setVisibility(GONE);
                       quickAdapter.replaceData(new ArrayList<>());
                   }
                  }
              }).error(new Transaction.Error() {
          @Override
          public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
              tv_clear.setVisibility(GONE);
              quickAdapter.replaceData(new ArrayList<>());
          }
      }).execute();

    }

    public void setSearch_line(int i) {
        this.search_line.setVisibility(i);
    }

    public void setNv(int i){
        nv.setVisibility(i);
    }
    public void setImageScan(int i){
        image_scan.setVisibility(i);
    }
    //设置输入框  提示语
    public void setSearch_et_inputHint(String hint){
        search_et_input.setHint(hint);
    }
    public interface SearchCallBack{
        void textCallBack(String text);
        void show(int i);
        void del(int type);
        void scan();
    }
}
