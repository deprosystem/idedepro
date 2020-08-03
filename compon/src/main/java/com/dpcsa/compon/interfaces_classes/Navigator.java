package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    public List<ViewHandler> viewHandlers = new ArrayList<>();

    public Navigator(ViewHandler ... handlers) {
        for (ViewHandler vh : handlers) {
            viewHandlers.add(vh);
        }
    }

    public Navigator add(ViewHandler handler) {
        viewHandlers.add(handler);
        return this;
    }

}
