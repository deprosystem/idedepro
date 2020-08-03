package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dpcsa.compon.R;

import java.util.ArrayList;
import java.util.List;

public class BaseComponList extends LinearLayout {

    protected Context context;
    private boolean isTag = false;
    protected BaseStaticListAdapter adapter;
    protected List<View> listV = new ArrayList<>();
    protected int ITEM_LAYOUT_ID;
    private int GAP_BETWEEN_TAGS_HORIS, GAP_BETWEEN_TAGS_VERT;
    private int GAP_BETWEEN_TAGS_DEFAULT = 5;
    private int HEIGTH_TAGS;
    private int HEIGTH_TAGS_DEFAULT = 64;
    private int WIDTH_TAGS;
    private int WIDTH_TAGS_DEFAULT = 250;
    protected int evenColor;

    public BaseComponList(Context context) {
        this(context, null);
    }

    public BaseComponList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseComponList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttributes(attrs);
    }

    protected void setAttributes(AttributeSet attrs){
        setOrientation(VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ComponList);
        GAP_BETWEEN_TAGS_HORIS = (int)a.getDimension(R.styleable.ComponList_gapBetweenTagsHoris, GAP_BETWEEN_TAGS_DEFAULT);
        GAP_BETWEEN_TAGS_VERT = (int)a.getDimension(R.styleable.ComponList_gapBetweenTagsVert, GAP_BETWEEN_TAGS_DEFAULT);
        HEIGTH_TAGS = (int)a.getDimension(R.styleable.ComponList_heightTags, HEIGTH_TAGS_DEFAULT);
        WIDTH_TAGS = (int)a.getDimension(R.styleable.ComponList_widthTagsMax, WIDTH_TAGS_DEFAULT);
        ITEM_LAYOUT_ID = a.getResourceId(R.styleable.ComponList_itemLayoutId, 0);
        evenColor = a.getColor(R.styleable.ComponList_evenColor, 0);
        a.recycle();
    }

    public void setAdapter(BaseStaticListAdapter lsa, boolean isTag){
        adapter = lsa;
        adapter.baseStaticList = this;
        this.isTag = isTag;
        formView();
    }

    public void formView(){
        listV.clear();
        View v = null;
        int width, widthAll;
        removeAllViews();
        int ik = adapter.getCount();
        if (isTag) {
            LayoutParams lp_W_W = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams lpM = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lpM.setMargins(0, 0, GAP_BETWEEN_TAGS_HORIS, 0);
            LinearLayout line = newLayout(context, lp_W_W);
            addView(line);
            widthAll = 0;
            for (int i = 0; i < ik; i++){
                v = adapter.getView(i);
                listV.add(v);
                v.setOnClickListener(onClick);
                v.setLayoutParams(lpM);
                v.measure(0, 0);
                width = v.getMeasuredWidth()+GAP_BETWEEN_TAGS_HORIS;
                widthAll += width;
                if (widthAll<WIDTH_TAGS) line.addView(v);
                else {
                    line = newLayout(context, lp_W_W);
                    addView(line);
                    line.addView(v);
                    widthAll = width;
                }
            }
        }
        else
            for (int i = 0; i < ik; i++){
                v = adapter.getView(i);
                listV.add(v);
                v.setOnClickListener(onClick);
                addView(v);
            }
    }

    public List<View> getListViews(){
        return listV;
    }

    public View getView(int position) {
        return listV.get(position);
    }

    public int getPosition(View view){
        int ik = listV.size();
        for (int i = 0; i < ik; i++) {
            if (listV.get(i) == view) return i;
        }
        return -1;
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.onClickView(v, null, getPosition(v));
        }
    };

    OnClickListener onClickElement = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parent, current = null, before;
            int position;
            parent = v;
            do {
                before = current;
                current = parent;
                parent = (View) current.getParent();
            } while (!(parent instanceof BaseComponList));
            if (isTag) {
                position = getPosition(before);
            } else {
                position = getPosition(current);
            }
            if (position > -1)
                adapter.onClickView(current, v, position);
        }
    };

    public LinearLayout newLayout(Context context, ViewGroup.LayoutParams lp){
        LinearLayout line1 = new LinearLayout(context);
        line1.setLayoutParams(lp);
        line1.setPadding(0, GAP_BETWEEN_TAGS_VERT, 0, 0);
        line1.setOrientation(LinearLayout.HORIZONTAL);
        return line1;
    }

//    public void notifyDataSetChanged() {
//        formView();
//    }

//    public interface BaseStaticListAdapter {
//        int getCount();
//        View getView(int position);
//        void onClickView(View view, View viewElrment, int position);
//    }

    public interface OnCallBack {
        void OnClick(View view, View viewElrment, int position);
    }
}
