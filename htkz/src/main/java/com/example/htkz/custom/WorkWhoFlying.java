package com.example.htkz.custom;

import android.view.View;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.custom_components.TextViewNumberGrammar;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.MoreWork;
import com.example.htkz.R;

public class WorkWhoFlying extends MoreWork {

    TextViewNumberGrammar tv;
    View add;

    @Override
    public void setParam(IBase iBase, Screen screen) {
        super.setParam(iBase, screen);
        tv = (TextViewNumberGrammar) parentLayout.findViewById(R.id.kids);
        add = parentLayout.findViewById(R.id.add_kid);
    }

    @Override
    public void afterChangeData(BaseComponent baseComponent) {
        if (baseComponent instanceof RecyclerComponent) {
            RecyclerComponent recycler = (RecyclerComponent) baseComponent;
            int kid = recycler.listData.size();
            if (kid < 4) {
                if (add.getVisibility() == View.GONE) {
                    add.setVisibility(View.VISIBLE);
                }
            } else {
                add.setVisibility(View.GONE);
            }
            tv.setData(kid);
        }
    }
}
