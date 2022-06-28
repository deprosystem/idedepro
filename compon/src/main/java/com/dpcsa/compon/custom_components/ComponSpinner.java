package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;


public class ComponSpinner extends androidx.appcompat.widget.AppCompatSpinner
        implements IComponent, IAlias {

    private Context context;
    private String alias;
    private String listItems;
    private boolean isUI;
    private int MATCH = LayoutParams.MATCH_PARENT;
    private int WRAP = LayoutParams.WRAP_CONTENT;
    private float DENSITY = getResources().getDisplayMetrics().density;
    int dp_24 = (int) (24 * DENSITY);
    int margHead = (int) (3 * DENSITY);
    String[] datList;
    int imgId;

    public ComponSpinner(Context context) {
        super(context);
        this.context = context;
    }

    public ComponSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            alias = a.getString(R.styleable.Simple_alias);
            listItems = a.getString(R.styleable.Simple_listItems);
            imgId = a.getResourceId(R.styleable.Simple_imageId, 0);
        } finally {
            a.recycle();
        }
        if (imgId != 0) {
            setBackgroundColor(0x00000000);
        }
        isUI = listItems != null && listItems.length() > 0;
        if (isUI) {
            datList = listItems.split(",");
            AdapterSp adapt = new AdapterSp(datList);
            setAdapter(adapt);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setData(Object data) {
        if (data == null) return;
        if (isUI) {
            if (data instanceof String) {
                String st = (String) data;
                int ik = datList.length;
                for (int i = 0; i < ik; i++) {
                    if (st.equals(datList[i])) {
                        setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Object getData() {
        if (isUI) {
            int pos = getSelectedItemPosition();
            return datList[pos];
        } else {
            return null;
        }
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        if (isUI) {
            int pos = getSelectedItemPosition();
            return datList[pos];
        } else {
            return null;
        }
    }

    @Override
    public void clearData() {
        setSelection(0);
    }

    private class AdapterSp extends BaseAdapter {
        private String[] data;
        public AdapterSp (String[] dat) {
            data = dat;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position].trim();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            RelativeLayout rr = new RelativeLayout(context);
            LinearLayout.LayoutParams lpll = new LinearLayout.LayoutParams(MATCH, WRAP);
            rr.setLayoutParams(lpll);
            TextView tv = new TextView(context);
            RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(WRAP, WRAP);
            lptv.setMargins(margHead, margHead, margHead, margHead);
            tv.setLayoutParams(lptv);
            tv.setTextSize(14);
            tv.setText(data[position].trim());
            rr.addView(tv);
            return rr;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout rr = new RelativeLayout(context);
            LinearLayout.LayoutParams lpll = new LinearLayout.LayoutParams(MATCH, WRAP);
            rr.setLayoutParams(lpll);
            TextView tv = new TextView(context);
            RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(WRAP, WRAP);
            lptv.setMargins(margHead, margHead, margHead, margHead);
            tv.setLayoutParams(lptv);
            tv.setTextSize(16);
            tv.setText(data[position].trim());
            rr.addView(tv);
            if (imgId != 0) {
                ImageView iv = new ImageView(context);
                RelativeLayout.LayoutParams lpiv = new RelativeLayout.LayoutParams(dp_24, dp_24);
                lpiv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                iv.setLayoutParams(lpiv);
                iv.setImageResource(imgId);
                rr.addView(iv);
            }
            return rr;
        }
    }
}
