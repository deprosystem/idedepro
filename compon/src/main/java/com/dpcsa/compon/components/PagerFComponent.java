package com.dpcsa.compon.components;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseFragment;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.PagerIndicator;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.param.ParamComponent;

import java.util.ArrayList;
import java.util.List;

public class PagerFComponent extends BaseComponent {
    ViewPager pager;
    ListRecords listData;
    PagerIndicator indicator;
    private View further;
    int count;
    private LayoutInflater inflater;
    private WorkWithRecordsAndViews modelToFurther = new WorkWithRecordsAndViews();
    private List<String> tabTitle;
    private TabLayout tabLayout;
    private Adapter adapter;

    public PagerFComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView == null || paramMV.paramView.viewId == 0) {
            pager = (ViewPager) componGlob.findViewByName(parentLayout, "pager");
        } else {
            pager = (ViewPager) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (pager == null) {
            iBase.log("Не найден ViewPager в " + multiComponent.nameComponent);
        }
        listData = new ListRecords();
        tabTitle = new ArrayList<>();
        count = paramMV.paramView.screens.length;
    }

    public void selectFragment(String screen) {
        if (screen == null || screen.length() == 0) return;
        for (int i = 0; i < count; i++) {
            String nameF = paramMV.paramView.screens[i];
            if (screen.equals(nameF)) {
                pager.setCurrentItem(i);
                String push = preferences.getPushType();
                if (push.length() > 0) {
                    BaseFragment bf = (BaseFragment) adapter.getFragmentView(i);
                    if (bf != null && bf.getView() != null && bf.getView().isShown()) {
                        bf.work.run();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void changeData(Field field) {
        if (paramMV.paramView.indicatorId != 0) {
            indicator = (PagerIndicator) parentLayout.findViewById(paramMV.paramView.indicatorId);
            indicator.setCount(count);
        }
        if (paramMV.paramView.tabId != 0) {
            tabLayout = (TabLayout) parentLayout.findViewById(paramMV.paramView.tabId);
            tabLayout.setupWithViewPager(pager);
            if (paramMV.paramView.arrayLabelId != 0) {
                String[] title = iBase.getBaseActivity().getResources().getStringArray(paramMV.paramView.arrayLabelId);
                int im = title.length;
                for (int i = 0; i < count; i++) {
                    if (i < im) {
                        tabTitle.add(title[i]);
                    } else {
                        tabTitle.add(String.valueOf(i));
                    }
                }
            } else {
                for (int i = 0; i < count; i++) {
                    tabTitle.add(String.valueOf(i));
                }
            }
        }
        BaseFragment bf = iBase.getBaseFragment();
        if (bf != null) {
            adapter = new Adapter(bf.getChildFragmentManager());
        } else {
            adapter = new Adapter(iBase.getBaseActivity().getSupportFragmentManager());
        }
        pager.setAdapter(adapter);
    }

    public class Adapter extends FragmentPagerAdapter {

        private FragmentManager fm;

        public Adapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            String nameF = paramMV.paramView.screens[position];
            Screen model = iBase.getBaseActivity().mapFragment.get(nameF);
            if (model == null) {
                iBase.log("1008 не описан фрагмент " + nameF + " в " + multiComponent.nameComponent);
            }
            BaseFragment fragment = new BaseFragment();
            fragment.setModel(model);
            return fragment;
        }

        public Fragment getFragmentView(int position) {
            List <Fragment> lf = fm.getFragments();
            for (int i = 0; i < lf.size(); i++) {
                Fragment ff = lf.get(i);
                String st = ff.getTag();
                int j = st.lastIndexOf(":");
                st = st.substring(j + 1);
                if (position == Integer.valueOf(st)) {
                    return ff;
                }
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle.get(position);
        }

        @Override
        public int getCount() {
            return count;
        }
    }

}
