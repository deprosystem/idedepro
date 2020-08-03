package com.dpcsa.compon.custom_components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.Notice;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BadgeTextView extends AppCompatTextView implements IComponent {

    public String[] arrayPush;
    private int badgeColor;
    private List<BroadcastReceiver> messageReceiver = new ArrayList<>();
    private String pushs;
    private int DEFAULT_badgeColor = 0xffF89A6C;
// Через preferences тому що можуть бути повідомлення не тільки від PushFMCService
    private ComponPrefTool preferences;

    public BadgeTextView(Context context) {
        super(context);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        pushs = a.getString(R.styleable.Simple_listPushName);
        badgeColor = a.getColor(R.styleable.Simple_badgeColor, DEFAULT_badgeColor);
        a.recycle();
        preferences = Injector.getPreferences();
        setVisibility(GONE);
        if (pushs != null && pushs.length() > 0) {
            setData(pushs);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getBackground() == null) {
            float r = h / 2f;
            float[] outR = new float[]{r, r, r, r, r, r, r, r};
            ShapeDrawable shapeAccent = new ShapeDrawable(new RoundRectShape(outR, null, null));
            shapeAccent.getPaint().setColor(badgeColor);
            setBackgroundDrawable(shapeAccent);
        }
    }

    @Override
    public void setData(Object data) {
        if (messageReceiver.size() > 0) {
            for (BroadcastReceiver bcr : messageReceiver) {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(bcr);
                bcr = null;
            }
            messageReceiver.clear();
        }
        String dd = (String) data;
        if (dd.length() > 0) {
            arrayPush = dd.split(",");
            BroadcastReceiver broadcastReceiver = null;
            int count = 0;
            for (String stname : arrayPush) {
                String stN = Notice.PREFIX_PUSH + stname;
                count += preferences.getNameInt(stN, 0);
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        procPush(stN);
                    }
                };
                messageReceiver.add(broadcastReceiver);
                LocalBroadcastManager.getInstance(getContext())
                        .registerReceiver(broadcastReceiver, new IntentFilter(stN));
            }
            if (count > 0) {
                setText(String.valueOf(count));
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        } else {
            setVisibility(GONE);
        }
    }

    private void procPush(String namePush) {
        int count = 0;
        for (String st : arrayPush) {
            count += preferences.getNameInt(Notice.PREFIX_PUSH + st, 0);
        }
        if (count > 0) {
            setText(String.valueOf(count));
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }

}
