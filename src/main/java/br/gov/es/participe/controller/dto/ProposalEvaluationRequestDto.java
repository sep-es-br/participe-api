package br.gov.es.participe.controller.dto;

public class ProposalEvaluationRequestDto {
    private Long personId;
    private Long proposalId;
    private Boolean includedInNextYearLOA;
    private String reason;
    private String reasonDetail;
    private String budgetUnitId;
    private String budgetUnitName;
    private String budgetActionId;
    private String budgetActionName;
    private String budgetPlan;
    private String representing;
    private String representingOrgTag;
    private String representingOrgName;
    private Boolean haveCost;
    private Boolean newRequest;

    public ProposalEvaluationRequestDto() {

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
    
    

    public String getRepresentingOrgTag() {
        return representingOrgTag;
    }

    public void setRepresentingOrgTag(String representingOrgTag) {
        this.representingOrgTag = representingOrgTag;
    }

    public String getRepresentingOrgName() {
        return representingOrgName;
    }

    public void setRepresentingOrgName(String representingOrgName) {
        this.representingOrgName = representingOrgName;
    }
    
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
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

    public String getReasonDetail() {
        return reasonDetail;
    }

    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
    }
}
