package br.gov.es.participe.model;

import java.util.Date;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.ProposalEvaluationRequestDto;

@RelationshipEntity(type = "EVALUATES")
public class Evaluates extends Entity {
    
    @StartNode
    private Person person;

    @EndNode
    private Comment comment;

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
    private Boolean active;
    private Boolean deleted;

    @DateString
    private Date date;

    public Evaluates() {

    }

    public Evaluates(ProposalEvaluationRequestDto proposalEvaluationRequestDto) {
        this.includedInNextYearLOA = proposalEvaluationRequestDto.getIncludedInNextYearLOA();
        this.budgetUnitId = proposalEvaluationRequestDto.getBudgetUnitId();
        this.budgetUnitName = proposalEvaluationRequestDto.getBudgetUnitName();
        this.budgetActionId = proposalEvaluationRequestDto.getBudgetActionId();
        this.budgetActionName = proposalEvaluationRequestDto.getBudgetActionName();
        this.budgetPlan = proposalEvaluationRequestDto.getBudgetPlan();
        this.reason = proposalEvaluationRequestDto.getReason();
        this.reasonDetail = proposalEvaluationRequestDto.getReasonDetail();
        this.representing = proposalEvaluationRequestDto.getRepresenting();
        this.representingOrgTag = proposalEvaluationRequestDto.getRepresentingOrgTag();
        this.representingOrgName = proposalEvaluationRequestDto.getRepresentingOrgName();
        this.haveCost = proposalEvaluationRequestDto.getHaveCost();
        this.newRequest = proposalEvaluationRequestDto.getNewRequest();
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
    
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getReasonDetail() {
        return reasonDetail;
    }

    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
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
    
    


}
