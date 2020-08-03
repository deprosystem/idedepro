package com.dpcsa.compon.interfaces_classes;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ToolMenu {

    public enum STATUS_VIEW {ALWAYS, EMPTY_STACK, NO_EMPTY_STACK};
    public enum STATUS_START {VIEW, GONE};

    public List<ItemTool> listItem = new ArrayList<>();

    public ToolMenu item(int viewId, Navigator navigator, STATUS_VIEW status, STATUS_START statusStart) {
        listItem.add(new ItemTool(viewId, navigator, status, statusStart));
        return this;
    }

    public class ItemTool {
        public int viewId;
        public Navigator navigator;
        public STATUS_VIEW statusView;
        public STATUS_START statusStart;
        public View view;

        public ItemTool(int viewId, Navigator navigator, STATUS_VIEW status, STATUS_START statusStart) {
            this.viewId = viewId;
            this.navigator = navigator;
            this.statusStart = statusStart;
            statusView = status;
            view = null;
        }
    }
}
