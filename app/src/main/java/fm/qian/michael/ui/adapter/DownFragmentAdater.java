package fm.qian.michael.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hr.bclibrary.utils.CheckUtil;

import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.ui.fragment.ComFragment;

/**
 * Created by 吕 on 2017/12/6.
 */

public class DownFragmentAdater extends FragmentPagerAdapter {

    private List<Fragment> titles;

    public DownFragmentAdater(FragmentManager fm) {
        super(fm);
        titles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return titles.get(position);
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
