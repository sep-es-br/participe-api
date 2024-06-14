package br.gov.es.participe.controller.dto;

import java.util.Map;

public class EvaluatorsNamesResponseDto {
    private Map<String,String> sectionsGuidNameMap;
    private Map<String,String> rolesGuidNameMap;
    
    public EvaluatorsNamesResponseDto() {

    }

    public EvaluatorsNamesResponseDto(Map<String, String> sectionsMap, Map<String, String> rolesMap) {
        this.sectionsGuidNameMap = sectionsMap;
        this.rolesGuidNameMap = rolesMap;
    }
    
    public Map<String, String> getSectionsGuidNameMap() {
        return sectionsGuidNameMap;
    }
    public void setSectionsGuidNameMap(Map<String, String> sectionsGuidNameMap) {
        this.sectionsGuidNameMap = sectionsGuidNameMap;
    }
    public Map<String, String> getRolesGuidNameMap() {
        return rolesGuidNameMap;
    }
    public void setRolesGuidNameMap(Map<String, String> rolesGuidNameMap) {
        this.rolesGuidNameMap = rolesGuidNameMap;
    }
}
