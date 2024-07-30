package br.gov.es.participe.controller.dto;

import java.util.HashMap;
import java.util.Map;

public class ProposalEvaluationJasperParamDto {

    private Map<String, Object> proposalEvaluationJasperMap = new HashMap<>();
    
    
    public ProposalEvaluationJasperParamDto(Boolean evaluationStatus, Long localityId, Long planItemAreaId,
    Long planItemId, String organizationGuid, Boolean loaIncluded, String commentText, Long conferenceId) {
        proposalEvaluationJasperMap.put("evaluationStatus", evaluationStatus);
        proposalEvaluationJasperMap.put("localityId", localityId);
        proposalEvaluationJasperMap.put("planItemAreaId", planItemAreaId);
        proposalEvaluationJasperMap.put("planItemId", planItemId);
        proposalEvaluationJasperMap.put("organizationGuid", organizationGuid != null ? "'" + organizationGuid + "'" : null);
        proposalEvaluationJasperMap.put("loaIncluded", loaIncluded);
        proposalEvaluationJasperMap.put("commentText", commentText != null ? "'" + commentText + "'" : null);
        proposalEvaluationJasperMap.put("conferenceId", conferenceId);
    }
    
    public Map<String, Object> getProposalEvaluationJasperMap() {
        return proposalEvaluationJasperMap;
    }
    
    public void setProposalEvaluationJasperMap(Map<String, Object> proposalEvaluationJasperMap) {
        this.proposalEvaluationJasperMap = proposalEvaluationJasperMap;
    }
}
