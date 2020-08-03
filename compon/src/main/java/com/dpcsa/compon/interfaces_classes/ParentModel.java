package com.dpcsa.compon.interfaces_classes;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.json_simple.Field;

import java.util.ArrayList;
import java.util.List;

public class ParentModel {
    public String nameParentModel;
    public Field field;
    public List<BaseComponent> componentList;

    public ParentModel(String nameParentModel) {
        this.nameParentModel = nameParentModel;
        field = null;
        componentList = new ArrayList<>();
    }

    public ParentModel(String nameParentModel, Field field) {
        this.nameParentModel = nameParentModel;
        this.field = field;
        componentList = new ArrayList<>();
    }
}
