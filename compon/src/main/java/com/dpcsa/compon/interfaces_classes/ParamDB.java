package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

public class ParamDB {
    public List<DescriptTableDB> listTables;

    public ParamDB() {
        listTables = new ArrayList<>();
    }

    public ParamDB(String name, int version) {
        listTables = new ArrayList<>();
        nameDB = name;
        versionDB = version;
    }

    public String nameDB;
    public int versionDB;
    public void addTable(String nameTables, String description) {
        listTables.add(new DescriptTableDB(nameTables, description));
    }

    public void addTable(String nameTables, String description, String indexName, String indexColumn) {
        listTables.add(new DescriptTableDB(nameTables, description, indexName, indexColumn));
    }
}
