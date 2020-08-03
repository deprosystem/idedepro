package com.dpcsa.compon.base;

import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;

public class BaseProvider {
    protected ListRecords listData;

    public BaseProvider(ListRecords listData) {
        this.listData = listData;
    }

    public void addAll(int position, ListRecords data) {
        listData.addAll(position, data);
    }
    public void setData(ListRecords listData) {
        this.listData = listData;
    }

    public int getCount() {
        return listData.size();
    }
    public int size() {
        return listData.size();
    }

    public void remove(int position) {
        listData.remove(position);
    }

    public Record get(int position) {
        return listData.get(position);
    }
}
