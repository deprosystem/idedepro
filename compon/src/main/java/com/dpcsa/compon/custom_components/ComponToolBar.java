package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dpcsa.compon.R;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.TruncateAt.END;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ComponToolBar extends RelativeLayout {

    private Context context;
    public int imgBack, imgHamburger;
    public int titleColor, imgPadding;
    float titleSize;
    public String titleStr;
    public List<ItemView> listView;
    private int h_tool;
    public ImageView BACK, HAMBURGER;
    public TextView TITLE;
    float DENSITY = getResources().getDisplayMetrics().density;
    float SP_DENSITY = getResources().getDisplayMetrics().scaledDensity;
    private int DIM_12 = (int) ( 12 * DENSITY);
    private int DIM_16 = (int) ( 16 * DENSITY);
    private int DIM_56 = (int) (56 * DENSITY);
    private float SP_20 = 20 * SP_DENSITY;
    private View BLANK_V;
    private int blankId;

    public ComponToolBar(Context context) {
        super(context);
        init(context, null);
    }

    public ComponToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        listView = new ArrayList<>();
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,0, 0);
        try {
            imgBack = a.getResourceId(R.styleable.Simple_imgBack, 0);
            titleStr = a.getString(R.styleable.Simple_titleTool);
            imgHamburger = a.getResourceId(R.styleable.Simple_imgHamburger, 0);
            titleColor = a.getColor(R.styleable.Simple_titleColor, 0xFFffffff);
            titleSize = a.getDimension(R.styleable.Simple_titleSize, SP_20);
            try {
                h_tool = a.getDimensionPixelSize(R.styleable.Simple_android_layout_height, DIM_56);
            } catch (RuntimeException e) {
                h_tool = 0;
            }

            imgPadding = a.getDimensionPixelSize(R.styleable.Simple_imgPadding,  DIM_16);
        } finally {
            a.recycle();
        }

        if (h_tool > 0) {
            setViewEl();
        } else {
            Log.d("SMPL_APP","Не задана высота ComponToolBar");
        }
    }

    private void setViewEl() {
        LayoutParams lp;
        int offsetTitle = 0;

        BLANK_V = new View(context);
        lp = new LayoutParams(1, 1);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        BLANK_V.setLayoutParams(lp);
        blankId = generateViewId();
        BLANK_V.setId(blankId);
        addView(BLANK_V);

        if (imgBack != 0) {
            offsetTitle = h_tool;
            BACK = new ImageView(context);
            lp = new LayoutParams(h_tool, h_tool);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            BACK.setLayoutParams(lp);
            if (imgPadding > 0) {
                BACK.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);
            }
            BACK.setImageResource(imgBack);
            addView(BACK);
        }

        if (imgHamburger != 0) {
            offsetTitle = h_tool;
            HAMBURGER = new ImageView(context);
            lp = new LayoutParams(h_tool, h_tool);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            HAMBURGER.setLayoutParams(lp);
            if (imgPadding > 0) {
                HAMBURGER.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);
            }
            HAMBURGER.setVisibility(GONE);
            HAMBURGER.setImageResource(imgHamburger);
            addView(HAMBURGER);
        }

        TITLE = new TextView(context);
        lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.setMargins(offsetTitle + DIM_12, 0, 0, 0);
        TITLE.setLayoutParams(lp);
        TITLE.setText(titleStr);
        TITLE.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        TITLE.setTextColor(titleColor);
        TITLE.setEllipsize(END);
        TITLE.setMaxLines(1);
        TITLE.setSingleLine();
        addView(TITLE);
    }

    public void setTitle(String name) {
        TITLE.setText(name);
    }

    public View addItem(int id) {
        int ik = listView.size();
        View res = null;
        if (id != 0 && ik < 4) {
            LayoutParams lp;
            String typeId = context.getResources().getResourceTypeName(id);
            switch (typeId) {
                case "drawable":
                    ImageView IMG = new ImageView(context);
                    if (imgPadding > 0) {
                        IMG.setPadding(imgPadding, imgPadding, imgPadding, imgPadding);
                    }
                    IMG.setImageResource(id);
                    lp = new LayoutParams(h_tool, h_tool);
                    res = IMG;
                    setViewInTool(IMG, lp, id);
                    break;
                case "layout":       // View
                    LayoutInflater mInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                    View VI = mInflater.inflate(id, null);
                    LinearLayout vg = new LinearLayout(context);
                    vg.setOrientation(LinearLayout.HORIZONTAL);
                    lp = new LayoutParams(WRAP_CONTENT, h_tool);
                    vg.addView(VI);
                    res = vg;
                    setViewInTool(vg, lp, id);
                    break;
            }
        }
        return res;
    }

    private void setViewInTool(View v, LayoutParams lp, int viewId) {
        int ik = listView.size();
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        if (ik > 0) {
            int idIK = listView.get(ik - 1).id;
            lp.addRule(RelativeLayout.LEFT_OF, idIK);
        } else {
            lp.addRule(RelativeLayout.LEFT_OF, blankId);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        v.setLayoutParams(lp);
        int idV = generateViewId();
        v.setId(idV);
//        if (ik == 0) {
//            v.setVisibility(GONE);
//        }
        ItemView iv = new ItemView(idV, h_tool, v, viewId);
        listView.add(iv);
        addView(v);
    }

    public class ItemView {
        public int id, width;
        public View view;
        public int viewId;

        public ItemView() {
        }

        public ItemView(int id, int width, View view, int viewId) {
            this.id = id;
            this.width = width;
            this.view = view;
            this.viewId = viewId;
        }
    }
}
