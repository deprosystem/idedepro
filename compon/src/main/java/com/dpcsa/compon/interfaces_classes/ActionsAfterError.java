package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

public class ActionsAfterError {
    public List<ViewHandler> viewHandlers = new ArrayList<>();
    public Boolean viewErrorDialog;

    public ActionsAfterError (Boolean viewErrorDialog, ViewHandler ... handlers) {
        this.viewErrorDialog = viewErrorDialog;
        for (ViewHandler vh : handlers) {
            viewHandlers.add(vh);
        }
    }

    public ActionsAfterError (ViewHandler ... handlers) {
        this.viewErrorDialog = true;
        for (ViewHandler vh : handlers) {
            viewHandlers.add(vh);
        }
    }
}
