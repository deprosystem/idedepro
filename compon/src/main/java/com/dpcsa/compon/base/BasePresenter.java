package com.dpcsa.compon.base;

import android.text.Html;
import android.util.Log;
import android.view.View;

import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.network.CacheWork;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.providers.VolleyInternetProvider;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BasePresenter implements BaseInternetProvider.InternetProviderListener {
    private IBase iBase;
    private ParamModel paramModel;
    private Record data;
    Map<String, String> headers;
    private IPresenterListener listener;
    private boolean isCanceled;
    private BaseInternetProvider internetProvider;
    protected JsonSimple jsonSimple = new JsonSimple();
    protected String nameJson, json, url;
    protected int method;
    private ComponGlob componGlob;
    private ComponPrefTool preferences;
    private CacheWork cacheWork;
    private String urlFull;
    private boolean onProgress;
    private View viewProgress;
    
    public BasePresenter(IBase iBase, ParamModel paramModel,
                         Map<String, String> headersPar, Record data, IPresenterListener listener) {
        this.iBase = iBase;
        this.paramModel = paramModel;
        this.data = data;
        this.listener = listener;
        this.headers = headersPar;
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (paramModel.pagination != null && paramModel.pagination.isEnd) {
            return;
        }
        onProgress = ! paramModel.noProgress;
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        cacheWork = Injector.getCacheWork();
        String nameToken = componGlob.appParams.nameTokenInHeader;
        String token = preferences.getSessionToken();
        if (paramModel.auth && (token == null || token.length() == 0)) {
            if (paramModel.authScreen != null && paramModel.authScreen.length() > 0) {
                iBase.startScreen(paramModel.authScreen, false);
            } else {
                onProgress = false;
                error(BaseInternetProvider.NO_AUTH, "NO_AUTH");
            }
            return;
        }
//        String token = componGlob.token;
        if (nameToken.length() > 0 && token.length() > 0) {
            headers.put(nameToken, token);
        }
        String nameTokenPush = componGlob.appParams.nameTokenPushInHeader;
        String tokenPush = preferences.getPushToken();
        if (paramModel.isHeaderPush && nameTokenPush != null && nameTokenPush.length() > 0) {
            if (tokenPush.length() > 0) {
                headers.put(nameTokenPush, tokenPush);
            }
        }

//        headers.put("User-Agent", "android;1.1.0");
        String userAgent = componGlob.appParams.nameVersionInHeader;
        if (userAgent.length() > 0) {
            headers.put(userAgent, componGlob.appParams.valueVersionInHeader);
        }

        String nameLanguage = componGlob.appParams.nameLanguageInHeader;
        if (nameLanguage.length() > 0) {
            headers.put(nameLanguage, preferences.getLocale());
        }
        if (paramModel.pagination != null) {
            headers.put(paramModel.pagination.paginationNameParamPerPage,
                    String.valueOf(paramModel.pagination.paginationPerPage));
            headers.put(paramModel.pagination.paginationNameParamNumberPage,
                    String.valueOf(paramModel.pagination.paginationNumberPage));
        }

        method = paramModel.method;
        if (method == ParamModel.FILTER) {
            method = ParamModel.POST;
        }
        long duration = paramModel.duration;
        String baseUrl = componGlob.appParams.baseUrl;
        if (componGlob.appParams.nameLanguageInURL) {
            String loc = Injector.getPreferences().getLocale();
            if (method == ParamModel.GET) {
                urlFull = baseUrl + loc + paramModel.url;
            } else {
                urlFull = baseUrl + paramModel.url;
            }
        } else {
            if (paramModel.url.startsWith("http")) {
                urlFull = paramModel.url;
            } else {
                urlFull = baseUrl + paramModel.url;
            }
        }
        if (method == ParamModel.GET) {
            String st = componGlob.installParam(paramModel.param, urlFull);
            if (st.equals(Constants.errorInstallParam)) {
                error(BaseInternetProvider.NO_AUTH, Constants.NEED_LOGIN);
                return;
            }
            url = urlFull + st;
        } else {
            url = urlFull;
        }
        if ( ! urlFull.startsWith("http")) {
            error(BaseInternetProvider.NO_HTTP, "");
            return;
        }
        if (duration > 0) {
            nameJson = url;
            json = cacheWork.getJson(nameJson);
            if (json == null) {
                startInternetProvider();
            } else {
                Field ff = null;
                try {
                    ff = jsonSimple.jsonToModel(Html.fromHtml(json).toString());
                } catch (JsonSyntaxException e) {
                    iBase.log(e.getMessage());
                    e.printStackTrace();
                }
                if (ff != null) {
                    Field f = ff;
                    if (f.value instanceof Record) {
                        Record rec = (Record) f.value;
                        Object obj = rec.getValue("data");
                        if (obj != null) {
                            ff = (Field) ((Record)obj).get(0);
                        }
                    }
                    listener.onResponse(ff);
                    if (duration == 1) {
                        onProgress = false;
                        startInternetProvider();
                    }
                } else {
                    startInternetProvider();
                }
            }
        } else {
            startInternetProvider();
        }

    }

    public void startInternetProvider() {
        isCanceled = false;
        Record multiP = formMultiP(data);
        Map<String, File> file = null;
        if (multiP != null && multiP.size() > 0) {
            file = new HashMap<>();
            for (Field f : multiP) {
                File fileM = new File((String) f.value);
                file.put(f.name, fileM);
            }
        }

        if (paramModel.internetProvider == null) {
            internetProvider = new VolleyInternetProvider();
            internetProvider.setParam(method,
                    url, headers, jsonSimple.ModelToJson(data), file, this);
        } else {
            BaseInternetProvider bip = null;
            try {
                bip = (BaseInternetProvider) paramModel.internetProvider.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (bip != null) {
                internetProvider = bip.getThis();
                internetProvider.setParam(method,
                        url, headers, jsonSimple.ModelToJson(data), file, this);
            } else {
                iBase.log("Ошибка создания internetProvider");
            }
        }
        iBase.addInternetProvider(internetProvider);
        if (onProgress) {
            if (paramModel.progressId != 0) {
                viewProgress = iBase.getParentLayout().findViewById(paramModel.progressId);
                if (viewProgress != null) {
                    viewProgress.setVisibility(View.VISIBLE);
                } else {
                    String nn = "";
                    if (iBase.getBaseFragment() != null) {
                        nn = iBase.getBaseFragment().mComponent.nameComponent;
                    } else {
                        nn = iBase.getBaseActivity().mComponent.nameComponent;
                    }
                    iBase.log("0004 Нет view для прогресса в " + nn);
                }
            } else {
                iBase.progressStart();
            }
        }
    }

    public Record formMultiP(Record rec) {
        if (rec == null) return null;
        Record r = new Record();
        for (Field f : rec) {
            if (f.type == Field.TYPE_FILE_PATH) {
                r.add(new Field(f.name, f.type, f.value));
            }
        }
        if (r.size() > 0) {
            int in = rec.size() - 1;
            for (int i = in; i >= 0; i--) {
                if (rec.get(i).type == Field.TYPE_FILE_PATH) rec.remove(i);
            }
            return r;
        } else {
            return null;
        }
    }

    public void cancel() {
        isCanceled = true;
        if (internetProvider != null) {
            internetProvider.cancel();
        }
    }

    @Override
    public void response(String response) {
        if (onProgress) {
            if (paramModel.progressId != 0) {
                if (viewProgress != null) {
                    viewProgress.setVisibility(View.GONE);
                }
            } else {
                iBase.progressStop();
            }
        }
        if (response == null) {
            iBase.showDialog("", "no response", null);
        }
        if (paramModel.duration > 0) {
            cacheWork.addCasche(url,
                    paramModel.duration, response);
        }
        if ( ! isCanceled) {
            if (response.length() == 0) {
                listener.onResponse(new Field("", Field.TYPE_STRING, ""));
            } else {
                Field f = null;
                try {
                    jsonSimple.nameRecToList = paramModel.nameRecToList;
                    f = jsonSimple.jsonToModel(response);
                } catch (JsonSyntaxException e) {
                    iBase.log(e.getMessage());
                    iBase.showDialog(BaseInternetProvider.JSONSYNTAXERROR, e.getMessage(), null);
                    e.printStackTrace();
                }
                if (f != null && f.value != null) {
                    Field ff = f;
                    if (f.value instanceof Record) {
                        Record rec = (Record) f.value;
                        Object obj = rec.getValue("data");
                        if (obj != null) {
                            ff = (Field) ((Record)obj).get(0);
                        }
                    }
                    listener.onResponse(ff);
                } else {
                    iBase.log("Ошибка данных");
                }
            }
        }
    }

    @Override
    public void error(int statusCode, String message) {
        if (onProgress) {
            if (paramModel.progressId != 0) {
                if (viewProgress != null) {
                    viewProgress.setVisibility(View.GONE);
                }
            } else {
                iBase.progressStop();
            }
        }
        if (paramModel.errorShowView == 0) {
            if (paramModel.viewErrorDialog == null || paramModel.viewErrorDialog) {
                iBase.showDialog(statusCode, message, null);
            } else {
                listener.onError(statusCode, message, null);
            }
        } else {
            listener.onError(statusCode, message, null);
        }
    }

}
