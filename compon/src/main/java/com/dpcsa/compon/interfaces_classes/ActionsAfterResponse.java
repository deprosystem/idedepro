package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

import com.dpcsa.compon.param.ParamModel;

import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE;

public class ActionsAfterResponse {
    public List<ViewHandler> viewHandlers = new ArrayList<>();

    public ActionsAfterResponse (ViewHandler ... handlers) {
        for (ViewHandler vh : handlers) {
            viewHandlers.add(vh);
        }
    }
}
