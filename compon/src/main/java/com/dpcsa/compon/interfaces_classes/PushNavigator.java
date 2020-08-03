package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

public class PushNavigator {
    public List<PushHandler> pushHandlers = new ArrayList<>();

    public PushNavigator(PushHandler ... handlers) {
        for (PushHandler vh : handlers) {
            pushHandlers.add(vh);
        }
    }
}
