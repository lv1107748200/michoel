package fm.qian.michael.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.hr.bclibrary.utils.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕 on 2017/12/11.
 */

public class MainAdapter extends FragmentPagerAdapter {

    private boolean[] flags;//标识,重新设置fragment时全设为true

    private List<Fragment> fragmentList;

    private FragmentManager fm;

    private long time;

    public MainAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        if(!CheckUtil.isEmpty(fragmentList)){
            return fragmentList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if(!CheckUtil.isEmpty(fragmentList)){
            return fragmentList.size();
        }
        return 0;
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        if (flags != null && flags[position]) {
//            /**得到缓存的fragment, 拿到tag并替换成新的fragment*/
//            Fragment fragment = (Fragment) super.instantiateItem(container, position);
//            String fragmentTag = fragment.getTag();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.remove(fragment);
//            fragment = fragmentList.get(position);
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commit();
//            /**替换完成后设为false*/
//            flags[position] = true;
//            if (fragment != null){
//                return fragment;
//            }else {
//                return super.instantiateItem(container, position);
//            }
//        } else {
//            return super.instantiateItem(container, position);
//        }
//    }

    public void upData(List list){
        if(CheckUtil.isEmpty(list))
            return;
        if(CheckUtil.isEmpty(fragmentList)){
            fragmentList = new ArrayList<>();
        }else {
            fragmentList.clear();
        }

//        if (this.fragmentList != null) {
//            flags = new boolean[list.size()];
//            for (int i = 0; i < list.size(); i++) {
//                flags[i] = false;
//            }
//        }

        fragmentList.addAll(list);
        notifyDataSetChanged();
    }

    public void updata(){
        notifyDataSetChanged();
    }
}
