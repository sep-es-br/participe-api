/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.controller.dto.integration;

import java.util.List;

/**
 *
 * @author gean.carneiro
 */
public class SpoProposalListRequestDto {
    
    private List<String> budgetUnitCodes;
    private String planItemName;
    private String textFilter;
    private List<String> syncedIds;
    
    private int pageNumber;
    private int pageSize;
    

    public String getPlanItemName() {
        return planItemName;
    }

    public void setPlanItemName(String planItemName) {
        this.planItemName = planItemName;
    }

    public List<String> getBudgetUnitCodes() {
        return budgetUnitCodes;
    }

    public void setBudgetUnitCodes(List<String> budgetUnitCodes) {
        this.budgetUnitCodes = budgetUnitCodes;
    }

    public String getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    public List<String> getSyncedIds() {
        return syncedIds;
    }

    public void setSyncedIds(List<String> syncedIds) {
        this.syncedIds = syncedIds;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    
    
}
