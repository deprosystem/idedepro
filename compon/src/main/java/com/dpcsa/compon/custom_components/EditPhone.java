package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;

public class EditPhone extends RelativeLayout implements IComponent, IAlias {

    private Context context;
    private int colorTransparent = 0x00000000;
    private EditTextMask editTextMask;
    private ImageView clearImage;
    private View line;
    private float textSize;

    public EditPhone(Context context) {
        super(context);
        init(context, null);
    }

    public EditPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditPhone(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        int imageId, selectorLine, heightLine;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);

        try {
            imageId = a.getResourceId(R.styleable.Simple_imageId, 0);
            selectorLine = a.getResourceId(R.styleable.Simple_selectorLine, 0);
            heightLine = a.getDimensionPixelSize(R.styleable.Simple_heightLine, 0);
        } finally {
            a.recycle();
        }

        editTextMask = new EditTextMask(context, attrs);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextMask.setLayoutParams(lp);
        addView(editTextMask);
        if (selectorLine != 0) {
            textSize = editTextMask.getTextSize();
            editTextMask.measure(0, 0);
            int h = editTextMask.getMeasuredWidth();
            editTextMask.setBackgroundColor(colorTransparent);
            line = new View(context);
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightLine);
            int offs = (int) (textSize * 1.353 + 18);
            lp.setMargins(0, offs, 0, 0);
            line.setLayoutParams(lp);
            line.setBackgroundResource(selectorLine);
            addView(line);
        }
        if (imageId != 0) {
            clearImage = new ImageView(context);
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            clearImage.setLayoutParams(lp);
            clearImage.setBackgroundResource(imageId);
            addView(clearImage);
            clearImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextMask.setText("");
                }
            });
        }
    }

    @Override
    public void setData(Object data) {
        editTextMask.setData(data);
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Object getData() {
        return editTextMask.getData();
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {
        editTextMask.setOnChangeStatusListener(statusListener);
    }

    @Override
    public String getString() {
        return editTextMask.getString();
    }

}
