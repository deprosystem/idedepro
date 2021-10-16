package com.dpcsa.compon.json_simple;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.custom_components.PlusMinus;
import com.dpcsa.compon.custom_components.ComponImageView;
import com.dpcsa.compon.custom_components.ComponTextView;
//import com.dpcsa.compon.glide.GlideApp;
//import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IBaseComponent;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.single.Injector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import static com.dpcsa.compon.json_simple.Field.TYPE_INTEGER;
import static com.dpcsa.compon.json_simple.Field.TYPE_LIST_RECORD;
import static com.dpcsa.compon.json_simple.Field.TYPE_LONG;
import static com.dpcsa.compon.json_simple.Field.TYPE_RECORD;
import static com.dpcsa.compon.json_simple.Field.TYPE_STRING;

public class WorkWithRecordsAndViews {
    protected Record model;
    protected View view;
    protected Navigator navigator;
    protected View.OnClickListener clickView;
    protected Context context;
    protected String[] param;
    protected Record recordResult;
    protected BaseComponent baseComponent;
    private boolean setParam;
    private Visibility[] visibilityManager;
    private int swipeId, rightId, leftId;
    private final String SYSTEM_TIME = "SYSTEM_TIME", SYSTEM_TIME_SEC = "SYSTEM_TIME_SEC";

    public void RecordToView(Record model, View view) {
        RecordToView(model, view, null, null);
    }

    public void RecordToView(Record model, View view, BaseComponent bc,
                         View.OnClickListener clickView) {
        this.model = model;
        this.view = view;
        if (bc != null) {
            baseComponent = bc;
            navigator = bc.navigator;
            if (navigator != null) {
                for (ViewHandler vh : navigator.viewHandlers) {
                    if (vh.viewId == 0) {
                        view.setOnClickListener(clickView);
                        break;
                    }
                }
            }
            visibilityManager = bc.paramMV.paramView.visibilityArray;
        }
        this.clickView = clickView;
        context = view.getContext();
        setParam = false;
        enumViewChild(view);
    }

    public Record ViewToRecord(View view, String par) {
        recordResult = new Record();
        param = par.split(",");
        for (String st : param) {
            int i = st.indexOf("(");
            if (i > 0) {
                String stN = st.substring(0, i);
                int ik = st.indexOf(")");
                if (ik == -1) {
                    Log.e("SMPL","Ошибка в параметрах " + stN + ". Скорее всего разделитель в скобках не ;");
                    ik = st.length();
                }
                recordResult.add(new Field(stN, TYPE_LIST_RECORD, st.substring(i + 1, ik)));
            } else {
                i = st.indexOf("=");
                if (i > -1) {
                    String stN = st.substring(0, i);
                    int ik = st.indexOf(")");
                    if (ik == -1) {
                        ik = st.length();
                    }
                    String stPar = st.substring(i + 1, ik);
                    switch (stPar) {
                        case SYSTEM_TIME:
                            recordResult.add(new Field(stN, TYPE_STRING, String.valueOf(new Date().getTime())));
                            break;
                        case SYSTEM_TIME_SEC:
                            recordResult.add(new Field(stN, TYPE_STRING, String.valueOf(new Date().getTime() / 1000)));
                            break;
                        default:
                            recordResult.add(new Field(stN, TYPE_STRING, stPar));

                    }
                } else {
                    recordResult.add(new Field(st, TYPE_STRING, null));
                }
            }
        }
        setParam = true;
        enumViewChild(view);
        return recordResult;
    }

    private void enumViewChild(View v) {
        ViewGroup vg;
        int id;
        if (v instanceof ViewGroup) {
            vg = (ViewGroup) v;
            int countChild = vg.getChildCount();
            id = v.getId();
            if (id > -1) {
                setValue(v);
            }
            for (int i = 0; i < countChild; i++) {
                enumViewChild(vg.getChildAt(i));
            }
        } else {
            if (v != null) {
                id = v.getId();
                if (id != -1) {
                    setValue(v);
                }
            }
        }
    }

    private void setRecordField(View v, String name) {
        for (Field f : recordResult) {
            if (f.type != TYPE_LIST_RECORD && f.name.equals(name)) {
                if (v instanceof ComponImageView) {
                    f.type = Field.TYPE_FILE_PATH;
                    f.value = ((ComponImageView) v).getPathImg();
                    break;
                }
                if (v instanceof IComponent) {
                    Object obj = ((IComponent) v).getData();
                    if (obj != null) {
                        if (obj instanceof Long) {
                            f.type = TYPE_LONG;
                            f.value = obj;
                        } else if (obj instanceof Integer) {
                            f.type = TYPE_INTEGER;
                            f.value = obj;
                        } else if (obj instanceof Record) {
                            f.type = TYPE_RECORD;
                            f.value = obj;
                        } else if (obj instanceof String) {
                            f.type = TYPE_STRING;
                            f.value = obj;
                        }
                    } else {
                        f.value = ((IComponent) v).getString();
                    }
                    break;
                }
                if (v instanceof TextView) {
                    f.value = ((TextView) v).getText().toString();
                    break;
                }
            }
        }
    }

    private void setValue(View v) {
        int id = v.getId();
        String st;
        String name = v.getContext().getResources().getResourceEntryName(id);
        if (setParam) {
            setRecordField(v, name);
            return;
        }
        if (v instanceof IAlias) {
            st = ((IAlias) v).getAlias();
            if (st != null && st.length() > 0) {
                name = st;
            }
        }

        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (id == vh.viewId) {
                    v.setOnClickListener(clickView);
                    break;
                }
            }
        }
        if (visibilityManager != null && visibilityManager.length > 0) {
            for (Visibility vis : visibilityManager) {
                if (vis.viewId == id) {
                    if (model.getBooleanVisibility(vis.nameField)) {
                        switch (vis.typeShow) {
                            case 0 :
                                v.setVisibility(View.VISIBLE);
                                break;
                            case 1 :
                                v.setEnabled(true);
                                break;
                        }
                    } else {
                        switch (vis.typeShow) {
                            case 0 :
                                v.setVisibility(View.GONE);
                                break;
                            case 1 :
                                v.setEnabled(false);
                                break;
                        }
                    }
                    break;
                }
            }
        }
        if (model == null) {
            return;
        }
        Field field = model.getField(name);

        if (field != null) {
            if (v instanceof IComponent) {
                if (v instanceof IBaseComponent) {
                    ((IBaseComponent) v).setData(baseComponent.iBase, field.value);
                } else {
                    if (v instanceof PlusMinus) {
                        ((PlusMinus) v).setParam(view, model, baseComponent);
                        ((PlusMinus) v).setData(field.value.toString());
                    } else {
                        ((IComponent) v).setData(field.value);
                    }
                }
                return;
            }
            if (v instanceof TextView) {
                if (field.value instanceof String) {
                    ((TextView) v).setText((String )field.value);
//                    if (v instanceof PlusMinus) {
//                        ((PlusMinus) v).setParam(view, model, baseComponent);
//                        ((PlusMinus) v).setData(field.value.toString());
//                    } else {
//                        ((TextView) v).setText((String )field.value);
//                    }
                    return;
                }
                if (field.value instanceof Long) {
                    if (v instanceof ComponTextView) {
                        st = ((ComponTextView) v).getNumberFormat();
                        if (st != null) {
                            ((ComponTextView) v).setText(new Formatter().format(st, field.value).toString());
                        } else {
                            ((ComponTextView) v).setText(field.value.toString());
                        }
                    } else {
                        ((TextView) v).setText(field.value.toString());
//                        if (v instanceof PlusMinus) {
//                            ((PlusMinus) v).setParam(view, model, baseComponent);
//                            ((PlusMinus) v).setData(field.value.toString());
//                        } else {
//                            ((TextView) v).setText(field.value.toString());
//                        }
                    }
//                    if (v instanceof PlusMinus) {
//                        ((PlusMinus) v).setParam(view, model, baseComponent);
//                    }
                    return;
                }
                if(field.value instanceof Date) {
                    SimpleDateFormat format;
                    if (v instanceof ComponTextView) {
                        st = ((ComponTextView) v).getDateFormat();
                        if (st != null) {
                            ((ComponTextView) v).setText(new Formatter().format(st, field.value).toString());
                        } else {
                            format = new SimpleDateFormat();
                            ((TextView) v).setText(format.format((Date) field.value));
                        }
                    } else {
                        format = new SimpleDateFormat();
                        ((TextView) v).setText(format.format((Date) field.value));
                    }
                    return;
                }
                return;
            }
        } else {
            if (v instanceof PlusMinus) {
                ((PlusMinus) v).setParam(view, model, baseComponent);
            }
        }
        if (v instanceof ImageView) {
            if (field == null) return;
            if (field.type == TYPE_STRING) {
                st = (String) field.value;
                if (st == null || st.equals("null")) {
                    st = "";
                }
                if (st.length() == 0) {
                    ((ImageView) v).setImageDrawable(null);
                    return;
                }
                if (st.contains("/") || st.contains(".")) {
                    if (!st.contains("http")) {
                        st = Injector.getComponGlob().appParams.baseUrl + st;
                    }
                    GlideRequest gr = GlideApp.with(context).load(st);
                    if (v instanceof ComponImageView) {
                        ComponImageView simg = (ComponImageView) v;
                        if (simg.getBlur() > 0) {
                            gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
                        }
                        if (simg.getPlaceholder() > 0) {
                            gr.apply(placeholderOf(simg.getPlaceholder()));
                        }
                        if (simg.isOval()) {
                            gr.apply(circleCropTransform());
                        }
                    }
                    gr.into((ImageView) v);
                } else {
                    if (v instanceof ComponImageView) {
                        ((ImageView) v).setImageDrawable(context
                                .getResources().getDrawable(((ComponImageView) v).getPlaceholder()));
                    } else {
                        ((ImageView) v).setImageResource(context.getResources()
                                .getIdentifier(st, "drawable", context.getPackageName()));
                    }
                }
            } else {
                if (field.type == TYPE_INTEGER) {
                    ((ImageView) v).setImageResource((Integer) field.value);
                }
            }
        }
    }
}
