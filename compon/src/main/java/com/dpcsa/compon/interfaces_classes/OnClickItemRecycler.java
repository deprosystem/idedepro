package com.dpcsa.compon.interfaces_classes;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dpcsa.compon.json_simple.Record;

public interface OnClickItemRecycler {
    void onClick(RecyclerView.ViewHolder holder, View view, int position, Record record);
}
