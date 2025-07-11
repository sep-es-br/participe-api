package br.gov.es.participe.controller.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ProposalEvaluationCommentResultDto {
    
    private Long commentId;
    private Boolean evaluationStatus;
    private String localityName;
    private String microrregionName;
    private String planItemName;
    private String planItemAreaName;
    private String description;
    private List<EvaluatorOrgsNameAndApproved> evaluatorOrgsNameAndApprovedList;
    private String evaluatorName;

    public class EvaluatorOrgsNameAndApproved {
        private String evaluatorOrgsName;
        private Boolean approved;

        public String getEvaluatorOrgsName() {
            return evaluatorOrgsName;
        }
        public void setEvaluatorOrgsName(String evaluatorOrgsName) {
            this.evaluatorOrgsName = evaluatorOrgsName;
        }
        public Boolean getApproved() {
            return approved;
        }
        public void setApproved(Boolean approved) {
            this.approved = approved;
        }

    }

    public String getMicrorregionName() {
        return microrregionName;
    }

    public void setMicrorregionName(String microrregionName) {
        this.microrregionName = microrregionName;
    }

    
    
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Boolean getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(Boolean evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getPlanItemName() {
        return planItemName;
    }

    public void setPlanItemName(String planItemName) {
        this.planItemName = planItemName;
    }

    public String getPlanItemAreaName() {
        return planItemAreaName;
    }

    public void setPlanItemAreaName(String planItemAreaName) {
        this.planItemAreaName = planItemAreaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EvaluatorOrgsNameAndApproved> getEvaluatorOrgsNameAndApprovedList() {
        return evaluatorOrgsNameAndApprovedList;
    }

    public void setEvaluatorOrgsNameAndApprovedList(
            List<EvaluatorOrgsNameAndApproved> evaluatorOrgsNameAndApprovedList) {
        this.evaluatorOrgsNameAndApprovedList = evaluatorOrgsNameAndApprovedList;
    }
    
    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

}
