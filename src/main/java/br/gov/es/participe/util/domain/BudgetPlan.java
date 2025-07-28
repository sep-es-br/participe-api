/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain;

/**
 *
 * @author Cliente
 */
public class BudgetPlan {
    
    private String budgetPlanId;
    private String budgetPlanName;

    public BudgetPlan() {
    }

    public BudgetPlan(String budgetPlanId, String budgetPlanName) {
        this.budgetPlanId = budgetPlanId;
        this.budgetPlanName = budgetPlanName;
    }
    
    

    public String getBudgetPlanId() {
        return budgetPlanId;
    }

    public void setBudgetPlanId(String budgetPlanId) {
        this.budgetPlanId = budgetPlanId;
    }

    public String getBudgetPlanName() {
        return budgetPlanName;
    }

    public void setBudgetPlanName(String budgetPlanName) {
        this.budgetPlanName = budgetPlanName;
    }
    
    
    
}
