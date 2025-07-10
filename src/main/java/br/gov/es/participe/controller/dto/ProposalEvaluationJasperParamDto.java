package br.gov.es.participe.controller.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProposalEvaluationJasperParamDto {

    private Map<String, Object> proposalEvaluationJasperMap = new HashMap<>();
    
    
    public ProposalEvaluationJasperParamDto(Boolean evaluationStatus, Long localityId, Long planItemAreaId,
    Long planItemId, List<String> organizationGuid, Boolean approved, String commentText, Long conferenceId, String localityTypeName, String structureItemName) {
        proposalEvaluationJasperMap.put("evaluationStatus", evaluationStatus);
        proposalEvaluationJasperMap.put("localityId", localityId);
        proposalEvaluationJasperMap.put("planItemAreaId", planItemAreaId);
        proposalEvaluationJasperMap.put("planItemId", planItemId);
        List<String> formattedOrganizationGuid = (organizationGuid != null && !organizationGuid.isEmpty())
        ? organizationGuid.stream()
                          .map(guid -> "'" + guid + "'")
                          .collect(Collectors.toList())
        : Collections.emptyList();
        proposalEvaluationJasperMap.put("organizationGuid", formattedOrganizationGuid);
        proposalEvaluationJasperMap.put("approved", approved);
        proposalEvaluationJasperMap.put("commentText", commentText != null ? "'" + commentText + "'" : null);
        proposalEvaluationJasperMap.put("conferenceId", conferenceId);
        proposalEvaluationJasperMap.put("localityTypeName", localityTypeName);
        proposalEvaluationJasperMap.put("structureItemName", structureItemName);
    }
    
    public Map<String, Object> getProposalEvaluationJasperMap() {
        return proposalEvaluationJasperMap;
    }
    
    public void setProposalEvaluationJasperMap(Map<String, Object> proposalEvaluationJasperMap) {
        this.proposalEvaluationJasperMap = proposalEvaluationJasperMap;
    }
}
