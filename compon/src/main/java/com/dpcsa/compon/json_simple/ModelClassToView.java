package com.dpcsa.compon.json_simple;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModelClassToView {
    protected FieldView[] fieldViewArray;
    protected String nameClass = "";
    protected String TYPE_INT = "int";
    protected String TYPE_TEXT_VIEW = "TextView";
    protected String TYPE_IMAGE_VIEW = "ImageView";
    protected String TYPE_CHART_RING = "ChartRing";

    public void execute(Object model, View view) {
        execute(model, view, null, null);
    }

    public void execute(Object model, View view, String[] navigator, View.OnClickListener clickView) {
        Class c = model.getClass();
        String st = c.getName();
        if ( ! st.equals(nameClass)) {
            nameClass = new String(st);
            fieldViewArray = createMV(model, view, navigator);
        }
        for (int i = 0; i < fieldViewArray.length; i++) {
            FieldView fv = fieldViewArray[i];
            try {
                Field f = c.getDeclaredField(fv.name);
                f.setAccessible(true);
                if (fv.typeModel.equals(TYPE_INT)) {
                    st = String.valueOf(f.getInt(model));
                } else {
                    st = (String) f.get(model);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            View viewItem = view.findViewById(fv.id);
            if (fv.typeView.contains(TYPE_TEXT_VIEW)) {
                ((TextView) viewItem).setText(st);
            } else {
                if (fv.typeView.contains(TYPE_IMAGE_VIEW)) {
                    if (st != null) {
                        if (st.contains("/")) {
                            if (!st.contains("http")) {
                                st = Injector.getComponGlob().appParams.baseUrl + st;
                            }
                            Glide.with(view.getContext())
                                    .load(st)
                                    .into((ImageView) viewItem);
                        } else {
                            ((ImageView) viewItem).setBackgroundResource(view.getContext().getResources()
                                    .getIdentifier(st, "drawable", view.getContext().getPackageName()));
                        }
                    }
                } else {
                    if (fv.typeView.contains(TYPE_CHART_RING)) {
//                        ((ChartRing) viewItem).setData(formData(0.3f), 0f);
                    }
                }
            }
            if (fv.isClick) {
                viewItem.setOnClickListener(clickView);
            }
        }
    }

    private FieldView[] createMV(Object model, View view, String[] navigator) {
        FieldView[] mv;
        List<FieldView> fieldViews = new ArrayList<>();
        nameViewChild(view, fieldViews);
        Class c = model.getClass();
        Field[] fields = c.getDeclaredFields();
        int countField = 0;
        for (Field f: fields) {
            Class fieldType = f.getType();
            String name =f.getName();
            for (FieldView fv: fieldViews) {
                if (fv.name.equals(name)) {
                    fv.typeModel = fieldType.getCanonicalName();
                    countField++;
                    break;
                }
            }
        }
        if (navigator != null && navigator.length != 0) {
            countField = 0;
            int ik = navigator.length;
            for (FieldView fv: fieldViews) {
                String name = fv.name;
                for (int i = 0; i < ik; i++) {
                    if (name.equals(navigator[i])) {
                        fv.isClick = true;
                        break;
                    }
                }
                if (fv.isClick || fv.typeModel.length() > 0) {
                    countField++;
                }
            }
        }
        mv = new FieldView[countField];
        int i = 0;
        for(FieldView fv: fieldViews) {
            if (fv.typeModel.length() > 0) {
                mv[i] = fv;
                Log.d("QWERT","NAME="+fv.name+" TYPE="+fv.typeModel);
                i++;
            }
        }
        return mv;
    }

    private void nameViewChild(View v, List<FieldView> fieldViews) {
        ViewGroup vg;
        int id;
        String name;
        if (v instanceof ViewGroup) {
            vg = (ViewGroup) v;
            int countChild = vg.getChildCount();
            id = v.getId();
            if (id > -1) {
                name = v.getContext().getResources().getResourceEntryName(id);
                fieldViews.add(new FieldView(name, v.getClass().getSimpleName(), id));
            }
            for (int i = 0; i < countChild; i++) {
                nameViewChild(vg.getChildAt(i), fieldViews);
            }
        } else {
            id = v.getId();
            if (id != -1) {
                name = v.getContext().getResources().getResourceEntryName(id);
                fieldViews.add(new FieldView(name, v.getClass().getSimpleName(), id));
            }
        }
    }

    public class FieldView {
        public String name;
        public String typeView;
        public String typeModel;
        public int id;
        public boolean isClick;
        public FieldView(String n, String t, int i) {
            name = n;
            typeView = t;
            id = i;
            typeModel = "";
            isClick = false;
        }
    }
}
