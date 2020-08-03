package com.dpcsa.compon.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.dpcsa.compon.components.MenuComponent;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.custom_components.SwipeLayout;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.param.ParamView;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.Injector;

import static com.dpcsa.compon.param.ParamModel.GET;
import static com.dpcsa.compon.param.ParamModel.GET_DB;

public class BaseProviderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private int[] layoutItemId;
    public String fieldType;
    private BaseProvider provider;
    private Context context;
    private WorkWithRecordsAndViews modelToView;
    private String layout;
    private Navigator navigator;
    private BaseComponent baseComponent;
    private ParamView paramView;
    private boolean isClickItem;
    private Visibility[] visibilityManager;
    private LayoutInflater inflater;
    private IBase iBase;
    public int duration = 250;
    protected int[] positionLevel;
    protected View[] imgLevel;
    protected int maxPositionLevel = 3;
    private int saveLevel, savePosition;
    private ComponGlob componGlob;
    private BaseDB baseDB;
    private int levelExp;
    private boolean isExpandedAdapt;
    public  boolean isPaginationStart;
    private int paginationStart;

    public BaseProviderAdapter(BaseComponent baseComponent) {
        context = baseComponent.activity;
        iBase = baseComponent.iBase;
        componGlob = Injector.getComponGlob();
        baseDB = Injector.getBaseDB();
        inflater = LayoutInflater.from(context);
        this.baseComponent = baseComponent;
        this.provider = baseComponent.provider;
        navigator = baseComponent.navigator;
        isClickItem = false;
        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (vh.viewId == 0) {
                    isClickItem = true;
                    break;
                }
            }
        }
        paramView = baseComponent.paramMV.paramView;
        if (paramView != null) {
            layoutItemId = paramView.layoutTypeId;
            fieldType = paramView.fieldType;
        } else {
            layoutItemId = null;
            fieldType = "";
        }
        visibilityManager = paramView.visibilityArray;
        modelToView = new WorkWithRecordsAndViews();
        layout = "item_recycler_" + baseComponent.multiComponent.nameComponent;
        positionLevel = new int[maxPositionLevel];
        imgLevel = new ImageView[maxPositionLevel];
        for (int i = 0; i < positionLevel.length; i++) {
            positionLevel[i] = -1;
        }
        levelExp = -1;
        isExpandedAdapt = paramView.expandedList != null && paramView.expandedList.size() > 0;
        if (baseComponent instanceof RecyclerComponent && baseComponent.paramMV.paramModel.pagination != null) {
            paginationStart = baseComponent.paramMV.paramModel.pagination.paginationPerPage / 2;
            isPaginationStart = false;
        } else {
            paginationStart = 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (fieldType.length() == 0) {
            return 0;
        } else {
            if (fieldType.equals("2")) {
                return position % 2;
            } else {
                Field f = provider.get(position).getField(fieldType);
                if (f == null) {
                    return 0;
                }
                if (f.type == Field.TYPE_STRING) {
                    String sv = (String) f.value;
                    if (sv != null && sv.length() > 0) {
                        char c = sv.charAt(0);
                        if (Character.isDigit(c)) {
                            return Integer.valueOf(sv);
                        } else {
                            return 1;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    if (f.value instanceof Integer) {
                        return (int) f.value;
                    } else {
                        long ll = (Long) f.value;
                        return (int) ll;
                    }
                }
            }
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (layoutItemId == null) {
            int resurceId = context.getResources().getIdentifier(layout, "layout", context.getPackageName());
            if (resurceId == 0) {
                iBase.log("Не найден " + layout);
            }
            view = inflater.inflate(resurceId, parent, false);
        } else {
            view = inflater.inflate(layoutItemId[viewType], parent, false);
        }
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SwipeLayout swipeView;
        if (holder.itemView instanceof SwipeLayout) {
            swipeView = (SwipeLayout) holder.itemView;
            swipeView.setOffset(0);
        }
        holder.itemView.setTag("PP="+position);
        final Record record = (Record) provider.get(position);
        modelToView.RecordToView(record,
                holder.itemView, baseComponent, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        Record rec = (Record) provider.get(pos);
                        if (isExpandedAdapt) {
                            Field fExp = rec.getField("expandedLevel");
                            if (fExp == null) {
                                rec.add(new Field("expandedLevel", Field.TYPE_INTEGER, 0));
                            }
                        }
                        baseComponent.clickItem.onClick(holder, view, pos, rec);
                    }
                });

        if (isClickItem) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    Record rec = (Record) provider.get(pos);
                    if (isExpandedAdapt) {
                        Field fExp = rec.getField("expandedLevel");
                        if (fExp == null) {
                            rec.add(new Field("expandedLevel", Field.TYPE_INTEGER, 0));
                        }
                    }
                    baseComponent.clickItem.onClick(holder, null, pos, (Record) rec);
                }
            });
        }
        if (baseComponent.iCustom != null) {
            baseComponent.iCustom.afterBindViewHolder(baseComponent.paramMV.paramView.viewId, position, record, holder);
        } else if (baseComponent.moreWork != null) {
            baseComponent.moreWork.afterBindViewHolder(baseComponent.paramMV.paramView.viewId, position, record, holder);
        }
        if (baseComponent instanceof MenuComponent) {
            ((MenuComponent) baseComponent).setColor(position, record, holder);
        }
        if (paramView.expandedList != null && paramView.expandedList.size() > 0) {
            ParamView.Expanded exp = paramView.expandedList.get(0);
            View expView = holder.itemView.findViewById(exp.expandedId);
            if (expView != null) {
                ((ItemHolder)holder).setExpandedImg(expView, this);
                ((ItemHolder)holder).setRightRotation(record.getBooleanVisibility("isExpanded"));
            }
        }
        if (paginationStart > 0 && ! isPaginationStart) {
            if (paginationStart == (provider.size() - position)) {
                isPaginationStart = true;
                baseComponent.actual();
            }
        }
    }

    @Override
    public int getItemCount() {
        return provider.getCount();
    }

    public void expanded(ItemHolder hold, int position) {
        Record expandedItem = (Record) provider.get(position);
        levelExp = expandedItem.getInt("expandedLevel");
        boolean exp = expandedItem.getBooleanVisibility("isExpanded");
        if (exp) {     // delete
            expandedItem.setBoolean("isExpanded", false);
            hold.isExpanded = false;
            hold.expandedImg.animate().rotation(0).setDuration(duration).start();
            delete(position, levelExp);
        } else {        // insert
            int posLev = positionLevel[levelExp];
            int countDel = 0;
            if (posLev > -1) {
                imgLevel[levelExp].animate().rotation(0).setDuration(duration).start();
                provider.get(posLev).setBoolean("isExpanded", false);
                countDel = delete(posLev, levelExp);
            }
            if (posLev < position) {
                position = position - countDel;
            }
            hold.isExpanded = true;
            hold.expandedImg.animate().rotation(180).setDuration(duration).start();
            imgLevel[levelExp] = hold.expandedImg;
            expandedItem.setBoolean("isExpanded", true);
            for (int i = levelExp; i < positionLevel.length; i++) {
                if (i == levelExp) {
                    positionLevel[i] = position;
                } else {
                    positionLevel[i] = -1;
                }
            }
            String nameF = paramView.expandedList.get(0).expandNameField;
            if (nameF != null && nameF.length() > 0) {
                Object obj = expandedItem.getValue(nameF);
                if (obj != null) {
                    setLevelData_1(levelExp, position, (ListRecords) obj);
                }
            } else {
                ParamView.Expanded expand;
                if (paramView.expandedList.size() > levelExp) {
                    expand = paramView.expandedList.get(levelExp);
                } else {
                    expand = paramView.expandedList.get(0);
                }
                ParamModel model = expand.expandModel;
                if (model != null) {
                    String[] param;
                    int ik;
                    switch (model.method) {
                        case GET_DB :
                            param = model.param.split(",");
                            ik = param.length;
                            for (int i = 0; i < ik; i++) {
                                String par = param[i];
                                String parValue = expandedItem.getString(par);
                                if (parValue == null) {
                                    parValue = getGlobalParam(par);
                                }
                                if (parValue != null) {
                                    param[i] = parValue;
                                }
                            }
                            saveLevel = levelExp;
                            savePosition = position;
                            baseDB.get(iBase, model, param, listener);
                            break;
                        case GET:
                            saveLevel = levelExp;
                            savePosition = position;
                            componGlob.setParam(expandedItem);
                            new BasePresenter(iBase, model, null, null, listener);
                            break;
                        default: {
                            new BasePresenter(iBase, model, null, null, listener);
                        }
                    }
                }
            }
        }
    }

    private String getGlobalParam(String name) {
        String st = null;
        List<Param> paramV = componGlob.paramValues;
        for (Param par : paramV) {
            if (par.name.equals(name)) {
                st = par.value;
                break;
            }
        }
        return st;
    }

    IPresenterListener listener = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (response == null) return;
            setLevelData_1(saveLevel, savePosition, (ListRecords) response.value);
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {

        }
    };

    private int delete(int position, int level) {
        int position_1 = position + 1;
        int countDel = 0;
        if (position_1 <= provider.size()) {
            Record expandedItem = (Record) provider.get(position_1);
            while (expandedItem.getInt("expandedLevel") > level) {
                provider.remove(position_1);
                countDel++;
                if (position_1 < provider.size()) {
                    expandedItem = (Record) provider.get(position_1);
                } else {
                    break;
                }
            }
            if (countDel > 0) {
                notifyItemRangeRemoved(position_1, countDel);
            }
            for (int i = level; i < positionLevel.length; i++) {
                positionLevel[i] = -1;
            }
        }
        return countDel;
    }



    public void setLevelData_1(int level, int position, ListRecords data) {
        int level_1 = level + 1;
        for (Record rec : data) {
            rec.setBoolean("isExpanded", false);
            rec.setInteger("expandedLevel", level_1);
        }
        setLevelData(data, position);
    }

    public void setLevelData(ListRecords data, int position) {
        if (data != null && data.size() > 0) {
            provider.addAll(position + 1, data);
            notifyItemRangeInserted(position + 1, data.size());
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        public boolean isExpanded;
        View expandedImg;
        BaseProviderAdapter adapter;

        public ItemHolder(View itemView) {
            super(itemView);
        }

        public void setExpandedImg(View img, final BaseProviderAdapter adapter) {
            expandedImg = img;
            this.adapter = adapter;
            expandedImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.expanded(ItemHolder.this, getAdapterPosition());
                }
            });
        }

        public void setRightRotation(boolean expanded) {
            if (isExpanded != expanded) {
                if (expanded) {
                    expandedImg.animate().rotation(180).setDuration(adapter.duration).start();
                } else {
                    expandedImg.animate().rotation(0).setDuration(adapter.duration).start();
                }
            }
        }
    }
}
