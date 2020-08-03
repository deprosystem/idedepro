package com.dpcsa.compon.interfaces_classes;

public class DescriptTableDB {
    public DescriptTableDB(String nameTable, String descriptTable) {
        this.nameTable = nameTable;
        this.descriptTable = descriptTable;
    }

    public DescriptTableDB(String nameTable, String descriptTable, String indexName, String indexColumn) {
        this.nameTable = nameTable;
        this.descriptTable = descriptTable;
        this.indexName = indexName;
        this.indexColumn = indexColumn;
    }

    public String nameTable;
    public String descriptTable;
    public String indexName;
    public String indexColumn;
}
