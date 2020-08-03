package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.Injector;

public class Gallery extends ViewPager implements IComponent, IAlias {

    private Context context;
    private List<Field> listData;
    private int INDICATOR, placeholder;
    private String alias, baseUrl;
    private PagerIndicator indicator;

    public Gallery(@NonNull Context context) {
       this(context, null);
    }

    public Gallery(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttribute(attrs);
    }

    private void setAttribute(AttributeSet attrs) {
        context = getContext();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            alias = a.getString(R.styleable.Simple_alias);
            INDICATOR = a.getResourceId(R.styleable.Simple_indicator, 0);
            placeholder = a.getResourceId(R.styleable.Simple_placeholder, 0);
        } finally {
            a.recycle();
        }
        baseUrl = Injector.getComponGlob().appParams.baseUrl;
    }

    PagerAdapter adapter = new PagerAdapter() {
        WorkWithRecordsAndViews modelToView = new WorkWithRecordsAndViews();
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            ImageView v = new ImageView(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            v.setLayoutParams(lp);

            Field f2 = listData.get(position);
            String st = (String) f2.value;
            if (st.indexOf("/") > -1) {
                if (!st.contains("http")) {
                    st = baseUrl + st;
                }
            }

            if (placeholder == 0) {
                Glide.with(context)
                        .load((String) st)
                        .into(v);
            } else {
                Glide.with(context)
                        .load((String) st)
                        .apply(new RequestOptions().placeholder(placeholder))
                        .into(v);
            }
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

    @Override
    public void setData(Object data) {
        if (data instanceof List) {
            listData = (List<Field>) data;
            if (INDICATOR != 0) {
                View v = (View) getParent();
                indicator = (PagerIndicator) v.findViewById(INDICATOR);
                indicator.setCount(listData.size());
            }
            setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (INDICATOR != 0) {
                        indicator.setSelect(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setAdapter(adapter);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        return listData;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }
}
