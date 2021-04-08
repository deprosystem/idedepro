package com.dpcsa.compon.single;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dpcsa.compon.base.BaseInternetProvider;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.Channel;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.Notice;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.FieldBroadcast;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.tools.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dpcsa.compon.json_simple.Field.TYPE_LIST_RECORD;
import static com.dpcsa.compon.json_simple.Field.TYPE_RECORD;

public class ComponGlob {
    public static String NAME_TOKEN = "token";
    public static String NAME_PROFILE = "profile";
    public static String NAME_PUSH_TOKEN = "push_token";
    public FieldBroadcast profile, token, pushToken;
    public Context context;
    public Map<String, Screen> MapScreen;
    public List<Channel> channels;
    public List<Notice> notices;
    public AppParams appParams;
    public List<Param> paramValues = new ArrayList<>();
    public Record globalData;
    private ComponPrefTool preferences;
    public JsonSimple jsonSimple = new JsonSimple();
    public ViewHandler[] initSettings;
    public static  int countSettings;

    public ComponGlob(Context context, ComponPrefTool preferences) {
        this.context = context;
        this.preferences = preferences;
        countSettings = 0;
        token = new FieldBroadcast(NAME_TOKEN, Field.TYPE_STRING, preferences.getSessionToken());
        pushToken = new FieldBroadcast(NAME_PUSH_TOKEN, Field.TYPE_STRING, preferences.getPushToken());
        MapScreen = new HashMap<String, Screen>();
        globalData = new Record();
        Record record = null;
        try {
            record = (Record) jsonSimple.jsonToModel(preferences.getProfile()).value;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        profile = new FieldBroadcast(NAME_PROFILE, TYPE_RECORD, record);
    }

    public String getNameScreenNotice(String name) {
        Notice not = getNotice(name);
        if (not != null) {
            for (String key : MapScreen.keySet()) {
                Screen scr = MapScreen.get(key);
                if (scr.customFragment != null && scr.customFragment == not.screen) {
                    return scr.nameComponent;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public Notice getNotice(String name) {
        if (name != null && name.length() > 0) {
            for (Notice not : notices) {
                if (name.equals(not.type)) {
                    return not;
                }
            }
        }
        return null;
    }

    public static synchronized void addCountSettings(){
        countSettings ++;
    }

    public void nullifyValue(String name) {
        Notice not = getNotice(name);
        if (not != null) {
            not.nullifyValue();
        }
    }

    public void setParam(Record fields) {
        if (fields == null) return;
        int ik = paramValues.size();
        boolean isParam;
        for (Field f: fields) {
            String name = f.name;
            if (f.value != null) {
                isParam = false;
                for (int i = 0; i < ik; i++) {
                    Param param = paramValues.get(i);
                    if (param.name.equals(name)) {
                        isParam = true;
                        setParamValue(param, f);
                        break;
                    }
                }
                if ( ! isParam) {
                    Param nParam = new Param(name, "");
                    setParamValue(nParam, f);
                    paramValues.add(nParam);
                }
            }
        }
    }

    public void setParamsFromGlob(Record rec, String params, String stN) {
//  В rec заносятся поля из globalData с именем stN в соответствии с перечнем параметров в params
//  Есди в globalData переменная типа TYPE_LIST_RECORD, то в rec записывается поле с именем stN типа TYPE_LIST_RECORD
        String[] globArr = params.split(";");
        if (globArr.length == 0) return;
        Field fg = globalData.getField(stN);
        if (fg != null && fg.value != null) {
            if (fg.type == TYPE_LIST_RECORD) {
                ListRecords listResult = new ListRecords();
                ListRecords lr = (ListRecords) fg.value;
                if (lr != null && lr.size() > 0) {
                    for (Record rr : lr) {
                        Record recItem = new Record();
                        for (String st : globArr) {
                            Field ff = rr.getField(st);
                            if (ff != null) {
                                recItem.add(new Field(st, ff.type, ff.value));
                            }
                        }
                        listResult.add(recItem);
                    }
                }
                Field field = rec.getField(stN);
                if (field == null) {
                    rec.add(new Field(stN, TYPE_LIST_RECORD, listResult));
                } else {
                    field.type = TYPE_LIST_RECORD;
                    field.value = listResult;
                }
            } else if (fg.type == TYPE_RECORD) {
                Record recGl = (Record) fg.value;
                Record recResult = new Record();
                for (String namePar : globArr) {
                    Field fPar = recGl.getField(namePar);
                    if (fPar != null && fPar.value != null) {
                        recResult.add(new Field(namePar, fPar.type, fPar.value));
                    }
                }
                Field field = rec.getField(stN);
                if (field == null) {
                    rec.add(new Field(stN, TYPE_RECORD, recResult));
                } else {
                    field.type = TYPE_RECORD;
                    field.value = recResult;
                }
            }
        }
    }

    public Param getParam(String name) {
        for (Param param : paramValues) {
            if (param.name.equals(name)) {
                return param;
            }
        }
        return null;
    }

    public void setParamValue(Param param, Field f) {
        switch (f.type) {
            case Field.TYPE_STRING:
                param.value = new String((String) f.value);
                break;
            case Field.TYPE_INTEGER:
                param.value = String.valueOf((Integer) f.value);
                break;
            case Field.TYPE_LONG:
                param.value = String.valueOf(f.value);
                break;
            case Field.TYPE_FLOAT:
                param.value = String.valueOf((Float) f.value);
                break;
            case Field.TYPE_DOUBLE:
                param.value = String.valueOf((Double) f.value);
                break;
            case Field.TYPE_BOOLEAN:
                param.value = String.valueOf((Boolean) f.value);
                break;
        }
    }

    public void addParam(String paramName) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                return;
            }
        }
        paramValues.add(new Param(paramName, ""));
    }

    public void addParamValue(String paramName, String paramValue) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                return;
            }
        }
        paramValues.add(new Param(paramName, paramValue));
    }

    public void setParamValue(String paramName, String paramValue) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                param.value = paramValue;
                return;
            }
        }
        paramValues.add(new Param(paramName, paramValue));
    }

    public String installParamName(String paramQuery, String url) {
        String st = "";
        String param = "";
        String sep = "";
        String paramCommon = appParams.commonParameters;
        if (paramCommon != null && paramCommon.length() > 0) {
            param = paramCommon;
            sep = ",";
        }
        if (paramQuery != null && paramQuery.length() > 0) {
            param += sep + paramQuery;
        }

        if (param != null && param.length() > 0) {
            if (url.contains("?")) {
                st = "&";
            } else {
                st = "?";
            }
            String[] paramArray = param.split(Constants.SEPARATOR_LIST);
            sep = "";
            for (String paramOne : paramArray) {
                String par1 = paramOne;
                int i = paramOne.indexOf("=");
                if (i > 0) {
                    par1 = paramOne.substring(0, i);
                }
                for (Param paramV : paramValues) {
                    if (par1.equals(paramV.name)) {
                        String valuePar = paramV.value;
                        if (valuePar != null && valuePar.length() > 0) {
                            st = st + sep + par1 + "=" + valuePar;
                            sep = "&";
                        } else {
                            if (i > 0) {
                                st += sep + paramOne;
                                sep = "&";
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (st.length() == 1) {
            st = "";
        }
        return st;
    }

    public String installParamSlash(String param) {
        String st = "";
        if (param != null && param.length() > 0) {
            String[] paramArray = param.split(Constants.SEPARATOR_LIST);
            for (String par : paramArray) {
                for (Param paramV : paramValues) {
                    if (param.equals(paramV.name)) {
                        if (paramV.value != null && paramV.value.length() > 0) {
                            st = st + "/" + paramV.value;
                        }
                        break;
                    }
                }
            }
        }
        return st;
    }

    public String getParamValue(String nameParam) {
        String vvv = getParamValueIfIs(nameParam);
        if (vvv == null) {
            return "";
        } else {
            return vvv;
        }
//        String np = nameParam;
//        int i = np.indexOf("=");
//        String vv = "";
//        if (i > 0) {
//            np = np.substring(0, i);
//            int i_1 = i + 1;
//            if (i_1 < nameParam.length())
//            vv = nameParam.substring(i_1);
//        }
//        for (Param paramV : paramValues) {
//            if (np.equals(paramV.name)) {
//                if (paramV.value.length() == 0) {
//                    paramV.value = vv;
//                }
//                return paramV.value;
//            }
//        }
//        return "";
    }

    public String getParamValueIfIs(String nameParam) {
        String np = nameParam;
        int i = np.indexOf("=");
        String vv = "";
        if (i > 0) {
            np = np.substring(0, i);
            int i_1 = i + 1;
            if (i_1 < nameParam.length()) {
                vv = nameParam.substring(i_1);
                if (vv.equals("SysDate")) {
                    Calendar c = new GregorianCalendar();
                    long tt = c.getTimeInMillis();
                    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
                    vv = df.format(tt);
                }
            }
        }
        for (Param paramV : paramValues) {
            if (np.equals(paramV.name)) {
                if (paramV.value.length() == 0) {
                    paramV.value = vv;
                }
                return paramV.value;
            }
        }
        return null;
    }

    public String installParam(String param, String url) {
        switch (appParams.typeParameterTransfer) {
            case NAME: return installParamName(param, url);
            case SLASH: return installParamSlash(param);
            default: return "";
        }
    }

    public void delGlobalData(String name) {
        globalData.deleteField(name);
    }

    public View findViewByName(View v, String name) {
        View vS = null;
        ViewGroup vg;
        int id;
        String nameS = "";
        if (v instanceof ViewGroup) {
            vg = (ViewGroup) v;
            int countChild = vg.getChildCount();
            id = v.getId();
            if (id != -1) {
                try {
                    nameS = v.getContext().getResources().getResourceEntryName(id);
                } catch (Resources.NotFoundException e) {
                    nameS = "";
                }
                if (name.equals(nameS)) {
                    return v;
                }
            }
            for (int i = 0; i < countChild; i++) {
                vS = findViewByName(vg.getChildAt(i), name);
                if (vS != null) {
                    return vS;
                }
            }
        } else {
            id = v.getId();
            if (id != -1) {
                try {
                    nameS = v.getContext().getResources().getResourceEntryName(id);
                } catch (Resources.NotFoundException e) {
                    nameS = null;
                }
                if (nameS != null) {
                    if (name.equals(nameS)) return v;
                }
            }
        }
        return vS;
    }

    public Calendar stringToDate(String st) {
        Calendar c;
        String dd = "";
        if (st.indexOf("T") > 0) {
            dd = st.split("T")[0];
        } else {
            dd = st;
        }
        String[] d = dd.split("-");
        c = new GregorianCalendar(Integer.valueOf(d[0]),
                Integer.valueOf(d[1]) - 1,
                Integer.valueOf(d[2]));
        return c;
    }

    public String TextForNumbet(int num, String t1, String t2_4, String t5_9) {
        int n1 = num % 100;
        if (n1 < 21 && n1 > 4) {
            return t5_9;
        }
        n1 = num % 10;
        if (n1 == 1) {
            return t1;
        }
        if (n1 > 1 && n1 < 5) {
            return t2_4;
        }
        return t5_9;
    }

    public Record formErrorRecord(IBase iBase, int statusCode, String message) {
        Record result = new Record();
        String stMes = "";
        if (statusCode < 700) {
            if (message != null && message.length() > 0) {
//Log.d("QWERT","formErrorRecord message="+message);
                Field f = null;
                try {
                    f = jsonSimple.jsonToModel(message);
                } catch (JsonSyntaxException e) {
                    String tit;
                    iBase.logNet(e.getMessage());
                    if (appParams.idStringJSONSYNTAXERROR != 0) {
                        stMes = context.getString(appParams.idStringJSONSYNTAXERROR);
                    } else {
                        stMes = "Error in message";
                    }
                    if (appParams.idStringDefaultErrorTitle == 0) {
                        tit = "StatusCode=" + statusCode;
                    } else {
                        tit = context.getString(appParams.idStringDefaultErrorTitle);
                    }
                    result.add(new Field(Constants.TITLE, Field.TYPE_STRING, tit));
                    result.add(new Field(Constants.MESSAGE, Field.TYPE_STRING, stMes));
                    e.printStackTrace();
                }
                if (f != null && f.value != null) {
                    Field ff = ((Record) f.value).get(0);
                    if (ff.type == TYPE_RECORD) {
                        result = (Record) ff.value;
                    } else {
                        return (Record) f.value;
                    }
                }
            }
        } else {
//            Log.d("QWERT","showDialog 111111 statusCode="+statusCode+" message="+message);
            String title = "";
            if (appParams.idStringDefaultErrorTitle == 0) {
                title = "StatusCode=" + statusCode;
            } else {
                title = context.getString(appParams.idStringDefaultErrorTitle);
            }
            switch (statusCode) {
                case BaseInternetProvider.ERRORINMESSAGE:
                    if (appParams.idStringERRORINMESSAGE != 0) {
                        stMes = context.getString(appParams.idStringERRORINMESSAGE);
                    } else {
                        stMes = "Error in message";
                    }
                    break;
                case BaseInternetProvider.NOCONNECTIONERROR:
                    if (appParams.idStringNOCONNECTIONERROR != 0) {
                        stMes = context.getString(appParams.idStringNOCONNECTIONERROR);
                    } else {
                        stMes = "No connection";
                    }
                    if (appParams.idStringNOCONNECTION_TITLE != 0) {
                        title = context.getString(appParams.idStringNOCONNECTION_TITLE);
                    } else {
                        title = "Missing internet";
                    }
                    break;
                case BaseInternetProvider.TIMEOUT:
                    if (appParams.idStringTIMEOUT != 0) {
                        stMes = context.getString(appParams.idStringTIMEOUT);
                    } else {
                        stMes = "Time out";
                    }
                    break;
                case BaseInternetProvider.NO_HTTP:
                    if (appParams.idStringNO_HTTP != 0) {
                        stMes = context.getString(appParams.idStringNO_HTTP);
                    } else {
                        stMes = "URL error";
                    }
                    break;
                case BaseInternetProvider.SERVERERROR:
                    if (appParams.idStringSERVERERROR != 0) {
                        stMes = context.getString(appParams.idStringSERVERERROR);
                    } else {
                        stMes = "Server error";
                    }
                    break;
                case BaseInternetProvider.JSONSYNTAXERROR:
                    if (appParams.idStringJSONSYNTAXERROR != 0) {
                        stMes = context.getString(appParams.idStringJSONSYNTAXERROR);
                    } else {
                        stMes = "Json syntax error";
                    }
                    break;
                case BaseInternetProvider.NO_AUTH:
                    if (appParams.idStringNO_AUTH != 0) {
                        stMes = context.getString(appParams.idStringNO_AUTH);
                    } else {
                        stMes = "No ayth";
                    }
                    break;
            }
            result.add(new Field(Constants.TITLE, Field.TYPE_STRING, title));
            result.add(new Field(Constants.MESSAGE, Field.TYPE_STRING, stMes));
//            showDialog(title, stMes, click);
        }
        if (appParams.LOG_LEVEL > 0) iBase.logNet("ERROR=" + result.getString(Constants.MESSAGE));
        return result;
    }

    public Record paramToRecord(String param) {
        Record rec = new Record();
        String[] par = param.split(",");
        if (par.length > 0) {
            for (String nameField : par) {
                String value = getParamValue(nameField);
                if (value.length() > 0) {
                    rec.add(new Field(nameField, Field.TYPE_STRING, value));
                }
            }
        }
        return rec;
    }

    public Record paramSetRecord(String param) {
        Record rec = new Record();
        String[] par = param.split(",");
        if (par.length > 0) {
            for (String nameField : par) {
                String np = nameField;
                int i = np.indexOf("=");
                String vv = "";
                if (i > 0) {
                    np = np.substring(0, i);
                    int i_1 = i + 1;
                    if (i_1 < nameField.length()) {
                        vv = nameField.substring(i_1);
                        if (vv.equals("SysDate")) {
                            Calendar c = new GregorianCalendar();
                            long tt = c.getTimeInMillis();
                            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
                            vv = df.format(tt);
                        }
                    }
                }

                Param p = getParam(np);
                String value;
                if (p == null) {
                    addParamValue(np, vv);
                    value = vv;
                } else {
                    if (p.value.length() == 0) {
                        value = vv;
                    } else {
                        value = p.value;
                    }
                }
                rec.add(new Field(np, Field.TYPE_STRING, value));
            }
        }
        return rec;
    }

    public void viewFromVar(View view, String nameVar, String listVar) {
        if (view != null) {
            Field ff = globalData.getField(nameVar);
            Object obj = null;
            if (ff != null) {
                obj = ff.value;
                if (obj instanceof Record) {
                    Record recObj = (Record) obj;
                    for (Field fr : (Record) obj) {
//                        setOneParam(fr);
                        Param param = getParam(fr.name);
                        if (param != null) {
                            setParamValue(param, fr);
                        }
                    }
                    Record rec = null;
                    if (listVar != null && listVar.length() > 0) {
                        String[] ls = listVar.split(",");
                        rec = new Record();
                        for (String st : ls) {
                            Field f = recObj.getField(st);
                            if (f != null) {
                                rec.add(f);
                            }
                        }
                    } else {
                        rec = (Record) obj;
                    }
                    if (view instanceof IComponent) {
                        ((IComponent) view).setData(rec);
                    } else if (view instanceof TextView) {
                        String st = "";
                        String sep = "";
                        for (int i = 0; i < rec.size(); i++) {
                            Field f = rec.get(i);
                            if (f.value != null) {
                                String sti = String.valueOf(f.value);
                                if (sti != null && sti.length() > 0) {
                                    st += sep + sti;
                                    sep = ", ";
                                }
                            }
                        }
                        ((TextView) view).setText(st);
                    } else if (view instanceof ViewGroup) {

                    }
                } else if (obj instanceof Field) {
//                    setOneParam((Field) obj);
                    Param param = getParam(((Field) obj).name);
                    if (param != null) {
                        setParamValue(param, (Field) obj);
                    }
                    if (view instanceof IComponent) {
                        ((IComponent) view).setData(obj);
                    } else if (view instanceof ViewGroup) {

                    }
                }
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = context.getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
