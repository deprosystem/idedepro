package com.dpcsa.compon.interfaces_classes;

public class Pagination {

    public int paginationNumberPage;
    public boolean isEnd;
    public int paginationPerPage;
    public String paginationNameParamPerPage;
    public String paginationNameParamNumberPage;
    public Pagination() {
        paginationNumberPage = 0;
        isEnd = false;
    }
}
