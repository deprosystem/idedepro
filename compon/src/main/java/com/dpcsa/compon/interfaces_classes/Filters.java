package com.dpcsa.compon.interfaces_classes;

import android.util.Log;

import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;

import static com.dpcsa.compon.json_simple.Field.TYPE_INTEGER;
import static com.dpcsa.compon.json_simple.Field.TYPE_STRING;

public class Filters {
    public FilterParam[] filterParams;
    public int maxSize;

    public Filters(int maxSize, FilterParam... args) {
        this.maxSize = maxSize;
        filterParams = args;
    }

    public boolean isConditions(Record record) {
        for (FilterParam fp: filterParams) {
//            Object value = record.getValue(fp.nameField);
//            if (value != null) {
//                if (fp.value instanceof String) {
//                    if (! (value instanceof String && ((String) fp.value).equals((String) value))) return false;
//                }
//            } else {
//                return false;
//            }
            Field f = record.getField(fp.nameField);
            if (f != null && f.value != null) {
                switch (f.type) {
                    case TYPE_STRING:

                        break;
                    case TYPE_INTEGER:
                        int r = (Integer) f.value;
                        int fi = (Integer) fp.value;
                        switch (fp.oper) {
                            case equally: return r == fi;
                            case more: return r > fi;
                            case less: return r < fi;
                            default: return false;
                        }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
