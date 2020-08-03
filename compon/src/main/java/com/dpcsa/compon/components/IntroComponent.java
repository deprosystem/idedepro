package com.dpcsa.compon.components;

import android.content.Context;
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
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;

public class IntroComponent extends BaseComponent {
    ViewPager pager;
    ListRecords listData;
    PagerIndicator indicator;
    private View furtherSkip, furtherNext, furtherStart;
    int count, count_1;
    private LayoutInflater inflater;
    private ComponPrefTool preferences;
//    private WorkWithRecordsAndViews modelToFurther = new WorkWithRecordsAndViews();

    public IntroComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
        preferences = Injector.getPreferences();
    }

    @Override
    public void initView() {
        pager = (ViewPager) parentLayout.findViewById(paramMV.paramView.viewId);
        if (pager == null) {
            iBase.log("Не найден ViewPager в " + multiComponent.nameComponent);
        }
        listData = new ListRecords();
    }

    @Override
    public void changeData(Field field) {
        if (field != null) {
            listData.clear();
            listData.addAll((ListRecords) field.value);
            count = listData.size();
            count_1 = count - 1;
            if (count > 0) {
                inflater = (LayoutInflater) iBase.getBaseActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                if (paramMV.paramView.indicatorId != 0) {
                    indicator = (PagerIndicator) parentLayout.findViewById(paramMV.paramView.indicatorId);
                    indicator.setCount(count);
                }
                if (paramMV.paramView.furtherSkip != 0) {
                    furtherSkip = (View) parentLayout.findViewById(paramMV.paramView.furtherSkip);
                    if (furtherSkip != null) {
                        if (count == 1) {
                            furtherSkip.setVisibility(View.GONE);
                        } else {
                            furtherSkip.setVisibility(View.VISIBLE);
                        }
                        furtherSkip.setOnClickListener(listener);
                    }
                }

                if (paramMV.paramView.furtherNext != 0) {
                    furtherNext = (View) parentLayout.findViewById(paramMV.paramView.furtherNext);
                    if (furtherNext != null) {
                        if (count == 1) {
                            furtherNext.setVisibility(View.GONE);
                        } else {
                            furtherNext.setVisibility(View.VISIBLE);
                        }
                        furtherNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int posit = pager.getCurrentItem() + 1;
                                if (posit < count) {
                                    pager.setCurrentItem(posit);
                                }
                            }
                        });
                    }
                }

                if (paramMV.paramView.furtherStart != 0) {
                    furtherStart = (View) parentLayout.findViewById(paramMV.paramView.furtherStart);
                    if (furtherStart != null) {
                        if (count == 1) {
                            furtherStart.setVisibility(View.VISIBLE);
                        } else {
                            furtherStart.setVisibility(View.GONE);
                        }
                        furtherStart.setOnClickListener(listener);
                    }
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
                        if (count_1 == position) {
                            if (furtherSkip != null) furtherSkip.setVisibility(View.GONE);
                            if (furtherNext != null) furtherNext.setVisibility(View.GONE);
                            if (furtherStart != null) furtherStart.setVisibility(View.VISIBLE);
                        } else {
                            if (furtherSkip != null) furtherSkip.setVisibility(View.VISIBLE);
                            if (furtherNext != null) furtherNext.setVisibility(View.VISIBLE);
                            if (furtherStart != null) furtherStart.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                pager.setAdapter(adapter);
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
            ViewGroup v = (ViewGroup) inflater.inflate(paramMV.paramView.viewId_1, null);
            Record record = listData.get(position);
            modelToView.RecordToView(record, v, IntroComponent.this, null);
            viewGroup.addView(v);
            return v;
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
            int isc = 1;
            preferences.setSplashScreen(isc);
            String stSc = preferences.getSplashNameScreen();
            if (stSc.length() > 0) {
                activity.onBackPressed();
                String[] stAr = stSc.split(",");
                iBase.startScreen(stAr[isc], false);
            }
        }
    };

}
