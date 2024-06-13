package br.gov.es.participe.controller.dto;

import java.util.List;
import java.util.ArrayList;

public class BudgetOptionsDto {
    
    private String budgetUnitId;
    private String budgetUnitName;
    private List<BudgetActionDto> budgetActions;

    public BudgetOptionsDto(String budgetUnitId, String budgetUnitName){
        this.budgetUnitId = budgetUnitId;
        this.budgetUnitName = budgetUnitName;
        this.budgetActions = new ArrayList<BudgetActionDto>();
    }

    public String getBudgetUnitId() {
        return budgetUnitId;
    }

    public void setBudgetUnitId(String budgetUnitId) {
        this.budgetUnitId = budgetUnitId;
    }

    public String getBudgetUnitName() {
        return budgetUnitName;
    }

    public void setBudgetUnitName(String budgetUnitName) {
        this.budgetUnitName = budgetUnitName;
    }

    public List<BudgetActionDto> getBudgetActions() {
        return budgetActions;
    }

    public void setBudgetActions(List<BudgetActionDto> budgetActions) {
        this.budgetActions = budgetActions;
    }

    public void buildBudgetActionDto(String budgetActionId, String budgetActionName) {
        this.budgetActions.add(new BudgetActionDto(budgetActionId, budgetActionName));
    }

    public class BudgetActionDto {
        private String budgetActionId;
        private String budgetActionName;
        
        public BudgetActionDto(String budgetActionId, String budgetActionName){
            this.budgetActionId = budgetActionId;
            this.budgetActionName = budgetActionName;
        }
    
        public String getBudgetActionId() {
            return budgetActionId;
        }
    
        public void setBudgetActionId(String budgetActionId) {
            this.budgetActionId = budgetActionId;
        }
    
        public String getBudgetActionName() {
            return budgetActionName;
        }
    
        public void setBudgetActionName(String budgetActionName) {
            this.budgetActionName = budgetActionName;
        }
     
    }

}


