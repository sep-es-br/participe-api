/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.controller.dto.integration;

/**
 *
 * @author gean.carneiro
 */
public class SpoProposalListRequestDto {
    
    private String budgetUnitCode;
    private Integer year;
    private Long planItemId;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getPlanItemId() {
        return planItemId;
    }

    public void setPlanItemId(Long planItemId) {
        this.planItemId = planItemId;
    }
    
    public String getBudgetUnitCode() {
        return budgetUnitCode;
    }

    public void setBudgetUnitCode(String budgetUnitCode) {
        this.budgetUnitCode = budgetUnitCode;
    }
    
    
    
}
