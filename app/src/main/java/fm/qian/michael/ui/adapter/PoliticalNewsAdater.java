package fm.qian.michael.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xxbm.sbecomlibrary.utils.CheckUtil;
import fm.qian.michael.ui.fragment.ComFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕 on 2017/12/6.
 */

public class PoliticalNewsAdater extends FragmentPagerAdapter {

    private List titles;

    private String kind;

    public PoliticalNewsAdater(FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
    }

    public PoliticalNewsAdater(FragmentManager fm, String kind) {
        super(fm);
        this.titles = new ArrayList<>();
        this.kind = kind;
    }

    @Override
    public Fragment getItem(int position) {
        ComFragment politicalNewsTabFragment = new ComFragment();
        politicalNewsTabFragment.setType(position);

        return politicalNewsTabFragment;
    }

    @Override
    public int getCount() {
        if(!CheckUtil.isEmpty(titles)){
            return titles.size();
        }
        return 0;
    }

    public void setTitles(List titles1){
        if(!CheckUtil.isEmpty(titles1)){
            titles.clear();
            titles.addAll(titles1);
            notifyDataSetChanged();
        }
    }
}
