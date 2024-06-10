package br.gov.es.participe.controller.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ProposalEvaluationResultDto {
    
    private Long id;
    private Boolean evaluationStatus; // true = 'Avaliado'; false = 'Não Avaliado'
    private String localityName; // Microrregiao
    private String planItemName; // Desafio / Categoria Orçamentaria
    private String planItemAreaName; // Area Tematica
    private String description; // Texto da proposta
    private List<String> evaluatorOrgsNameList;
    private String evaluatorName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getEvaluatorOrgsNameList() {
        return evaluatorOrgsNameList;
    }

    public void setEvaluatorOrgsNameList(List<String> evaluatorOrgsNameList) {
        this.evaluatorOrgsNameList = evaluatorOrgsNameList;
    }
    
    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }
}
