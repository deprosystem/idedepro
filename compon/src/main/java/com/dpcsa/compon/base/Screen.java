package com.dpcsa.compon.base;

import android.view.View;

import com.dpcsa.compon.components.AutoAutch;
import com.dpcsa.compon.components.BarcodeComponent;
import com.dpcsa.compon.components.CalendarComponent;
import com.dpcsa.compon.components.ContainerComponent;
import com.dpcsa.compon.components.DateDiapasonComponent;
import com.dpcsa.compon.components.DrawerComponent;
import com.dpcsa.compon.components.EditGalleryComponent;
import com.dpcsa.compon.components.EnabledComponent;
import com.dpcsa.compon.components.IntroComponent;
import com.dpcsa.compon.components.LoadDbComponent;
import com.dpcsa.compon.components.MapComponent;
import com.dpcsa.compon.components.MenuBComponent;
import com.dpcsa.compon.components.MenuBottomComponent;
import com.dpcsa.compon.components.MenuComponent;
import com.dpcsa.compon.components.ModelComponent;
import com.dpcsa.compon.components.PagerFComponent;
import com.dpcsa.compon.components.PagerVComponent;
import com.dpcsa.compon.components.PanelComponent;
import com.dpcsa.compon.components.PanelEnterComponent;
import com.dpcsa.compon.components.PhotoComponent;
import com.dpcsa.compon.components.PlusMinusComponent;
import com.dpcsa.compon.components.PopUpComponent;
import com.dpcsa.compon.components.RecognizeVoiceComponent;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.components.SearchComponent;
import com.dpcsa.compon.components.SpinnerComponent;
import com.dpcsa.compon.components.SequenceComponent;
import com.dpcsa.compon.components.StaticListComponent;
import com.dpcsa.compon.components.SubscribeComponent;
import com.dpcsa.compon.components.SubscribeFirebase;
import com.dpcsa.compon.components.SwitchComponent;
import com.dpcsa.compon.components.TagsComponent;
//import com.dpcsa.compon.components.ToolBarCopy;
import com.dpcsa.compon.components.ToolBarComponent;
import com.dpcsa.compon.components.ToolMenu;
//import com.dpcsa.compon.components.ToolMenu;
import com.dpcsa.compon.components.TotalComponent;
import com.dpcsa.compon.components.YouTubePlayerComponent;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.MoreWork;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.PushNavigator;
import com.dpcsa.compon.interfaces_classes.SetData;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamMap;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.param.ParamView;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.dpcsa.compon.param.ParamComponent.TC.CUSTOM;
import static com.dpcsa.compon.param.ParamComponent.TC.RECYCLER;

public class Screen<T>{
    public String nameComponent;
    public List<ParamComponent> listComponents;
    public List<SetData> listSetData;
    public String title;
    public int titleId;
    public String args;
    public int fragmentLayoutId;
    public enum TYPE_VIEW {ACTIVITY, FRAGMENT, CUSTOM_FRAGMENT, CUSTOM_ACTIVITY};
    public TYPE_VIEW typeView;
    public Navigator navigator, startNavig, stopNavig;
    public PushNavigator pushNavigator;
    public ICustom iCustom;
    public Class<T> customFragment;
    public Class<T> additionalWork;
    public MoreWork moreWork;
    public Constants.AnimateScreen animateScreen;
    public ItemSetValue[] itemSetValues;
    public int keyBack;
    public boolean transparentScreen = false;
    public Boolean isFullScreen;

    public void setCustom(ICustom iCustom) {
        this.iCustom = iCustom;
        for (ParamComponent pc : listComponents) {
            pc.baseComponent.iCustom = iCustom;
        }
    }

    public Screen(String name, int layoutId, String title, String formatParams) {
        this.title = title;
        this.args = formatParams;
        this.nameComponent = name;
        this.fragmentLayoutId = layoutId;
        listComponents = new ArrayList<>();
    }

    public Screen(String name, int layoutId) {
        this.title = "";
        this.args = null;
        this.nameComponent = name;
        this.fragmentLayoutId = layoutId;
        listComponents = new ArrayList<>();
    }

    public Screen(String name, Class<T> customFragment) {
        this.nameComponent = name;
        this.customFragment = customFragment;
        listComponents = new ArrayList<>();
    }

    public T getCustomFragment() {
        try {
            return (T) customFragment.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Screen animate(Constants.AnimateScreen animate) {
        animateScreen = animate;
        return this;
    }

    public Screen fullScreen(boolean show) {
        isFullScreen = show;
        return this;
    }

    public Screen transparent() {
        transparentScreen = true;
        return this;
    }

    public Screen list(ParamModel paramModel, ParamView paramView) {
        return component(RECYCLER, paramModel, paramView, null);
    }

    public Screen list(ParamModel paramModel, ParamView paramView, Navigator navigator) {
        return component(RECYCLER, paramModel, paramView, navigator);
    }

    public Screen component(ParamComponent.TC type, ParamModel paramModel,
                               ParamView paramView) {
        return component(type, paramModel, paramView, null);
    }

    public Screen component(ParamComponent.TC type, ParamModel paramModel) {
        return component(type, paramModel, null, null);
    }

    public Screen component(ParamComponent.TC type,
                               ParamView paramView) {
        return component(type, null, paramView, null);
    }

    public Screen component(ParamComponent.TC type, ParamModel paramModel,
                               ParamView paramView,
                               Navigator navigator) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = type;
        paramComponent.paramModel = paramModel;
        paramComponent.paramView = paramView;
        paramComponent.navigator = navigator;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen component(@NonNull Class<T> typeClass, ParamModel paramModel,
                            ParamView paramView,
                            Navigator navigator) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = CUSTOM;
        paramComponent.typeClass = typeClass;
        paramComponent.paramModel = paramModel;
        paramComponent.paramView = paramView;
        paramComponent.navigator = navigator;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen addWork(Class<T> additionalWork) {
        ParamComponent paramComponent = listComponents.get(listComponents.size() - 1);
        paramComponent.additionalWork = additionalWork;
        return this;
    }

    public Screen eventFrom(int eventComponent) {
        ParamComponent paramComponent = listComponents.get(listComponents.size() - 1);
        paramComponent.eventComponent = eventComponent;
        return this;
    }

    public Screen noActualStart() {
        ParamComponent paramComponent = listComponents.get(listComponents.size() - 1);
        paramComponent.startActual = false;
        return this;
    }

    public Screen noHandlersStart() {
        ParamComponent paramComponent = listComponents.get(listComponents.size() - 1);
        paramComponent.startNoHandlers = true;
        return this;
    }

    public Screen menu(ParamModel paramModel, ParamView paramView) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MENU;
        paramComponent.paramModel = paramModel;
        paramComponent.paramView = paramView;
        paramComponent.paramView.maxItemSelect = -1;
        paramComponent.navigator = new Navigator(new ViewHandler("nameFunc"));
        listComponents.add(paramComponent);
        return this;
    }

    public Screen switchComponent(int viewId, Navigator navigator, Navigator navigatorOff) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.SWITCH;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.navigator = navigator;
        paramComponent.navigatorOff = navigatorOff;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentSubscribe(int viewId, @NonNull String urlSub, @NonNull String urlUnsub) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.SUBSCRIBE;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.st1 = urlSub;
        paramComponent.st2 = urlUnsub;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen subscribeTopic(int viewId, @NonNull String nameTopic) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.SUBSCRIBE_TOPIC;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.st1 = nameTopic;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen menuBottom(int viewId, String ... args) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MENU_BOTTOM;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.paramView.screens = args;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen menuBottom(ParamModel paramModel, ParamView paramView) {
        return menuBottom(paramModel, paramView, navigator);
    }

    public Screen menuBottom(ParamModel paramModel, ParamView paramView, Navigator ... navigator) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MENU_B;
        paramComponent.paramModel = paramModel;
        paramComponent.paramView = paramView;
        paramComponent.arrNavigator = navigator;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentMap(int viewId, ParamModel paramModel, ParamMap paramMap,
                                  Navigator navigator) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MAP;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.paramModel = paramModel;
        paramComponent.navigator = navigator;
        paramComponent.paramMap = paramMap;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentMap(int viewId, ParamMap paramMap) {
        return componentMap(viewId, null, paramMap, null);
    }

    public Screen componentYoutube(int viewId) {
        return componentYoutube(viewId, "");
    }

    public Screen componentYoutube(int viewId, String source) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.YOU_TUBE;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.paramView.fieldType = source;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen drawer(int viewId, int containerId, int drawerId, String screensContainer, String screensDrawer) {
        String[] screens = {screensContainer, screensDrawer};
        int[] id = {containerId, drawerId};
        return component(ParamComponent.TC.DRAWER, new ParamView(viewId, screens, id));
    }

    public Screen plusMinus(int editId, int plusId, int minusId) {
        return component(ParamComponent.TC.PLUS_MINUS, new ParamView(editId, plusId, minusId));
    }

    public Screen plusMinus(int editId, int plusId, int minusId, Navigator navigator, Multiply... args) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PLUS_MINUS;
        paramComponent.paramModel = null;
        paramComponent.paramView = new ParamView(editId, plusId, minusId);
        paramComponent.navigator = navigator;
        paramComponent.multiplies = args;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentDateDiapason(int viewId) {
        return component(ParamComponent.TC.DATE_DIAPASON, new ParamView(viewId));
    }

    public Screen calendar(int viewId, String param) {
        ParamView pv = new ParamView(viewId);
        pv.selectNameField = param; //  Назва параметру для зберігання дати
        return component(ParamComponent.TC.CALENDAR, pv);
    }

    public Screen componentBarcode(int ... view) {
        return component(ParamComponent.TC.BARCODE, new ParamView(view[0], view));
    }

    public Screen componentSearch(int viewIdEdit, ParamModel paramModel, int recyclerId) {
        return componentSearch(viewIdEdit, paramModel, recyclerId, false, 3, 700);
    }

    public Screen componentSearch(int viewIdEdit, ParamModel paramModel, int recyclerId,
                                  boolean hideRecycler, int minLen, int delayMillis) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.SEARCH;
        paramComponent.paramModel = paramModel;
        paramComponent.viewSearchId = viewIdEdit;
        paramComponent.paramView = new ParamView(0);
        paramComponent.paramView.indicatorId = recyclerId;
        paramComponent.eventComponent = viewIdEdit;
        paramComponent.minLen = minLen;
        paramComponent.delayMillis = delayMillis;
        paramComponent.hide = hideRecycler;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen editGallery(int viewId, int addId, int delId, int moveId, int idTextPermits, String query) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.EDIT_GALLERY;
        paramComponent.paramView = new ParamView(viewId, new int[] {addId, delId, moveId});
        paramComponent.paramForPathFoto = null;
        paramComponent.paramView.idStringExtra = idTextPermits;
        paramComponent.paramModel = new ParamModel(query);
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentPhoto(int viewClick, int imageView, int idTextPermits) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PHOTO;
        paramComponent.paramView = new ParamView(viewClick, imageView);
        paramComponent.paramForPathFoto = null;
        paramComponent.paramView.idStringExtra = idTextPermits;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentPhoto(int viewClick, int[] imageView, int idTextPermits) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PHOTO;
        paramComponent.paramView = new ParamView(viewClick, imageView);
        paramComponent.paramForPathFoto = null;
        paramComponent.paramView.idStringExtra = idTextPermits;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentPhoto(int viewClick, int imageView, int idTextPermits, String paramForPathFoto) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PHOTO;
        paramComponent.paramView = new ParamView(viewClick, imageView);
        paramComponent.paramForPathFoto = paramForPathFoto;
        paramComponent.paramView.idStringExtra = idTextPermits;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentPhoto(int viewClick, int[] imageView, int idTextPermits, String paramForPathFoto) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PHOTO;
        paramComponent.paramView = new ParamView(viewClick, imageView);
        paramComponent.paramForPathFoto = paramForPathFoto;
        paramComponent.paramView.idStringExtra = idTextPermits;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentModel(String nameModel, ParamModel paramModel) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MODEL;
        paramComponent.name = nameModel;
        paramComponent.paramModel = paramModel;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentModel(ParamModel paramModel) {
        return componentModel(ParamModel.PARENT_MODEL, paramModel);
    }

    public Screen fragmentsContainer(int fragmentsContainerId) {
        return fragmentsContainer(fragmentsContainerId, null);
    }

    public Screen fragmentsContainer(int fragmentsContainerId, String screen) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.CONTAINER;
        paramComponent.fragmentsContainerId = fragmentsContainerId;
        paramComponent.startScreen = screen;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen autoAutch(ParamModel mod, Record rec, Map<String, String> head, ActionsAfterResponse after, String nextScreen) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.AUTO_AUTCH;
        paramComponent.name = nextScreen;
        paramComponent.paramModel = mod;
        paramComponent.paramModel.record = rec;
        paramComponent.paramModel.head = head;
        paramComponent.after = after;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentIntro(ParamModel model, int pager, int layoutId, int indicator, int skip, int next, int start) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.INTRO;
        paramComponent.paramModel = model;
        paramComponent.paramView = new ParamView(pager);
        paramComponent.paramView.indicatorId = indicator;
        paramComponent.paramView.viewId_1 = layoutId;
        paramComponent.paramView.furtherSkip = skip;
        paramComponent.paramView.furtherNext = next;
        paramComponent.paramView.furtherStart = start;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentSequence(String intro, String auth, String main) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.SEQUENCE;
        paramComponent.intro = intro;
        if (paramComponent.intro == null) {
            paramComponent.intro = "";
        }
        paramComponent.auth = auth;
        if (paramComponent.auth == null) {
            paramComponent.auth = "";
        }
        paramComponent.main = main;
        if (paramComponent.main == null) {
            paramComponent.main = "";
        }
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentTotal(int viewId, int viewIdWithList, Visibility[] visbil, String ... nameFields) {
        return componentTotal(viewId, viewIdWithList, 0, visbil, nameFields);
    }

    public Screen componentTotal(int viewId, int viewIdWithList, int viewEvent, Visibility[] visibil, String ... nameFields) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.TOTAL;
        ParamView pv = new ParamView(viewId);
        paramComponent.paramView = pv;
        pv.viewIdWithList = viewIdWithList;
        pv.visibilityArray = visibil;
        pv.nameFields = nameFields;
        paramComponent.eventComponent = viewEvent;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen loadDb(String table, String url, long duration, String alias) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.LOAD_DB;
        ParamModel pm = new ParamModel();
        pm.updateTable = table;
        pm.updateUrl = url;
        pm.duration = duration;
        pm.updateAlias = alias;
        paramComponent.paramModel = pm;
        listComponents.add(paramComponent);
        return this;
    }
/*
    public Screen toolBar(int viewId) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.TOOL;
        paramComponent.paramModel = null;
        paramComponent.paramView = new ParamView(viewId);
//        paramComponent.toolMenu = null;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen toolBar(int viewId, com.dpcsa.compon.interfaces_classes.ToolMenu menu) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.TOOL;
        paramComponent.paramModel = null;
        paramComponent.paramView = new ParamView(viewId);
//        paramComponent.toolMenu = menu;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen toolBar(int viewId, int viewBack, int viewHamb, int viewLogo, int overImg, com.dpcsa.compon.interfaces_classes.ToolMenu menu) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.TOOL;
        paramComponent.paramModel = null;
        paramComponent.paramView = new ParamView(viewId, new int[] {viewBack, viewHamb, viewLogo, overImg});
//        paramComponent.toolMenu = menu;
        listComponents.add(paramComponent);
        return this;
    }
/*
    public Screen toolBarModify(ModifierTool... args) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.MODIFY_TOOL;
        paramComponent.paramModel = null;
        paramComponent.paramView = null;
        paramComponent.modifierTools = args;
        listComponents.add(paramComponent);
        return this;
    }
*/
    public Screen componentEditPhone(int viewId) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.PHONE;
        paramComponent.paramView = new ParamView(viewId);
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentButton(int viewId,
                                     Navigator navigator,
                                     int... mustValid) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.BUTTON;
        paramComponent.mustValid = mustValid;
        paramComponent.paramView = new ParamView(viewId);
        paramComponent.navigator = navigator;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentRecognizeVoice(int clickViewId, int textViewResultId) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.RECOGNIZE_VOICE;
        paramComponent.paramView = new ParamView(clickViewId, textViewResultId);
        listComponents.add(paramComponent);
        return this;
    }

    public Screen componentRecognizeVoice(int clickViewId, String param, ActionsAfterResponse after) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.RECOGNIZE_VOICE;
        paramComponent.paramView = new ParamView(clickViewId);
        paramComponent.startScreen = param;
        paramComponent.after = after;
        listComponents.add(paramComponent);
        return this;
    }

    public Screen navigator(ViewHandler ... handlers) {
        this.navigator = new Navigator(handlers);
        if (handlers.length > 0) {
            for (ViewHandler vh : handlers) {
                if (vh.type == ViewHandler.TYPE.KEY_BACK) {
                    keyBack = vh.viewId;
                    break;
                }
            }
        }
        return this;
    }

    public Screen startNavigator(ViewHandler ... handlers) {
        this.startNavig = new Navigator(handlers);
        return this;
    }

    public Screen stopNavigator(ViewHandler ... handlers) {
        this.stopNavig = new Navigator(handlers);
        return this;
    }

    public Screen pushNavigator(PushHandler... handlers) {
        this.pushNavigator = new PushNavigator(handlers);
        return this;
    }

    public Screen setValue(ItemSetValue ... item) {
        itemSetValues = item;
        return this;
    }

    public Screen enabled(int viewId, int ... validId) {
        ParamComponent paramComponent = new ParamComponent();
        paramComponent.type = ParamComponent.TC.ENABLED;
        paramComponent.mustValid = validId;
        paramComponent.paramView = new ParamView(viewId);
        listComponents.add(paramComponent);
        return this;
    }

    public Screen setDataParam(int viewId, String nameParam, int source) {
        if (listSetData == null) {
            listSetData = new ArrayList<>();
        }
        listSetData.add(new SetData(viewId, nameParam, source));
        return this;
    }

    public Screen actualReceiver(String name) {
        int i = listComponents.size();
        if (i > 0) {
            ParamComponent pc = listComponents.get(i - 1);
            if (pc != null) {
                pc.nameReceiver = name;
            }
        }
        return this;
    }

    public BaseComponent getComponent(int viewId) {
        for (ParamComponent cMV : listComponents) {
            if (cMV.paramView.viewId == viewId) {
                return cMV.baseComponent;
            }
        }
        return null;
    }

    public View getComponentView(int viewId) {
        for (ParamComponent cMV : listComponents) {
            if (cMV.paramView.viewId == viewId) {
                BaseComponent bc = cMV.baseComponent;
                if (bc != null) {
                    return bc.viewComponent;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public BaseComponent getComponent(ParamComponent.TC type) {
        for (ParamComponent cMV : listComponents) {
            if (cMV.type == type) {
                return cMV.baseComponent;
            }
        }
        return null;
    }

    public boolean isComponentByType(ParamComponent.TC type) {
        for (ParamComponent cMV : listComponents) {
            if (cMV.type == type) {
                return true;
            }
        }
        return false;
    }

    public void initComponents(IBase iBase) {
        if (additionalWork != null) {
            try {
                moreWork = (MoreWork) additionalWork.newInstance();
                moreWork.setParam(iBase, this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (ParamComponent cMV : listComponents) {
            cMV.moreWork = moreWork;
            switch (cMV.type) {
                case PANEL :
                    new PanelComponent(iBase, cMV, this);
                    break;
                case PANEL_ENTER:
                    new PanelEnterComponent(iBase, cMV, this);
                    break;
                case SPINNER :
                    new SpinnerComponent(iBase, cMV, this);
                    break;
                case RECYCLER :
                    new RecyclerComponent(iBase, cMV, this);
                    break;
                case SEQUENCE :
                    new SequenceComponent(iBase, cMV, this);
                    break;
                case MENU :
                    new MenuComponent(iBase, cMV, this);
                    break;
                case STATIC_LIST :
                    new StaticListComponent(iBase, cMV, this);
                    break;
                case PAGER_V:
                    new PagerVComponent(iBase, cMV, this);
                    break;
                case PAGER_F:
                    new PagerFComponent(iBase, cMV, this);
                    break;
                case MODEL:
                    new ModelComponent(iBase, cMV, this);
                    break;
                case CONTAINER:
                    new ContainerComponent(iBase, cMV, this);
                    break;
                case MAP:
                    new MapComponent(iBase, cMV, this);
                    break;
                case TOTAL:
                    new TotalComponent(iBase, cMV, this);
                    break;
                case SEARCH:
                    new SearchComponent(iBase, cMV, this);
                    break;
                case INTRO:
                    new IntroComponent(iBase, cMV, this);
                    break;
                case EDIT_GALLERY:
                    new EditGalleryComponent(iBase, cMV, this);
                    break;
                case DRAWER:
                    new DrawerComponent(iBase, cMV, this);
                    break;
                case PHOTO:
                    new PhotoComponent(iBase, cMV, this);
                    break;
                case POP_UP:
                    new PopUpComponent(iBase, cMV, this);
                    break;
                case RECOGNIZE_VOICE:
                    new RecognizeVoiceComponent(iBase, cMV, this);
                    break;
                case PLUS_MINUS:
                    new PlusMinusComponent(iBase, cMV, this);
                    break;
                case BARCODE:
                    new BarcodeComponent(iBase, cMV, this);
                    break;
                case DATE_DIAPASON:
                    new DateDiapasonComponent(iBase, cMV, this);
                    break;
                case LOAD_DB:
                    new LoadDbComponent(iBase, cMV, this);
                    break;
                case MENU_BOTTOM:
                    new MenuBottomComponent(iBase, cMV, this);
                    break;
                case MENU_B:
                    new MenuBComponent(iBase, cMV, this);
                    break;
                case ENABLED:
                    new EnabledComponent(iBase, cMV, this);
                    break;
                case YOU_TUBE:
                    new YouTubePlayerComponent(iBase, cMV, this);
                    break;
                case TOOL:
                    ToolBarComponent toolBar = new ToolBarComponent(iBase, cMV, this);
                    iBase.setToolBar(toolBar);
                    break;
                case TOOL_MENU:
                    ToolMenu toolMenu = new ToolMenu(iBase, cMV, this);
                    break;
                case CALENDAR:
                    new CalendarComponent(iBase, cMV, this);
                    break;
                case SWITCH:
                    new SwitchComponent(iBase, cMV, this);
                    break;
                case AUTO_AUTCH:
                    new AutoAutch(iBase, cMV, this);
                    break;
                case TAGS:
                    new TagsComponent(iBase, cMV, this);
                    break;
                case SUBSCRIBE:
                    new SubscribeComponent(iBase, cMV, this);
                    break;
                case SUBSCRIBE_TOPIC:
                    new SubscribeFirebase(iBase, cMV, this);
                    break;
                case CUSTOM:
                    BaseComponent bc = null;
                    try {
                        bc = (BaseComponent) cMV.typeClass.newInstance();
                        bc.initComponent(iBase, cMV, this);
                    } catch (InstantiationException e) {
                        iBase.log("Error custom class for " + nameComponent);
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        iBase.log("Error custom class for " + nameComponent);
                        e.printStackTrace();
                    }
                    break;
            }
            cMV.baseComponent.init();
        }
    }

    public String getParamModel () {
        String st = getParamTitle();
        String sep = "";
        if (st.length() > 0) {
            sep = ",";
        }
        for (ParamComponent vp : listComponents) {
            String paramComponent = getParamApi(vp);
            if (paramComponent.length() > 0) {
                st += sep + paramComponent;
                sep = ",";
            }
        }
        if (itemSetValues != null) {
            for (ItemSetValue sv : itemSetValues) {
                if (sv.type == ItemSetValue.TYPE_SOURCE.PARAM) {
                    if (sv.name != null && sv.name.length() > 0)
                        st += sep + sv.name;
                    sep = ",";
                }
            }
        }
        return st;
    }

    public String getParamApi(ParamComponent mvp) {
        if (mvp != null) {
            String paramResult = "";
            if (mvp.paramModel != null) {
                paramResult = mvp.paramModel.param;
            }
            if (mvp.navigator != null && mvp.navigator.viewHandlers != null) {
                String sep = "";
                if (paramResult.length() > 0) {
                    sep = ",";
                }
                for (ViewHandler vh : mvp.navigator.viewHandlers) {
                    if (vh.paramModel != null) {
                        paramResult += sep + vh.paramModel.param;
                        sep = ",";
                    }
                }
            }
            return paramResult;
        } else {
            return "";
        }
    }

    public String getParamTitle() {
        if (args != null) {
            return args;
        } else {
            return "";
        }
    }
}
