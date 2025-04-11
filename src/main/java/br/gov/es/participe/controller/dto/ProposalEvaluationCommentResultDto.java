package br.gov.es.participe.controller.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ProposalEvaluationCommentResultDto {
    
    private Long commentId;
    private Boolean evaluationStatus;
    private String localityName;
    private String planItemName;
    private String planItemAreaName;
    private String description;
    private List<EvaluatorOrgsNameAndLoaIncluded> evaluatorOrgsNameAndLoaIncludedList;
    private String evaluatorName;

    public class EvaluatorOrgsNameAndLoaIncluded {
        private String evaluatorOrgsName;
        private Boolean loaIncluded;

        public String getEvaluatorOrgsName() {
            return evaluatorOrgsName;
        }
        public void setEvaluatorOrgsName(String evaluatorOrgsName) {
            this.evaluatorOrgsName = evaluatorOrgsName;
        }
        public Boolean getLoaIncluded() {
            return loaIncluded;
        }
        public void setLoaIncluded(Boolean loaIncluded) {
            this.loaIncluded = loaIncluded;
        }

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

    public List<EvaluatorOrgsNameAndLoaIncluded> getEvaluatorOrgsNameAndLoaIncludedList() {
        return evaluatorOrgsNameAndLoaIncludedList;
    }

    public void setEvaluatorOrgsNameAndLoaIncludedList(
            List<EvaluatorOrgsNameAndLoaIncluded> evaluatorOrgsNameAndLoaIncludedList) {
        this.evaluatorOrgsNameAndLoaIncludedList = evaluatorOrgsNameAndLoaIncludedList;
    }
    
    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

}
