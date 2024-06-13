package br.gov.es.participe.controller.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import br.gov.es.participe.model.Evaluates;

public class ProposalEvaluationResponseDto {
    private Long id;
    private Boolean includedInNextYearLOA;
    private String reason;
    private String budgetUnitId;
    private String budgetUnitName;
    private String budgetActionId;
    private String budgetActionName;
    private String budgetPlan;
    private String representing;
    private String createdAt;
    private String updatedAt;

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
            this.budgetPlan = evaluatesRelationship.getBudgetPlan();
        } else {
            this.reason = evaluatesRelationship.getReason();
        }
        this.representing = evaluatesRelationship.getRepresenting();
        this.createdAt = formatDate(evaluatesRelationship.getCreatedAt());
        this.updatedAt = evaluatesRelationship.getUpdatedAt() != null ? formatDate(evaluatesRelationship.getUpdatedAt()) : null;
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

    public String getBudgetPlan() {
        return budgetPlan;
    }

    public void setBudgetPlan(String budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    public String getRepresenting() {
        return representing;
    }

    public void setRepresenting(String representing) {
        this.representing = representing;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    private String formatDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        Instant instantDate = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instantDate, ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

}
