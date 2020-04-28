package com.example.test.project.model;

import com.example.test.project.entity.Vacation;

import java.util.List;

public class VacationListPage {
    boolean hasPrev;
    boolean hasNext;
    List<Vacation> vacations;
    int pageNumber;
    int rowsPerPage;
    boolean ascending;
    String sortByColumn;

    public VacationListPage(boolean hasPrev, boolean hasNext, List<Vacation> vacations, int pageNumber, int rowsPerPage, boolean ascending, String column) {
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.vacations = vacations;
        this.pageNumber = pageNumber;
        this.rowsPerPage = rowsPerPage;
        this.ascending = ascending;
        this.sortByColumn = column;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getSortByColumn() {
        return sortByColumn;
    }

    public void setSortByColumn(String sortByColumn) {
        this.sortByColumn = sortByColumn;
    }
}
