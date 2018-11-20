package fm.qian.michael.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xxbm.sbecomlibrary.utils.CheckUtil;

import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.ui.fragment.ComFragment;
import fm.qian.michael.ui.fragment.SearchFragment;

/*
 * lv   2018/9/25
 */
public class SearchFragmentAdater extends FragmentPagerAdapter {

    private List<SearchFragment> searchFragments;

    private String kind;

    public SearchFragmentAdater(FragmentManager fm) {
        super(fm);
        searchFragments = new ArrayList<>();
    }

    public SearchFragmentAdater(FragmentManager fm, String kind) {
        super(fm);
        searchFragments = new ArrayList<>();
        this.kind = kind;
    }

    @Override
    public SearchFragment getItem(int position) {

        SearchFragment politicalNewsTabFragment = searchFragments.get(position);

        return politicalNewsTabFragment;
    }

    @Override
    public int getCount() {
        if(!CheckUtil.isEmpty(searchFragments)){
            return searchFragments.size();
        }
        return 0;
    }

    public void setTitles(List<SearchFragment> searchFragments){
        if(!CheckUtil.isEmpty(searchFragments)){
            this.searchFragments.clear();
            this.searchFragments.addAll(searchFragments);
            notifyDataSetChanged();
        }
    }
}
