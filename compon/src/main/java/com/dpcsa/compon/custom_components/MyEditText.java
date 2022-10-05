package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.dpcsa.compon.R;

public class MyEditText extends AppCompatEditText {
    int idShow, idHide;
    View show, hide;
    public MyEditText(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            idShow = a.getResourceId(R.styleable.Simple_idShowPassword, 0);
            idHide = a.getResourceId(R.styleable.Simple_idHidePassword, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View parrent = getParenView();
        show = parrent.findViewById(idShow) ;
        hide = parrent.findViewById(idHide);
        show.setOnClickListener(listener);
        hide.setOnClickListener(listener);
        hide.setVisibility(GONE);
        show.setVisibility(VISIBLE);
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == idShow) {
                show.setVisibility(GONE);
                hide.setVisibility(VISIBLE);
                setInputType(128);
            } else {
                hide.setVisibility(GONE);
                show.setVisibility(VISIBLE);
                setInputType(129);
            }
        }
    };

    private View getParenView() {
        ViewParent viewRoot = getParent();
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        View vr = (View) view2;
        return vr;
    }
}
