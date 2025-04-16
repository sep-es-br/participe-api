package br.gov.es.participe.model;

import java.util.*;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.io.*;

// @NodeEntity
@RelationshipEntity(type = "EVALUATES")
public class ProposalEvaluation extends Entity { //extends Entity implements Serializable {

    @StartNode
    private Person person;

    @EndNode
    private Comment comment;

    // @Property
    private String status;
    private Boolean includedInNextYearLOA;
    private Optional<String> reason;

    public ProposalEvaluation() {
        this.status = "Nâo Avaliado";
        this.includedInNextYearLOA = true;
    }

    // Construtor com argumentos
    // private ProposalEvaluation() {

    // }

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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Boolean getIncludedInNextYearLOA() {
        return includedInNextYearLOA;
    }
    public void setIncludedInNextYearLOA(Boolean includedInNextYearLOA) {
        this.includedInNextYearLOA = includedInNextYearLOA;
    }
    public Optional<String> getReason() {
        return reason;
    }
    public void setReason(Optional<String> reason) {
        this.reason = reason;
    }

    // private String budgetUnitId;
    // private String budgetUnitName;
    // private String budgetActionId;
    // private String budgetActionName;
    // private String budgetPlanId;
    // private String budgetPlanName;

    

}



// @Relationship(type = "GENERATED_BY", direction = Relationship.OUTGOING)
//     private Comment comment;

//     /* Lista de organizações avaliadoras */
//     // @Relationship(type = "EVALUATED_BY", direction = Relationship.OUTGOING)
//     // private List<Organization> organizationList;

//     private Conference conference;
//     private Locality locality;
//     private PlanItem planItem;
//     private PlanItem planItemArea;

//     private String status;
//     private Boolean includedInNextYearLOA;
//     private Optional<String> reason;

//     /* Caso includedInNextYearLOA == true */
//     // private Optional<BudgetUnit> budgetUnit;
//     // private Optional<BudgetAction> budgetAction;
//     // private Optional<BudgetPlan> budgetPlan;

//     public ProposalEvaluation() {
//     }

//     // public ProposalEvaluation(ProposalEvaluation propEval) {

//     // }

//     public ProposalEvaluation(Comment comment) {
//         this.comment = comment;
//         this.conference = comment.getConference();
//         this.locality = comment.getLocality();
//         this.planItem = comment.getPlanItem();
//         this.planItemArea = comment.getPlanItem().getParent();
//         // this.planItemArea = this.planItem.getParent();
//         this.status = "Não Avaliado";
//         this.includedInNextYearLOA = true;
//     }

//     public Comment getComment() {
//         return comment;
//     }

//     public void setComment(Comment comment) {
//         this.comment = comment;
//     }

//     public Conference getConference() {
//         return conference;
//     }

//     public void setConference(Conference conference) {
//         this.conference = conference;
//     }

//     public Locality getLocality() {
//         return locality;
//     }

//     public void setLocality(Locality locality) {
//         this.locality = locality;
//     }

//     public PlanItem getPlanItem() {
//         return planItem;
//     }

//     public void setPlanItem(PlanItem planItem) {
//         this.planItem = planItem;
//     }

//     public PlanItem getPlanItemArea() {
//         return planItemArea;
//     }

//     public void setPlanItemArea(PlanItem planItemArea) {
//         this.planItemArea = planItemArea;
//     }

//     public String getStatus() {
//         return status;
//     }

//     public void setStatus(String status) {
//         this.status = status;
//     }

//     public Boolean getIncludedInNextYearLOA() {
//         return includedInNextYearLOA;
//     }

//     public void setIncludedInNextYearLOA(Boolean includedInNextYearLOA) {
//         this.includedInNextYearLOA = includedInNextYearLOA;
//     }

//     public Optional<String> getReason() {
//         return reason;
//     }

//     public void setReason(Optional<String> reason) {
//         this.reason = reason;
//     }
