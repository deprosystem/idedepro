package com.dpcsa.compon.components;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.PagerIndicator;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.param.ParamComponent;

import java.util.ArrayList;
import java.util.List;

public class PagerVComponent extends BaseComponent {
    ViewPager pager;
    ListRecords listData;
    PagerIndicator indicator;
    private View further;
    int count;
    private List<String> tabTitle;
    private TabLayout tabLayout;
    private LayoutInflater inflater;
    private WorkWithRecordsAndViews modelToFurther = new WorkWithRecordsAndViews();

    public PagerVComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
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
    }

    @Override
    public void changeData(Field field) {
        if (field != null) {
            listData.clear();
            listData.addAll((ListRecords) field.value);
            count = listData.size();
            if (count > 0) {
                inflater = (LayoutInflater) iBase.getBaseActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                if (paramMV.paramView.indicatorId != 0) {
                    indicator = (PagerIndicator) parentLayout.findViewById(paramMV.paramView.indicatorId);
                    indicator.setCount(count);
                }


                if (paramMV.paramView.tabId != 0) {
                    tabLayout = (TabLayout) parentLayout.findViewById(paramMV.paramView.tabId);
                    if (tabLayout == null) {
                        iBase.log("Не найден TabLayout в " + multiComponent.nameComponent);
                    } else {
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
                }


                if (paramMV.paramView.furtherViewId != 0) {
                    further = (View) parentLayout.findViewById(paramMV.paramView.furtherViewId);
                    modelToFurther.RecordToView(listData.get(0), further, this, listener);
                }
                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (indicator != null) {
                            indicator.setSelect(position);
                        }
                        if (further != null) {
                            modelToFurther.RecordToView(listData.get(position), further, PagerVComponent.this, listener);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                pager.setAdapter(adapter);
                if (count == 1) {
                    if (further != null) {
                        modelToFurther.RecordToView(listData.get(0), further, this, listener);
//                        modelToFurther.RecordToView(listData.get(0), further, navigator, listener, null);
                    }
                }
            }
        }
    }

    PagerAdapter adapter = new PagerAdapter() {
        WorkWithRecordsAndViews modelToView = new WorkWithRecordsAndViews();
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View v = inflater.inflate(paramMV.paramView.layoutTypeId[0], null);
            Record record = listData.get(position);
            modelToView.RecordToView(record, v, PagerVComponent.this, listener);
            viewGroup.addView(v);
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (tabTitle.size() > 0) {
                return tabTitle.get(position);
            } else {
                return "";
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int position, Object view) {
            viewGroup.removeView((View) view);
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = pager.getCurrentItem();
            clickAdapter(null, v, position, listData.get(position));
        }
    };

    public void pagerPlusItem() {
        int posit = pager.getCurrentItem() + 1;
        if (posit < count) {
            pager.setCurrentItem(posit);
        }
    }
}
