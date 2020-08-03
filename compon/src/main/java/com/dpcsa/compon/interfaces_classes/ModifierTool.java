package com.dpcsa.compon.interfaces_classes;

public class ModifierTool {
    public enum TYPE_MODIF {ADD, VISIBLE, UN_VISIBLE};

    public int[] ind;
    public ToolMenu toolMenu;
    public TYPE_MODIF type;

    public ModifierTool(TYPE_MODIF type, int ... args) {
        ind = args;
        this.type = type;
    }

    public ModifierTool(ToolMenu toolMenu) {
        type = TYPE_MODIF.ADD;
        this.toolMenu = toolMenu;
    }
}
