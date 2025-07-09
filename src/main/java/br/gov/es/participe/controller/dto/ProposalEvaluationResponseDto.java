package br.gov.es.participe.controller.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import br.gov.es.participe.model.Evaluates;
import br.gov.es.participe.util.domain.BudgetPlan;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class ProposalEvaluationResponseDto {
    private Long id;
    private Boolean includedInNextYearLOA;
    private String reason;
    private String reasonDetail;
    private String budgetUnitId;
    private String budgetUnitName;
    private String budgetActionId;
    private String budgetActionName;
    private List<BudgetPlan> budgetPlan;
    private String representing;
    private String evaluatorName;
    private Boolean haveCost;
    private String costType;
    private Boolean newRequest;
    private String date;

    public ProposalEvaluationResponseDto() {

    }

    public ProposalEvaluationResponseDto(Evaluates evaluatesRelationship) {
        this.id = evaluatesRelationship.getId();
        this.includedInNextYearLOA = evaluatesRelationship.getIncludedInNextYearLOA();
        if(evaluatesRelationship.getIncludedInNextYearLOA()) {
            this.budgetUnitId = evaluatesRelationship.getBudgetUnitId();
            this.budgetUnitName = evaluatesRelationship.getBudgetUnitName();
            this.budgetActionId = evaluatesRelationship.getBudgetActionId();
            this.budgetActionName = evaluatesRelationship.getBudgetActionName();

            if(
                evaluatesRelationship.getBudgetPlanIds() != null &&
                evaluatesRelationship.getBudgetPlanNames() != null &&
                evaluatesRelationship.getBudgetPlanIds().size() == evaluatesRelationship.getBudgetPlanNames().size()
            ) {
                this.budgetPlan = IntStream.range(0, evaluatesRelationship.getBudgetPlanIds().size())
                                    .mapToObj(i -> new BudgetPlan(
                                            evaluatesRelationship.getBudgetPlanIds().get(i),
                                            evaluatesRelationship.getBudgetPlanNames().get(i)
                                    )).collect(Collectors.toList());
                
               
                
            }

            this.haveCost = evaluatesRelationship.getHaveCost();
            this.costType = evaluatesRelationship.getCostType();
            this.newRequest = evaluatesRelationship.getNewRequest();
        } else {
            this.reason = evaluatesRelationship.getReason();
        }
        this.reasonDetail = evaluatesRelationship.getReasonDetail();
        this.representing = evaluatesRelationship.getRepresenting();
        this.date = formatDate(evaluatesRelationship.getDate());
        this.evaluatorName = evaluatesRelationship.getPerson().getName();
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public Boolean getHaveCost() {
        return haveCost;
    }

    public void setHaveCost(Boolean haveCost) {
        this.haveCost = haveCost;
    }

    public Boolean getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(Boolean newRequest) {
        this.newRequest = newRequest;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIncludedInNextYearLOA() {
        return includedInNextYearLOA;
    }

    public void setIncludedInNextYearLOA(Boolean includedInNextYearLOA) {
        this.includedInNextYearLOA = includedInNextYearLOA;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public  List<BudgetPlan> getBudgetPlan() {
        return budgetPlan;
    }

    public void setBudgetPlan(List<BudgetPlan> budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    public String getRepresenting() {
        return representing;
    }

    public void setRepresenting(String representing) {
        this.representing = representing;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String formatDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        Instant instantDate = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instantDate, ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }



    public String getReasonDetail() {
        return reasonDetail;
    }



    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
    }

}
